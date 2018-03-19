package com.ethink.agent.net;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ethink.agent.Config;
import com.ethink.agent.event.Event;
import com.ethink.agent.event.EventHandler;
import com.ethink.agent.util.HttpUtil;

/**
 * @类描述 HTTP轮询Job
 * @创建时间 2017年10月12日
 * @author wangluliang
 */
public class HttpPolling  implements Job{

	private final static Logger log = LoggerFactory.getLogger(HttpPolling.class);

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		// 获得服务器地址
		String taskServer = Config.get(Config.TASK_SERVER);
		String atmcVer = Config.get(Config.ATMC_VER);
		String agentVer = Config.get(Config.AGENT_VER);
		
		String url = taskServer + "/task/planReqAct.ebf";
		System.out.println(url+"HTTP轮询已启动,地址"+url);
		// atmc版本和agent版本参数
		String params = "atmcVer="+atmcVer+"&agentVer="+agentVer;
		try {
			String response = HttpUtil.sendGetRequest(url, params);
			log.info("HTTP轮询发送请求url:"+url+",参数:"+params+",响应："+response);
			if(response!=null&&!("".equals(response))){
				//抛出
				EventHandler.fireEvent(EventHandler.newEvent(Event.TYPE_HTTPDATA, response));				
			}else{
				log.info("响应为空，不做处理");
			}
		}catch(Exception e) {
			log.error("HTTP轮询请求异常,请检查请求地址是否正确！");
			throw new JobExecutionException(e);
		}
		
	}


}
