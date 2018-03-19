package com.ethink.agent.task.executor.rdp;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.ethink.agent.Config;
import com.ethink.agent.SerialNumber;
import com.ethink.agent.annotation.TaskExecutors;
import com.ethink.agent.decode.HttpEncoder;
import com.ethink.agent.decode.RdpEncoder;
import com.ethink.agent.event.Event;
import com.ethink.agent.event.EventHandler;
import com.ethink.agent.event.EventListener;
import com.ethink.agent.net.SendMessage;
import com.ethink.agent.task.bean.RDPTask;
import com.ethink.agent.task.bean.ServerTask;
import com.ethink.agent.task.bean.Task;
import com.ethink.agent.task.executor.TaskExecutor;
import com.ethink.agent.task.mng.TaskStatusManager;
import com.ethink.agent.util.FetchUrl;
import com.ethink.agent.util.HttpUtil;

@TaskExecutors(value="pauseAct")
public class RDPPauseExexutor implements TaskExecutor{
	
	private final static Logger log = LoggerFactory.getLogger(RDPPauseExexutor.class);

	String taskServer = Config.get(Config.TASK_SERVER);

	//RDP暂停
	String url = taskServer + "/task/pauseAct.ebf";

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		Task task = (Task) context.getJobDetail().getJobDataMap().get("taskData");
		// 判断task对象是否合法
		if (isPause(task)) {
			
			ServerTask serverTask = (ServerTask) task;
			
			RDPTask rdpTask = new RDPTask();
			rdpTask.setTRNCODE("301002");
			rdpTask.setTRNEJ(SerialNumber.getSerialNumber());
			rdpTask.setCTIME(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
			
			try {
				SendMessage.send(null, new RdpEncoder().encode(rdpTask), SendMessage.TYPE_RDP);
			} catch (Exception e) {
				log.error("Agaent向RDP发送暂停指令失败", e);
			}
			
		}
	}

	public boolean isPause(Task task){
		if(task != null && task instanceof ServerTask){
			ServerTask serverTask = (ServerTask)task;
			return serverTask.getTasktype().equals("pauseAct");
		}else{
			return false;
		}
		
	}
	
}