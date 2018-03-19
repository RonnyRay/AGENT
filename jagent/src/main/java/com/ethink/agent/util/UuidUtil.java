package com.ethink.agent.util;
import java.util.UUID;
public class UuidUtil {
	//生成随机ID值
    public static String  uuid() {		  
	String uuid = UUID.randomUUID().toString().replaceAll("-", "");
	//System.out.println(uuid);	
	return uuid;
	}
}
