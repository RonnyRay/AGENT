package com.ethink.agent.task.executor.server;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ethink.agent.Config;
import com.ethink.agent.task.bean.ServerTask;
import com.ethink.agent.task.bean.Task;
import com.ethink.agent.task.executor.TaskExecutor;
import com.ethink.agent.util.FetchUrl;
import com.ethink.agent.util.FileSearch;
import com.ethink.agent.util.FileUpload;
import com.ethink.agent.util.FileUtil;
import com.ethink.agent.util.HttpMessage;

public class TermFileAcExecutor implements TaskExecutor {

	private final static Logger log = LoggerFactory.getLogger(TermFileAcExecutor.class);

	//获取服务器地址
	private String taskServer = Config.get(Config.TASK_SERVER);
	//执行结果上送地址
	private String url = taskServer + "/task/getTermFileAct.ebf";
	//文件缓存
	private static String tempFile="C:\\tempFile";
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException{
		Task task = (Task) context.getJobDetail().getJobDataMap().get("taskData");
		//判断task类型
		log.info("开始获取终端文件……");
		if (isSYRestart(task)) {
		ServerTask serverTask = (ServerTask) task;
		//获取文件类型
		int searchType = serverTask.getSearchType();
		String[] filePath = serverTask.getFilePath().split("|");
		String[] wildcard = serverTask.getWildcard().split("|");
		//符合通配符的文件路径
		List<String> resultList = new ArrayList<String>(); 
		//创建缓存文件夹
		File folder = new File(tempFile);
		if(!folder.exists()){
			folder.mkdirs();
		}
		//searchType搜索文件类型： 0 表示文件,1表示文件夹
		if (searchType==0) {
			//IncludeSubDir: 表示是否搜索子目录，=Y是，=N否（默认N）
			if ("Y".equals(serverTask.getIncludeSubDir())) {
				for (int i = 0; i < filePath.length; i++) {
				log.info("//获取符合通配符的文件");
				FileSearch.findFiles(filePath[i], wildcard[i],resultList,true); 
				}
				
			}
			else {
				for (int i = 0; i < filePath.length; i++) {
				//获取符合通配符的文件
				FileSearch.findFiles(filePath[i], wildcard[i],resultList,false);
				}
			}
			if (!resultList.isEmpty()) {				
			
			log.info("//根据CmpType进行文件筛选");
			FileSearch.selectFile(resultList,serverTask.getCmpType(),serverTask.getSearchRangeBegin(),serverTask.getSearchRangeEnd());
			}
						
		}
		else {
			for (int i = 0; i < filePath.length; i++) {
				
				try {
					FetchUrl.copyFolder(new File(filePath[i]+"\\"+wildcard[i]),folder);
				} catch (IOException e) {
				 log.info("文件复制失败");
				}
			}
			
		}
		
		 try {
			FileUtil.fileToZip(tempFile, tempFile.substring(0,tempFile.lastIndexOf("/")+1), serverTask.getFilename());
		} catch (Exception e) {
			log.info("终端文件打包失败！");
		}
		 String[] path = {tempFile.substring(0,tempFile.lastIndexOf("/")+1)+serverTask.getFilename()+".zip"};
		 log.info("上传终端件");
         String uploadResult = FileUpload.upload(serverTask.getFilesvr(), path); 
         if (null!=uploadResult &&!"".equals(uploadResult)) {
        	 
         	HttpMessage.uploadMess(HttpMessage.setTaskStatus(serverTask, "9", "100", "上传成功"),url);
			}
         else {
        	 log.info("上传终端件失败");
         	HttpMessage.uploadMess(HttpMessage.setTaskStatus(serverTask, "6", "0", "上传失败"),url);
         }
         //删除缓存文件
		 FileUtil.deleteFile(tempFile);	
		
    }
}
	
	public boolean isSYRestart(Task task) {
		if (task != null && task instanceof ServerTask) {
			ServerTask serverTask = (ServerTask) task;
			if ("getTermFileAct".equals(serverTask.getTasktype())) {
				return true;
			}
		}
		return false;
	}


}