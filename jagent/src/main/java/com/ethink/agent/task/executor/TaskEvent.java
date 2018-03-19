/**
 * Ethink 2015 copyright
 * 
 */
package com.ethink.agent.task.executor;

/**
 *
 * 描述: Task事件接口
 * @author wangjing.dc@qq.com
 */
public interface TaskEvent {
	
	/**rdp系统启动*/
	public static final String TYPE_RDPSTARTUP = "TYPE_RDPSTARTUP"; 
	
	/**
	 * 
	 * @param eventType  事件类型
	 * @param args 参数
	 */
	public void event(String eventType, Object... args);
	
}
