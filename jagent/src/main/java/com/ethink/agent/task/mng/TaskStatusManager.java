/**
 * Ethink 2017 copyright
 * 
 */
package com.ethink.agent.task.mng;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ethink.agent.db.TaskFunctionDao;
import com.ethink.agent.event.Event;
import com.ethink.agent.task.bean.ServerTask;
import com.ethink.agent.task.bean.Task;

/**
 *
 * 描述: Task状态管理
 * 
 * @author wangjing.dc@qq.com
 */
@Component
public class TaskStatusManager {

	private static final Logger log = LoggerFactory.getLogger(TaskStatusManager.class);

	@Autowired
	private TaskFunctionDao taskFunctionDao;

	/**
	 * 更新task状态(params顺序为starttime,endtime)
	 * 
	 * @param task
	 * @param ready
	 */
	public void update(Task task, TaskStatus status, String... params) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("taskId", task.getTaskId());
		map.put("taskStatus", status);
		for(int i=0;i<params.length;i++){
			if (params[i] != null) {
				if(i==0){
					map.put("startTime", params[i]);
				}else{
					map.put("endTime", params[i]);
				}
			}
			
		}
		int flag = taskFunctionDao.updateTaskInfo(map);
		if (flag != 0) {
			log.info("数据库更新task任务状态成功,taskId:" + task.getTaskId() + " 任务状态：" + status);
		} else {
			log.info("数据库更新task任务状态失败,taskId:" + task.getTaskId());
		}
	}

	/**
	 * 插入一条task信息
	 * 
	 * @param task
	 * @param status
	 * @param taskData
	 */
	public void insert(Task task, TaskStatus status,String eventType) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("taskId", task.getTaskId());
		map.put("taskStatus", status);
		map.put("taskData", task.getTaskData());
		map.put("planid", "");
		map.put("msgType", eventType);
		if(task instanceof ServerTask){
			map.put("planid", ((ServerTask)task).getPlanid());
		}
		
		int flag = taskFunctionDao.insertTaskInfo(map);
		if (flag != 0) {
			log.info("数据库插入task信息成功,taskId:" + task.getTaskId() + " 任务状态：" + status);
		} else {
			log.info("数据库更新task任务状态失败,taskId:" + task.getTaskId());
		}
	}

	public List<ServerTask> getExtraTask() {
		List<ServerTask> list = taskFunctionDao.selectExtraStatus();
		return list;
	}

}
