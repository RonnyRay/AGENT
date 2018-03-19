package com.ethink.agent.task.executor.server;

import java.net.ConnectException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ethink.agent.Config;
import com.ethink.agent.SerialNumber;
import com.ethink.agent.annotation.TaskExecutors;
import com.ethink.agent.decode.RdpEncoder;
import com.ethink.agent.net.RDPMessageSend;
import com.ethink.agent.task.bean.RDPTask;
import com.ethink.agent.task.bean.ServerTask;
import com.ethink.agent.task.bean.Task;
import com.ethink.agent.task.executor.TaskExecutor;
import com.ethink.agent.util.HttpMessage;
import com.ethink.agent.util.TimeUtil;

/* @类描述：暂停ATM
 * @date: 2017年10月26日
 * @author: dingfan
 */
@TaskExecutors(value = "PauseAct")
public class PauseActExecutor implements TaskExecutor {

	private final static Logger log = LoggerFactory.getLogger(PauseActExecutor.class);
	//获取服务器地址
	String taskServer = Config.get(Config.TASK_SERVER);
	//执行结果上送地址
	String url = taskServer + "/task/pauseAct.ebf";
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException{
		Task task = (Task) context.getJobDetail().getJobDataMap().get("taskData");
		//判断task类型
		if (isSYRestart(task)) {
			ServerTask serverTask = (ServerTask) task;
			RDPTask rdptask = new RDPTask();
			rdptask.setTRNCODE("000000");
			rdptask.setTRNEJ(SerialNumber.getSerialNumber());
			rdptask.setCTIME(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
			log.info("开始向rdp发送暂停ATM报文");
			boolean flag = true;
			while (flag) {
			    try {
					log.info("向rdp发送暂停ATM报文");
					String result = RDPMessageSend.send(new RdpEncoder().encode(rdptask));
					if (result!=null&&"".equals(result)) {
						// 上送任务状态
						HttpMessage.uploadMess(HttpMessage.setTaskStatus(serverTask, "4", "20", "RDP报文已发送，正在执行"),url);
					}
					else {
						// 上送任务状态
						HttpMessage.uploadMess(HttpMessage.setTaskStatus(serverTask, "4", "0", "RDP报文已发送，等待执行"),url);
					}
			    } catch (ConnectException e) {
					try {
						log.info("暂停ATM报文发送失败，1分钟后再次连接rdp");
						Thread.sleep(10000);	
						if (isTimeout(serverTask.getExectime(), serverTask.getValid())) {
							flag = false;
							log.info("rdp暂停ATM报文发送失败，已超时，不再发送");
							HttpMessage.uploadMess(HttpMessage.setTaskStatus(serverTask, "7", "0", "RDP报文发送失败"),url);
							throw new JobExecutionException("暂停ATM报文发送失败", e);
						}
					} catch (Throwable e1) {
						log.error(null, e);
					}
					continue;
				}
				break;
			}
		}

	}

	/*
	 * @方法描述：判断task类型 
	 * @date: 2017年10月26日
	 * @author: dingfan
	 */
	public boolean isSYRestart(Task task) {
		if (task != null && task instanceof ServerTask) {
			ServerTask serverTask = (ServerTask) task;
			if ("PauseAct".equals(serverTask.getTasktype())) {
				return true;
			}
		}
		return false;
	}

	/*
	 * @方法描述：判断报文发送是否超时 
	 * @date: 2017年10月26日
	 * @author: dingfan
	 */
	private boolean isTimeout(String executime, String valid) throws Exception {
		Date starttime = TimeUtil.StringToTime(executime, "yyyy-MM-dd HH:mm:ss.SSS");
		Date datenow = new Date();		
		long interval = datenow.getTime() - starttime.getTime();
		if (Long.parseLong(valid) * 1000 * 60 < interval) {
			return true;
		}
		return false;
	}
}
