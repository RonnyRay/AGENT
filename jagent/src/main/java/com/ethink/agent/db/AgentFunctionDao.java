package com.ethink.agent.db;

import java.util.List;
import java.util.Map;

/**
 * 本地数据库访问接口
 * @author zhaxing
 */
public interface AgentFunctionDao {
	
	/**
	 * 查询设备号
	 * @return
	 */
	String selectDeviceNum();

	/**
	 * 根据name条件查询SystemInfo表的value
	 * @return
	 */
	String selectSystemInfoValueByName(Map<String,Object> params);

	/**
	 * 插入一条SystemInfo表数据 
	 * @return
	 */
	void insertSystemInfo(Map<String,Object> params);

	/**
	 * 根据name条件修改SystemInfo表的value
	 * @return
	 */
	void updateSystemInfoValueByName(Map<String,Object> params);

	/**
	 * 查询配置信息
	 * @param confName
	 */
	public String selectConfig(Map<String, Object> params);
	
	/**
	 * 查询所有配置信息
	 * @param params
	 * @return
	 */
	public List<Map> selectAllConfig();
    
	
	/**
	 * 更改config信息
	 * @param params
	 * @return
	 */
	int updateConfig(Map<String, Object> param);
    
}
