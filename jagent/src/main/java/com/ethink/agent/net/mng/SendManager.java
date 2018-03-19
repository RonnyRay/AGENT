package com.ethink.agent.net.mng;

import com.ethink.agent.event.Event;
import com.ethink.agent.event.EventHandler;
import com.ethink.agent.event.EventListener;

/**
 * @类描述 上送报文管理类
 * @创建时间 2017年10月19日
 * @author wangluliang
 */
public class SendManager {
	EventListener sendListener = new EventListener() {
		
		@Override
		public int getPriority() {
			// TODO Auto-generated method stub
			return 0;
		}
		
		@Override
		public void event(Event event) {
			SendManager.this.sendMessEvent(event);
		}

		
	};
	public SendManager(){
		EventHandler.listen(Event.TYPE_RDPSEND, sendListener);
	}
	
	/**
	 * 处理发送消息时间
	 * @param event
	 */
	public void sendMessEvent(Event event){
		
	}
	

}
