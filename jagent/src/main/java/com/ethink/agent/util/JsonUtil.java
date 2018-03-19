package com.ethink.agent.util;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtil {
	
	private final static Logger LOG = LoggerFactory.getLogger(JsonUtil.class);
	private final static ObjectMapper objectMapper = new ObjectMapper();
	
	/**
	 * Map转Json字符串
	 * @param map
	 * @return
	 */
	public static String MapParseToJsonStr(Map<String,Object> map){
		
		String result = null;
		try {
			result = objectMapper.writeValueAsString(map);
		} catch (Exception e) {
			LOG.error("Map转Json字符串时发生异常,map="+map, e.getMessage());
		}
		return result;
	}
	
	/**
	 * Json字符串转Map
	 * @param map
	 * @return
	 */
	public static Map<String,Object> JsonStrParseToMap(String jsonStr){
		Map<String,Object> result = null;
		try {
			result = objectMapper.readValue(jsonStr, Map.class);
		} catch (Exception e) {
			LOG.error("Json字符串转Map时发生异常,jsonStr="+jsonStr, e.getMessage());
		}
		return result;
	}
	
	
}
