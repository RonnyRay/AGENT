/**
 * Ethink 2017 copyright
 * 
 */
package com.ethink.agent;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import com.ethink.agent.db.ExecutorJDBC;
import com.ethink.agent.event.Event;
import com.ethink.agent.event.EventHandler;
import com.ethink.agent.event.EventListener;
import com.ethink.agent.net.HttpPollingScheduler;
import com.ethink.agent.net.RDPListening;
import com.ethink.agent.net.TcpListening;

/**
 *
 * 描述:
 * @author wangjing.dc@qq.com
 */
@Component
public class Agent {
	
	private static final Logger log = LoggerFactory.getLogger(Agent.class);
	private static ConfigurableApplicationContext context;
	
	@Autowired
	private TcpListening tcpListening;
	
	@Autowired
	private RDPListening rDPListening;
	
	@Autowired
	private HttpPollingScheduler httpPollingScheduler;

	/**
	 * 检索系统bean对象
	 * @param <T>
	 * @param clazz
	 * @return
	 */
	public static <T> T findBean(Class<T> clazz) {
		return context.getBean(clazz);
	}
	
	public Agent setContext(ConfigurableApplicationContext app) {
		context = app;
		
		return this;
	}

	/**
	 * 启动taskServer监听服务
	 * @return
	 */
	public Agent startTaskServerListen() {
		tcpListening.start();
		
		return this;
	}
	
	/**
	 * 启动rdp监听服务
	 * @return
	 */
	public Agent startRDPListen() {
		rDPListening.start();
		
		return this;
	}
	
	/**
	 * 启动http轮询
	 * @return
	 */
	public Agent startHttpPolling() {
		httpPollingScheduler.start();
		
		return this;
	}
	
	/**
	 * 注册task事件监听
	 * @param listener
	 * @return
	 */
	public Agent listenTaskin(EventListener listener) {
		EventHandler.listen(Event.TYPE_HTTPDATA, listener);
		EventHandler.listen(Event.TYPE_TCPDATA, listener);
		EventHandler.listen(Event.TYPE_RDPDATA, listener);
		EventHandler.listen(Event.TYPE_RDPSTARTUP, listener);
		EventHandler.listen(Event.TYPE_AGENTSTARTUP, listener);
		return this;
	}

	@SuppressWarnings("rawtypes")
	public Agent printConfig() {
		List<Map> configs = Config.getAll();
		log.info("----------------------========系统初始配置信息=========-------------------------");
		for(Map conf : configs) {
			log.info(conf.toString());
		}
		log.info("----------------------============================-------------------------");
		
		return this;
	}
	
	/**
	 * agent启动时检查是否有需要还原的任务
	 * @return
	 */
	public Agent taskRestore(){
		log.info("检查是否有需要还原的任务-----");
		EventHandler.fireEvent(EventHandler.newEvent(Event.TYPE_AGENTSTARTUP, null));
		return this;
	}

	/**更新表结构
	 * @return
	 */
	public Agent executorJDBC() {
		new ExecutorJDBC().executorJDBC();
		return this;
	}
	
}
