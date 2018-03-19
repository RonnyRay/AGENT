package com.ethink.agent.task.executor.server;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ethink.agent.Config;
import com.ethink.agent.SerialNumber;
import com.ethink.agent.annotation.TaskExecutors;
import com.ethink.agent.decode.HttpEncoder;
import com.ethink.agent.decode.RdpEncoder;
import com.ethink.agent.net.RDPMessageSend;
import com.ethink.agent.net.SendMessage;
import com.ethink.agent.task.bean.RDPTask;
import com.ethink.agent.task.bean.ServerTask;
import com.ethink.agent.task.bean.Task;
import com.ethink.agent.task.executor.TaskExecutor;
import com.ethink.agent.task.mng.TaskStatus;
import com.ethink.agent.util.BeanUtil;
import com.ethink.agent.util.FetchUrl;
import com.ethink.agent.util.TimeUtil;
import com.ethink.agent.util.Util;

@TaskExecutors(value = "adUpdateAct")
public class ADUpdateExecutor implements TaskExecutor {

	String taskServer = Config.get(Config.TASK_SERVER);

	//需要发送的字段
	String filterField[] = {"tasktype","planid","taskid","termid","fileid","taskstatus","percent","extradescription"};
	
	private final static Logger log = LoggerFactory.getLogger(ADUpdateExecutor.class);
	
	//
	String url = taskServer + "/task/atmcUpdateAct.ebf";
	// 文件缓存地址
	String cachePath = "d:/local_download/";

	public ADUpdateExecutor() {
		// 监听rdp启动事件
		//EventHandler.listen(Event.TYPE_RDPSTARTUP, rdpStartListener);
	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		Task task = (Task) context.getJobDetail().getJobDataMap().get("taskData");
		// 判断task对象是否合法
		if (isADUpdate(task)) {
			// 判断是否agent启动事件
			ServerTask serverTask = (ServerTask) task;
			Object agentStart = context.getJobDetail().getJobDataMap().get("agentStart");
			if(agentStart!=null){
				log.info("广告升级重启后升级操作，正在执行--------------");
				rdpStartup(serverTask,context);
				return;
			}
			log.info("广告升级升级开始，开始时间："+TimeUtil.getNowDateTime()+",任务报文："+task.getTaskData());
			// fileid=FILE SERVER返回文件ID
			serverTask.setFileid(serverTask.getFileURL());
				try {
					// 上送任务状态
					uploadMess(setTaskStatus(serverTask, "3", "0,", "开始执行"));
					if (serverTask.getFileURL() == null || "".equals(serverTask.getFileURL())) {
						log.error("下载文件地址为空，升级失败，请检查下传报文文件路径");
						throw new JobExecutionException("下载文件地址为空，升级失败，请检查下传报文文件路径");
					}
					// 下载atmc更新包
						FetchUrl.fecthFile(serverTask.getFileURL(), getFielName(serverTask));

					// 校验md5
					if (!(checkFileMD5(serverTask))) {
						log.error("文件校验失败");
						throw new JobExecutionException("文件校验失败");
					}
					// 更新atmc
					try {
						FetchUrl.copyFolder(new File(cachePath+getFielName(serverTask)), new File("c:/"));
						log.info("广告升级更新文件成功");
					} catch (Exception e) {
						log.error("下载成功，拷贝失败，不在重试");
						uploadMess(setTaskStatus(serverTask, "6", "100,", "下载成功，拷贝失败"));
						throw new JobExecutionException("下载成功，拷贝失败");
					}
					
					// 上送任务状态
					// uploadMess(setTaskStatus(serverTask, "6", "100,",
					// "执行成功"));
					// 通知rdp重启
					RDPTask rdptask = new RDPTask();
					rdptask.setTRNCODE("301003");
					rdptask.setTRNEJ(SerialNumber.getSerialNumber());
					rdptask.setCTIME(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
					log.info("开始向rdp发送升级命令");
					boolean flag = true;
					
					while(flag){
						try{
							String result = RDPMessageSend.send(new RdpEncoder().encode(rdptask));	
							log.info("已通知rdp重启");
						}catch (Exception e){
							try {
								log.info("广告升级命令发送失败，1分钟后再次连接rdp");
								Thread.sleep(60000);
							} catch (InterruptedException e1) {
								log.error(null, e);
							}
							if(isTimeout(serverTask.getExectime(), serverTask.getValid())){
								flag = false;
								log.info("广告升级命令发送失败，已超时，不再发送");
								throw new JobExecutionException("广告升级命令发送失败，已超时");
							}
							continue;
						}
						break;
					}
				} catch (Exception e) {
					log.error("广告升级失败", e);
					// 清除缓存文件
					clearCacheFile(serverTask);
					throw new JobExecutionException("广告升级失败", e);
				}
				clearCacheFile(serverTask);

				//返回任务状态等数据
				Map<String, Object> mapResult = new HashMap<String, Object>();
				mapResult.put("taskStatus", TaskStatus.extra);
				context.setResult(mapResult);

		}

	}

	private void clearCacheFile(ServerTask serverTask) {
		File cacheFile = new File(getFilePath(serverTask));
		if (cacheFile.exists()) {
			cacheFile.delete();
		}
	}

	private boolean checkFileMD5(ServerTask serverTask) {
		// 校验MD5
		String fileMD5 = Util.md5(new File(getFilePath(serverTask)));
		if (serverTask.getFileMD5().equals(fileMD5)) {
			return true;
		}
		return false;
	}

	private ServerTask setTaskStatus(ServerTask serverTask, String taskstatus, String percent,
			String Extradescription) {
		serverTask.setTaskstatus(taskstatus);
		serverTask.setPercent(percent);
		serverTask.setExtradescription(Extradescription);
		return serverTask;
	}

	/**
	 * 上送报文
	 * 
	 * @param serverTask
	 */
	private void uploadMess(ServerTask serverTask) {
		try {
			//过滤字段
			ServerTask serverTaskFilter = (ServerTask) BeanUtil.filterBean(serverTask, filterField);
			// 将Task转为字符串
			String taskStr = new HttpEncoder().stringEncoder(serverTaskFilter);
			log.info("上传报文,url:"+url+"  报文内容："+taskStr);
			String response = SendMessage.send(url, taskStr, SendMessage.TYPE_HTTP_GET);
		} catch (Exception e) {
			log.error("上传任务失败", e);
			// 保存任务报文

		}
	}

	/**
	 * 判断是否是rdp升级
	 * 
	 * @param task
	 * @return
	 */
	public boolean isADUpdate(Task task) {
		if (task != null && task instanceof ServerTask) {
			ServerTask serverTask = (ServerTask) task;
			if ("adUpdateAct".equals(serverTask.getTasktype())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 处理rdpstart事件
	 * 
	 * @param event
	 * @throws Exception
	 */
	protected void rdpStartup(ServerTask serverTask,JobExecutionContext context){
		
		Map<String, Object> mapResult = new HashMap<String, Object>();
			// 判断文件是否有效
			if (isSuccessUpdate(serverTask)) {
				// 任务完成上传报
				uploadMess(setTaskStatus(serverTask, "6", "100,", "执行成功"));
				mapResult.put("taskStatus", TaskStatus.executed);
				log.info("RDP重启后升级完成,升级成功");
			} else {
				mapResult.put("taskStatus", TaskStatus.error);
				log.info("RDP重启后升级完成,升级失败");
			}
			// 完成更新操作
			//返回任务状态等数据
			context.setResult(mapResult);

	}

	private boolean isSuccessUpdate(ServerTask serverTask) {
		Boolean flag = true;
		// 判断rdp版本
		if (Config.get(Config.NEW_ADV_VER).equals(Config.get(Config.OLD_ADV_VER))) {
			log.info("广告新版本与旧版本相同，升级失败");
			return false;
		}
		log.info("*******************广告版本符合***********************");
		// 判断时间
		try {
			if (isTimeout(serverTask.getExectime(), serverTask.getValid())) {
				log.info("广告版本升级时间过长，最长升级时间为："+serverTask.getValid());
				return false;
			}
		} catch (Exception e) {
			log.error("升级校验失败，时间格式错误");
			return false;
		}
		return flag;
	}

	/**
	 * 判断是否超时，超时返回true
	 * @param executime(开始执行时间)
	 * @param valid(任务有效期，分钟)
	 * @return
	 * @throws Exception 
	 */
	private boolean isTimeout(String executime,String valid) throws Exception{
		Date starttime = TimeUtil.StringToTime(executime, "yyyy-MM-dd HH:mm:ss.SSS");
		
		Date datenow = new Date();
		// 执行时间
		long interval = datenow.getTime() - starttime.getTime();
		if (Long.parseLong(valid) * 1000 * 60 < interval) {
			return true;
		}
		return false;
	}
	
	private String getFilePath(ServerTask serverTask) {

		return cachePath + getFielName(serverTask);
	}

	private String getFielName(ServerTask serverTask) {
		// http://10.1.91.11:8080/FileServer/File\File\Other\test.exe
		String fileurl = serverTask.getFileURL();
		String filename = fileurl.substring(fileurl.lastIndexOf("/")+1).trim();
		log.info("文件地址为："+fileurl+"文件名为"+filename);
		return filename;
	}

}

