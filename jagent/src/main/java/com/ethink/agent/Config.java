/**
 * Ethink 2017 copyright
 * 
 */
package com.ethink.agent;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.ethink.agent.db.AgentFunctionDao;

/**
 *
 * 描述: 系统配置
 * @author wangjing.dc@qq.com
 */
@Component
public class Config {
	
	/**Task TCP主动监听端口*/
	public static final String LISTEN_TASK_PORT = "LISTEN_PORT4TASKSERVER";
	
	/**Task Server 地址*/
	public static final String TASK_SERVER = "TASK_SERVER";
	
	/**文件服务器地址*/
	public static final String FILE_SERVER = "FILE_SERVER";
	
	/**RDP报文发送端口*/
	public static final String RDP_PORT = "RDP_PORT";
	
	/**Http 轮询时间间隔*/
	public static final String TASK_LOOP_TIME = "TASK_LOOP_TIME";
	
	/**当前atmc版本号，完成版本号*/
	public static final String ATMC_VER = "ATMC_VER";
	
	/**当前agent版本号.如果没有单独的agent，使用atmc的版本号*/
	public static final String AGENT_VER = "AGENT_VER";
	
	/**8位设备编号表示该设备的前置编号*/
	public static final String DEV_NUM = "DEV_NUM";
			
	/**当前软件版本号*/
    public static final String NEW_SOF_VER = "NEW_SOF_VER";
	
    /**旧当前软件版本号*/
    public static final String OLD_SOF_VER = "OLD_SOF_VER";
    
    /**当前广告本号*/
	public static final String NEW_ADV_VER = "NEW_ADV_VER";
	
	/**旧广告本号*/

	public static final String OLD_ADV_VER = "OLD_ADV_VER";
	
	/**旧agen版本号*/
	public static final String OLD_AGENT_VER = "OLD_AGENT_VER";
	
	/**agent的路径*/
	public static final String AGENT_PATH = "AGENT_PATH";
	
	
	/*-----------------AGENT参数--------------*/
	/** 推送服务器地址 */
	public static final String PUSHSVRIP = "PUSHSVRIP";
	/** 推送服务器端口 */
	public static final String PUSHSVRPORT = "PUSHSVRPORT";
	/** 电子流水地址 */
	public static final String EJSVR = "EJSVR";
	/** 电子流水端口 */
	public static final String EJSVRPORT = "EJSVRPORT";
	/** 状态监控服务IP */
	public static final String AMSIP = "AMSIP";
	/** 监控服务TCP端口 */
	public static final String AMSTCPPORT = "AMSTCPPORT";
	/** 监控服务UDP端口 */
	public static final String AMSUDPPORT = "AMSUDPPORT";
	/** 监控报文发送间隔（秒） */
	public static final String SENDINTERVAL = "SENDINTERVAL";
	/** 1-上传电子流水，0-不上传电子流水 */
	public static final String EJUPLOAD = "EJUPLOAD";
	/** 只打印电子流水不打印纸质流水（ejupload必须为1才有效），0-打印电子流水和纸质流水 */
	public static final String EJONLY = "EJONLY";
	/** 电子流水丢失容忍笔数，硬盘未坏 */
	public static final String EJTORLERANT = "EJTORLERANT";
	/** 电子流水丢失容忍笔数，硬盘已坏 */
	public static final String EJHDDTOLERANT = "EJHDDTOLERANT";
	/** 对账报文批量上传笔数 */
	public static final String TRANSINFOBATCH = "TRANSINFOBATCH";
	/** 上传文件大小限制（KB） */
	public static final String FILELIMIT = "FILELIMIT";
	
	
	private static final Logger log = LoggerFactory.getLogger(Config.class);
	
	@Autowired
	private AgentFunctionDao agentFunctionDao;
	@Autowired private Environment env;
	
	private List<String> fromFile = Arrays.asList(RDP_PORT,AGENT_VER,AGENT_PATH);
	
	/**
	 * 获取配置参数
	 * @param confName 参数名称
	 * @return 参数值
	 */
	public static String get(String confName) {
		Config conf = Agent.findBean(Config.class);
		return conf.getConfig(confName);
	}
	
	public static int set(String name, String value) {
		Config conf = Agent.findBean(Config.class);
		return conf.setConfig(name, value);
	}
	
	public static List<Map> getAll(){
		Config conf = Agent.findBean(Config.class);
		return conf.getAllConfig();
	}
	
	public String getConfig(String confName) {
		if(fromFile.contains(confName)) {
			String value = env.getProperty(confName);
			if(value == null) {
				log.error("配置文件中找不到属性值" + confName);
			}
			
			return value;
		}
		
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("name", confName);
		
		String value = agentFunctionDao.selectConfig(param);		
		return value;
	}
	
	public  List<Map> getAllConfig() {
		return agentFunctionDao.selectAllConfig();
	}
	
	public int setConfig(String name, String value) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("value", value);
		param.put("name", name);
		return agentFunctionDao.updateConfig(param);
	}
	
	
}
