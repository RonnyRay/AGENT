/**
 * Ethink 2017 copyright
 * 
 */
package com.ethink.agent.task.bean;

/**
 *
 * 描述:
 * @author wangjing.dc@qq.com
 */
public class Task {
	
	/**任务类型*/
	private String taskType;
	
	/**任务id*/
	private String taskId;
	
	/**任务执行时间*/
	private String executeTime;

	/** 任务原始报文 */
	private String taskData;
	
	/** 任务状态 */
	private String taskStatus;
	/**
	 * @return the taskType
	 */
	public String getTaskType() {
		return taskType;
	}

	/**
	 * @param taskType the taskType to set
	 */
	public void setTaskType(String taskType) {
		this.taskType = taskType;
	}

	/**
	 * @return the taskId
	 */
	public String getTaskId() {
		return taskId;
	}

	/**
	 * @param taskId the taskId to set
	 */
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	/**
	 * @return the executeTime
	 */
	public String getExecuteTime() {
		return executeTime;
	}

	/**
	 * @param executeTime the executeTime to set
	 */
	public void setExecuteTime(String executeTime) {
		this.executeTime = executeTime;
	}

	public String getTaskData() {
		return taskData;
	}

	public void setTaskData(String taskData) {
		this.taskData = taskData;
	}

	public String getTaskStatus() {
		return taskStatus;
	}

	public void setTaskStatus(String taskStatus) {
		this.taskStatus = taskStatus;
	}
	
	
}
