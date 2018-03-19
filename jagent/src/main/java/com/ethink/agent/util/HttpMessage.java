package com.ethink.agent.util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ethink.agent.Config;
import com.ethink.agent.decode.HttpEncoder;
import com.ethink.agent.net.SendMessage;
import com.ethink.agent.task.bean.ServerTask;



public class HttpMessage {
	private final static Logger log = LoggerFactory.getLogger(HttpMessage.class);

	public static ServerTask setTaskStatus(ServerTask serverTask, String taskstatus, String percent,
				String Extradescription) {
			serverTask.setTaskstatus(taskstatus);
			serverTask.setPercent(percent);
			serverTask.setExtradescription(Extradescription);
			return serverTask;
		}
		
		public static void uploadMess(ServerTask serverTask,String uploadurl) {
			//需要发送的字段
			String filterField[] = {"tasktype","planid","taskid","termid","fileid","taskstatus","percent","extradescription"};
			String url = uploadurl;
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
	}
