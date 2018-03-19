package com.ethink.agent.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtil {
	
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public static String getNowDateTime(){
		return sdf.format(new Date());
	}

	public static Date StringToTime(String time, String simpledf) throws ParseException {
		SimpleDateFormat sdfs = new SimpleDateFormat(simpledf);
		return sdfs.parse(time);
	}
}
