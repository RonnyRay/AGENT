package com.ethink.agent.task.executor.server;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeroturnaround.zip.NameMapper;
import org.zeroturnaround.zip.ZipUtil;
import com.ethink.agent.Config;
import com.ethink.agent.annotation.TaskExecutors;
import com.ethink.agent.decode.HttpEncoder;
import com.ethink.agent.net.SendMessage;
import com.ethink.agent.task.bean.ServerTask;
import com.ethink.agent.task.bean.Task;
import com.ethink.agent.task.executor.TaskExecutor;
import com.ethink.agent.task.mng.TaskStatus;
import com.ethink.agent.util.BeanUtil;
import com.ethink.agent.util.FetchUrl;
import com.ethink.agent.util.TimeUtil;
import com.ethink.agent.util.Util;

/**
 * @类描述 agent升级执行器
 * @创建时间 2017年10月26日
 * @author wangluliang
 */
@TaskExecutors(value = "agentUpdateAct")
public class AgentUpdateExecutor implements TaskExecutor {

	String taskServer = Config.get(Config.TASK_SERVER);

	// 需要发送的字段
	String filterField[] = { "tasktype", "planid", "taskid", "termid", "fileid", "taskstatus", "percent",
			"extradescription" };

	private final static Logger log = LoggerFactory.getLogger(AgentUpdateExecutor.class);

	//
	String url = taskServer + "/task/adUpdateAct.ebf";
	// 文件缓存地址
	String cachePath = "d:/local_download/";

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		String agentPath = Config.get(Config.AGENT_PATH);
		
		Task task = (Task) context.getJobDetail().getJobDataMap().get("taskData");
		// 判断任务是否符合
		if (isAgentUpdate(task)) {
			ServerTask serverTask = (ServerTask) task;
			// 是否是agent重启后操作
			Object agentStart = context.getJobDetail().getJobDataMap().get("agentStart");
			if (agentStart != null) {
				log.info("agent重启后升级操作，正在执行--------------");
				agentRestart(serverTask, context);
				return;
			}
			log.info("Agent升级开始，开始时间：" + TimeUtil.getNowDateTime() + ",任务报文：" + task.getTaskData());

			serverTask.setFileid(serverTask.getFileURL());
			try {
				// 上送任务状态
				uploadMess(setTaskStatus(serverTask, "4", "0,", "开始执行"));
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

				// 上送任务状态
				// uploadMess(setTaskStatus(serverTask, "4", "40,","执行成功"));
				// 更新atmc
				// try {
				// FetchUrl.copyFolder(new
				// File(cachePath+getFielName(serverTask)), new
				// File(FileUpload));
				// log.info("Agent更新文件成功");
				// } catch (Exception e) {
				// log.error("下载成功，拷贝失败，不在重试");
				// uploadMess(setTaskStatus(serverTask, "6", "100,",
				// "下载成功，拷贝失败"));
				// throw new JobExecutionException("下载成功，拷贝失败");
				// }

				// 解压文件
				log.info("开始解压文件");
				UnpackZip(cachePath + getFielName(serverTask), agentPath+"/agent-update", "gbk");
				log.info("解压文件成功");

				// 启动升级器
				try {
					callBat(agentPath+"/agent-update/agentUpdate.bat");

					log.info("Agent升级器启动成功");
				} catch (Exception e) {
					log.info("Agent升级器启动失败");
					throw new JobExecutionException("Agent升级器启动失败，请检查升级文件");
				}

				// 清除缓存文件
				clearCacheFile(serverTask);

				// 返回任务状态等数据
				Map<String, Object> mapResult = new HashMap<String, Object>();
				mapResult.put("taskStatus", TaskStatus.extra);
				mapResult.put("exit", true);
				context.setResult(mapResult);
				
				// 关闭agent
				log.info("Agent即将关闭----");

			} catch (Exception e) {
				log.error("agent升级失败", e);
				// 清除缓存文件
				clearCacheFile(serverTask);
				throw new JobExecutionException("agent升级失败", e);
			}

		}
	}

	private void UnpackZip(String zippath, String filepath, String charset) {
		ZipUtil.unpack(new File(zippath), new File(filepath), new NameMapper() {
			@Override
			public String map(String name) {
				return name.substring(name.lastIndexOf("/") + 1);
			}
		}, Charset.forName(charset));
	}

	private void callBat(String cmdPath) throws Exception {
		Runtime rt = Runtime.getRuntime();
		Process ps = rt.exec("cmd /c start " + cmdPath);

	}

	private void agentRestart(ServerTask serverTask, JobExecutionContext context) {
		Map<String, Object> mapResult = new HashMap<String, Object>();
		// 判断升级是否成功
		if (isSuccessUpdate(serverTask)) {
			//更新数据库旧版本号
			Config.set("OLD_AGENT_VER",Config.get(Config.AGENT_VER));
			// 任务完成上传报
			uploadMess(setTaskStatus(serverTask, "6", "100,", "执行成功"));
			mapResult.put("taskStatus", TaskStatus.executed);
			log.info("Agent重启后升级完成,升级成功");
		} else {
			mapResult.put("taskStatus", TaskStatus.error);
			log.info("Agent重启后升级完成,升级失败");
		}
		// 完成更新操作
		String agentPath = Config.get(Config.AGENT_PATH);
		clearUpdateCache(agentPath);
		// 返回任务状态等数据
		context.setResult(mapResult);

	}

	/**删除更新文件
	 * @param agentPath
	 */
	private void clearUpdateCache(String agentPath) {
		File cacheFile = new File(agentPath+"/agent-update");
		if (cacheFile.exists()) {
			deleteDir(cacheFile);
			cacheFile.delete();
			log.info("agent升级缓存文件已清除：" + cacheFile.getPath());
		}
	}

	private void deleteDir(File cacheFile) {
		if(cacheFile.isDirectory()){
			File files[] = cacheFile.listFiles();
			for(File file : files){
				deleteDir(file);
			}
		}else{
			cacheFile.delete();
		}
	}

	private void clearCacheFile(ServerTask serverTask) {
		File cacheFile = new File(getFilePath(serverTask));
		if (cacheFile.exists()) {
			cacheFile.delete();
			log.info("缓存文件已清除：" + cacheFile.getPath());
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
			// 过滤字段
			ServerTask serverTaskFilter = (ServerTask) BeanUtil.filterBean(serverTask, filterField);
			// 将Task转为字符串
			String taskStr = new HttpEncoder().stringEncoder(serverTaskFilter);
			log.info("上传报文,url:" + url + "  报文内容：" + taskStr);
			String response = SendMessage.send(url, taskStr, SendMessage.TYPE_HTTP_GET);
		} catch (Exception e) {
			log.error("上传任务失败", e);
			// 保存任务报文

		}
	}

	private String getFilePath(ServerTask serverTask) {

		return cachePath + getFielName(serverTask);
	}

	private String getFielName(ServerTask serverTask) {
		// http://10.1.91.11:8080/FileServer/File\File\Other\test.exe
		String fileurl = serverTask.getFileURL();
		String filename = fileurl.substring(fileurl.lastIndexOf("/") + 1).trim();
		log.info("文件地址为：" + fileurl + "文件名为" + filename);
		return filename;
	}

	private boolean isSuccessUpdate(ServerTask serverTask) {
		Boolean flag = true;
		// 判断rdp版本
		if (Config.get(Config.AGENT_VER).equals(Config.get(Config.OLD_AGENT_VER))) {
			log.info("agent新版本与旧版本相同，升级失败");
			return false;
		}
		
		log.info("*******************agent版本符合***********************");
		// 判断时间
		try {
			if (isTimeout(serverTask.getExectime(), serverTask.getValid())) {
				log.info("RDP版本升级时间过长，最长升级时间为：" + serverTask.getValid());
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
	 * 
	 * @param executime(开始执行时间)
	 * @param valid(任务有效期，分钟)
	 * @return
	 * @throws Exception
	 */
	private boolean isTimeout(String executime, String valid) throws Exception {
		Date starttime = TimeUtil.StringToTime(executime, "yyyy-MM-dd HH:mm:ss.SSS");

		Date datenow = new Date();
		// 执行时间
		long interval = datenow.getTime() - starttime.getTime();
		if (Long.parseLong(valid) * 1000 * 60 < interval) {
			return true;
		}
		return false;
	}

	public boolean isAgentUpdate(Task task) {
		if (task != null && task instanceof ServerTask) {
			ServerTask serverTask = (ServerTask) task;
			if ("agentUpdateAct".equals(serverTask.getTasktype())) {
				return true;
			}
		}
		return false;
	}

}
