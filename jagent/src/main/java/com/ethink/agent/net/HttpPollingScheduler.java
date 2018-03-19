package com.ethink.agent.net;

import org.quartz.DateBuilder;
import org.quartz.DateBuilder.IntervalUnit;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ethink.agent.Config;
import com.ethink.agent.Starter;

/**
 * 类描述:轮询调度
 * 创建时间:2017年10月12日
 * @author wangluliang
 */
@Component
public class HttpPollingScheduler implements Starter{
	Scheduler scheduler;
	private final static Logger log = LoggerFactory.getLogger(HttpPollingScheduler.class);
	/**
	 * 启动轮询
	 */
	@Override
	public void start(){
		try {
			String taskLoopTime = Config.get(Config.TASK_LOOP_TIME);
		
			//创建scheduler
			 scheduler = StdSchedulerFactory.getDefaultScheduler();
			//定义Trigger
			Trigger trigger = TriggerBuilder.newTrigger()
					.withIdentity("httppolling", "httppollingGroup")
					//一个周期后执行
					.startAt(DateBuilder.futureDate(Integer.parseInt(taskLoopTime), IntervalUnit.SECOND))
					.withSchedule(
							SimpleScheduleBuilder.simpleSchedule()
							.withIntervalInSeconds(Integer.parseInt(taskLoopTime)).repeatForever())
					.build();
			//定义Job
			JobDetail job = JobBuilder.newJob(HttpPolling.class)
					.withIdentity("httppolling","httppollingGroup").build();
			//加入调度
			scheduler.scheduleJob(job, trigger);
			//启动
			scheduler.start();
			log.info("HTTP轮询启动,轮询间隔："+taskLoopTime+",将于"+taskLoopTime+"秒后开始第一次轮询");
		} catch (Exception e) {
			log.error("HTTP轮询启动失败!",e);
		}
		
	}
	/**
	 * 关闭轮询
	 */
	@Override
	public void stop(){
		if(scheduler==null){
			return;
		}
//		try {
//			scheduler.shutdown(true);
//			log.info("HTTP轮询已关闭");
//		} catch (SchedulerException e) {
//			log.error("HTTP轮询关闭失败!",e);
//		}
	}
	
	public static void main(String[] args) {
		new HttpPollingScheduler().start();
	}
}
