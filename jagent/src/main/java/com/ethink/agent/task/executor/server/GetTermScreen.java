package com.ethink.agent.task.executor.server;


import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.imageio.ImageIO;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ethink.agent.Config;
import com.ethink.agent.annotation.TaskExecutors;
import com.ethink.agent.task.bean.ServerTask;
import com.ethink.agent.task.bean.Task;
import com.ethink.agent.task.executor.TaskExecutor;
import com.ethink.agent.util.FileUpload;
import com.ethink.agent.util.FileUtil;
import com.ethink.agent.util.HttpMessage;

/* @类描述：获取屏幕截图
 * @date: 2017年11月6日
 * @author: dingfan
 *
 */
@TaskExecutors(value="getTermScreenAct")
public class GetTermScreen implements TaskExecutor{
	
	private final static Logger log = LoggerFactory.getLogger(GetTermScreen.class);
	
	String taskServer = Config.get(Config.TASK_SERVER);

	//RDP截屏
	String url = taskServer + "/task/getTermScreenAct.ebf";
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		Task task = (Task) context.getJobDetail().getJobDataMap().get("taskData");
		//获取服务器地址
		String taskServer = Config.get(Config.TASK_SERVER);
		//执行结果上送地址
		String url = taskServer + "/task/shutdownAct.ebf";
		
		// 判断task对象是否合法
		if (isGetTermScreen(task)) {
			
			ServerTask serverTask = (ServerTask) task;
			
			String fileName = serverTask.getFilename();
		 
			if(fileName == null || "".equals(fileName)){
				log.error("未获取到截屏文件名", fileName);
				return;
			}
			
			if(fileName.substring(fileName.lastIndexOf(".") + 1).equalsIgnoreCase("ZIP")){
				fileName = fileName.substring(0, fileName.lastIndexOf("."));
			}
			
			String folderPath = "C:/ScreenPicture/" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + "/" + fileName.substring(0, fileName.lastIndexOf("."));
			
			File folder = new File(folderPath);
			if(!folder.exists()){
				folder.mkdirs();
			}

			BufferedImage image;
			
			try {
				
				image = new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
				ImageIO.write(image, fileName.substring(fileName.lastIndexOf(".") + 1), new FileOutputStream(new File(folderPath + "/" + fileName)));
				
			} catch (Exception e) {
				log.error("Agaent截取终端屏幕失败", e);
				return;
			}
			
            try {
            	
				if(!FileUtil.fileToZip(folderPath, folderPath.substring(0, folderPath.lastIndexOf("/")), fileName)){
					log.error("Agaent截屏图片打ZIP压缩包失败");
					return;
				}
            	
			} catch (Exception e) {
				log.error("Agaent截屏图片打ZIP压缩包失败", e);
				return;
			}     
            String[] path = {folderPath+"/"+fileName+".zip"};
            //上传截屏文件
            String uploadResult = FileUpload.upload(serverTask.getFilesvr(), path); 
            if (null!=uploadResult &&!"".equals(uploadResult)) {
            	
            	HttpMessage.uploadMess(HttpMessage.setTaskStatus(serverTask, "9", "100", "上传成功"),url);
			}
            else {
            	 log.info("上传终端件失败");
            	HttpMessage.uploadMess(HttpMessage.setTaskStatus(serverTask, "6", "0", "上传失败"),url);
            }
            FileUtil.deleteFile(folderPath);                  
	    }
	}
	
	public boolean isGetTermScreen(Task task){
		if(task != null && task instanceof ServerTask){
			ServerTask serverTask = (ServerTask)task;
			return serverTask.getTasktype().equals("getTermScreenAct");
		}else{
			return false;
		}
		
	}
	
	
}