package com.ethink.agent;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
*
* 描述:流水号生成器
* @author chenquanhong
*/
public class SerialNumber{
	
	private static StringBuilder serialNumber = new StringBuilder(6).append(new SimpleDateFormat("dd").format(new Date()) + "0000");
		
	public static String getSerialNumber(){
		
		String day = new SimpleDateFormat("dd").format(new Date());
		
        if(serialNumber == null || "".equals(serialNumber) || !day.equals(serialNumber.substring(0, 2))){
        	serialNumber = serialNumber.delete(0, serialNumber.length()).append(day + "0000");
        }else{
        	
        	int number = Integer.parseInt(serialNumber.substring(2));
        	
        	if(number >= 9999){
        		number = 0;
        	}
        	
        	serialNumber = serialNumber.delete(2, serialNumber.length()).append(String.format("%04d", number+ 1));
        }
        
        return serialNumber.toString();
	}
	
	public static void main(String[] args){
		
		for (int i = 0; i < 1; i++){
			System.out.println(i + " , " + SerialNumber.getSerialNumber());
		}
		
	}
}