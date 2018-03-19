package com.ethink.agent.service;

import java.util.Map;

/**
 * 更新数据接口
 * @author zhaxing
 */
public interface ExecuteService {
	
	/**
	 * 更新数据
	 * @return
	 */
	public void execute(Map<String, Object> parameterMap);

}
