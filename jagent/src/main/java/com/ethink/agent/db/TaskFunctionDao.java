package com.ethink.agent.db;

import java.util.List;
import java.util.Map;

import com.ethink.agent.task.bean.ServerTask;
import com.ethink.agent.task.bean.Task;

/**
 * @类描述 Task访问接口
 * @创建时间 2017年10月18日
 * @author wangluliang
 */
public interface TaskFunctionDao {

	/**
	 * 插入task对象
	 * @param map
	 * @return
	 */
	public int insertTaskInfo(Map<String, Object> map);
	
	/**
	 * 更新task状态
	 * @param map
	 * @return
	 */
	public int updateTaskInfo(Map<String, Object> map);

	/**
	 * 获取额外状态的task
	 * @return
	 */
	public List<ServerTask> selectExtraStatus();
	/**
	 * 获得没有执行的task
	 * @return
	 */
	public List<Map<String, Object>> selectNotyetExecute();
	
}
