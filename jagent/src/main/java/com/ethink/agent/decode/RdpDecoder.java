package com.ethink.agent.decode;

import com.ethink.agent.task.bean.RDPTask;
import com.ethink.agent.util.UuidUtil;

/* @类描述：解析Rdp报文并返回rdpTask对象
 * @date: 2017年10月16日
 * @author: dingfan
 */
public class RdpDecoder implements Decoder {
	@Override
	public RDPTask decode(String code) {		
		 RDPTask rdpTask = new RDPTask();	    
		  String[] tcpDates = code.split("\\|");
	      for ( int i = 0; i < tcpDates.length; i++) {
	      	switch (i) {
	      	   case 0:
					rdpTask.setCMDRESULT(tcpDates[i]);					
					break;					
				case 1:
					rdpTask.setTRNEJ(tcpDates[i]);						
					break;
				case 2:
					rdpTask.setCTIME(tcpDates[i]);						
					break;
				case 3:
					rdpTask.setSOF_VER(tcpDates[i]);						
					break;
				case 4:
					rdpTask.setADV_VER(tcpDates[i]);					
					break;
				case 5:
					rdpTask.setCFG_VER(tcpDates[i]);
					break;
				case 6:
					rdpTask.setDEV_NUM(tcpDates[i]);
					break;										
				default:
					break;
				}	            	
			}
	      rdpTask.setTaskType("RDPStartExecutor");
	      rdpTask.setTaskId(UuidUtil.uuid());
		 return rdpTask;
	}
}
