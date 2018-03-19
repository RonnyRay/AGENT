/**
 * Ethink 2017 copyright
 * 
 */
package com.ethink.agent.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * 描述: 事件处理助手
 * @author wangjing.dc@qq.com
 */
public class EventHandler {
	
	private static final Logger log = LoggerFactory.getLogger(EventHandler.class);
	
	private static Map<String, List<EventListener>> listeners = new HashMap<String, List<EventListener>>();

	public static void fireEvent(Event event) {
		log.info("====Event=====" + event);
		
		List<EventListener>  fires = listeners.get(event.getEventType());
		if(fires != null && fires.size() > 0) {
			for(EventListener listener : fires) {
				try {
					listener.event(event);
				}catch(Exception e) {
					log.error(null, e);
				}
			}
		}
	}
	
	/**
	 * 快速创建event对象
	 * @param eventType
	 * @param eventData
	 * @return
	 */
	public static Event newEvent(String eventType, Object eventData) {
		Event event = new Event();
		event.setEventType(eventType);
		event.setEventData(eventData);
		
		return event;
	}
	
	public static void listen(String eventType, EventListener listener) {
		if(! listeners.containsKey(eventType)) {
			listeners.put(eventType, new ArrayList<EventListener>());
		}
		
		listeners.get(eventType).add(listener);
//		listeners.get(eventType).sort(new Comparator<EventListener>() {
//
//			@Override
//			public int compare(EventListener o1, EventListener o2) {
//				return o1.getPriority()-o2.getPriority();
//			}
//		});
	}
	
}
