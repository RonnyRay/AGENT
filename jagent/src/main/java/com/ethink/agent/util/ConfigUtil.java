package com.ethink.agent.util;

import java.util.*;

public class ConfigUtil {
	
	private static final ResourceBundle bundle = ResourceBundle.getBundle("config");
	
	/**
	 * 获取指定属性值
	 * @param propertyName
	 * @return
	 */
	public static String getPropertyValue(String propertyName){
		return bundle.getString(propertyName);
	}
	
}
