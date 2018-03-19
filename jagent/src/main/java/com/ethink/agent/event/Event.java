/**
 * Ethink 2017 copyright
 * 
 */
package com.ethink.agent.event;

import com.ethink.agent.annotation.EventDecoder;
import com.ethink.agent.decode.HttpDecoder;
import com.ethink.agent.decode.RdpDecoder;
import com.ethink.agent.decode.RdpEncoder;
import com.ethink.agent.decode.TcpDecoder;

/**
 *
 * 描述: 系统事件接口
 * @author wangjing.dc@qq.com
 */
public class Event {
	
	/** 收发报文事件类型都需要加入EventDecoder注解 */
	
	/**rdp系统启动*/
	@EventDecoder(value = RdpDecoder.class)
	public static final String TYPE_RDPSTARTUP = "TYPE_RDPSTARTUP"; 
	
	/**Agent系统启动*/
	public static final String TYPE_AGENTSTARTUP = "TYPE_AGENTSTARTUP"; 
	
	/**TCP监听事件*/
	@EventDecoder(value = TcpDecoder.class)
	public static final String TYPE_TCPDATA = "TYPE_TCPDATA";
	
	/**http轮询事件*/
	@EventDecoder(value=HttpDecoder.class)
	public static final String TYPE_HTTPDATA = "TYPE_HTTPDATA";
	
	/**RDP监听事件*/
	@EventDecoder(value = RdpDecoder.class)
	public static final String TYPE_RDPDATA = "TYPE_RDPDATA";
	
	@EventDecoder(value = RdpEncoder.class)
	public static final String TYPE_RDPSEND = "TYPE_RDPSEND";
	/**
	 * 事件类型
	 */
	private String eventType;
	/**
	 * 事件附带数据
	 */
	private Object eventData;
	
	/**
	 * @return the eventType
	 */
	public String getEventType() {
		return eventType;
	}
	/**
	 * @param eventType the eventType to set
	 */
	public void setEventType(String eventType) {
		this.eventType = eventType;
	}
	/**
	 * @return the eventData
	 */
	public Object getEventData() {
		return eventData;
	}
	/**
	 * @param eventData the eventData to set
	 */
	public void setEventData(Object eventData) {
		this.eventData = eventData;
	}
	
	
	

}
