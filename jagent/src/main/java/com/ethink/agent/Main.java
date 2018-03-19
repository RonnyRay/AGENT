package com.ethink.agent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.PropertySource;

import com.ethink.agent.event.EventListener;
import com.ethink.agent.task.mng.TaskManager;

@SpringBootApplication
@PropertySource({"classpath:config.properties", "file:agent.properties","classpath:agent_ver.properties"})
public class Main{
	
	private final static Logger log = LoggerFactory.getLogger(Main.class);
	
	private static ConfigurableApplicationContext context;
	
    public static void main(final String[] args) throws Exception {    	
    	SpringApplication app = new SpringApplication(Main.class);
    	app.setBannerMode(Banner.Mode.OFF);
		log.debug("Ronny--------------------------------Test");
    	context = app.run(args);
    	stargAgent();
    }

	
	private static void stargAgent() {
		Agent bs = context.getBean(Agent.class);
		
		bs.setContext(context)
			//更新表结构
			.executorJDBC()
			.printConfig()
			// 注册通讯没事件监听
			.listenTaskin((EventListener)(Agent.findBean(TaskManager.class)))
			// 启动taskserver监听
			.startTaskServerListen()
			// 启动rdp监听
			.startRDPListen()
			// 启动http轮询
			.startHttpPolling()
			;
		
		log.info("=================jagent started======================");
		//还原任务
		bs.taskRestore();
	}

	
    
}
