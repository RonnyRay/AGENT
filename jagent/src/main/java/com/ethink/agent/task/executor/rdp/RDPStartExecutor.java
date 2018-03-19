/**
 * Ethink 2017 copyright
 * 
 */
package com.ethink.agent.task.executor.rdp;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ethink.agent.Config;
import com.ethink.agent.annotation.TaskExecutors;
import com.ethink.agent.event.Event;
import com.ethink.agent.event.EventHandler;
import com.ethink.agent.task.bean.RDPTask;
import com.ethink.agent.task.bean.Task;
import com.ethink.agent.task.executor.RDPTaskExecutor;


/**
 *
 * 描述: RDP启动执行器
 * @author wangjing.dc@qq.com
 */
@TaskExecutors(value="RDPStartExecutor")
public class RDPStartExecutor implements RDPTaskExecutor {
	private final static Logger log = LoggerFactory.getLogger(RDPStartExecutor.class);
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		log.info("RDPStartExecutor  begin to executing……");
		Task task = (Task)context.getJobDetail().getJobDataMap().get("taskData");		
		//判断task对象是否合法
		if(isRDPStart(task)){
			RDPTask rdpTask = (RDPTask)task;
			log.info("RDP信息开始保存………………");
			log.info("RDP信息开始保存………………"+rdpTask.toString()+"…………");
			saveData( rdpTask);			
			EventHandler.fireEvent(EventHandler.newEvent(Event.TYPE_RDPSTARTUP, rdpTask));
		}
	}
	
	public boolean isRDPStart(Task task){
		if(task!=null && task instanceof RDPTask){
			RDPTask rdpTask = (RDPTask)task;
			if("RDPStartExecutor".equals(rdpTask.getTaskType())){
				return true;
			}			
		}
		return false;
	}
	
	/*方法描述：保存rdp下发报文信息
	 *@prams: rdpTask
	 * 
	 */
	public  void saveData(RDPTask rdpTask) {		
			if (null!=rdpTask.getSOF_VER()&&!"".equals(rdpTask.getSOF_VER())) {	
				String OLD_SOF_VER =  Config.get(Config.NEW_SOF_VER);				
				int saveOLDResult =  Config.set(Config.OLD_SOF_VER,OLD_SOF_VER);				
				int saveNEWResult =  Config.set(Config.NEW_SOF_VER,rdpTask.getSOF_VER());
				log.info("saveOLDResult="+saveOLDResult+";saveNEWResult="+saveNEWResult);
				if (saveOLDResult>0 && saveNEWResult>0) {					
					log.info("软件版本号保存成功……………………");
				}
				else {
					log.info("软件版本号保存失败……………………");
				}
			}
					
			if (null!=rdpTask.getADV_VER()&&!"".equals(rdpTask.getADV_VER())) {	
				String OLD_ADV_VER =  Config.get(Config.NEW_ADV_VER);				
				int saveOLDResult =  Config.set(Config.OLD_ADV_VER,OLD_ADV_VER);
				int saveNEWResult = Config.set(Config.NEW_ADV_VER,rdpTask.getADV_VER() );						
				if (saveOLDResult>0 && saveNEWResult>0) {					
					log.info("广告版本号保存成功……………………");
				}
				else {
					log.info("广告版本号保存失败……………………");
				}
			}
			if (null!=rdpTask.getDEV_NUM()&&!"".equals(rdpTask.getDEV_NUM())) {			
				int saveResult = Config.set(Config.DEV_NUM,rdpTask.getDEV_NUM() );
				if (saveResult>0) {					
					log.info("设备号保存成功……………………");
				}
				else {
					log.info("设备号号保存失败……………………");
				}
			}						  	
	}
	
}
