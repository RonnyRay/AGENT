/**
 * Ethink 2017 copyright
 * 
 */
package com.ethink.agent.event;

/**
 *
 * 描述:
 * @author wangjing.dc@qq.com
 */
public interface EventListener {
	/**
	 * 得到优先级(1-10),10最高，最先执行
	 * @return
	 */
	public int getPriority();
	
	public void event(Event event);

}
