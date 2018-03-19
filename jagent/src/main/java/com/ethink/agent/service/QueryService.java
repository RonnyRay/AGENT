package com.ethink.agent.service;

import java.util.Map;

/**
 * 查询数据接口
 * @author zhaxing
 */
public interface QueryService {
	
	/**
	 * 查询数据(无参)
	 * @return
	 */
	public Map<String,Object> query();

	/**
	 * 查询数据
	 * @return
	 */
	public Map<String,Object> query(Map<String, Object> parameterMap);

}
