package com.ethink.agent.decode;


/* @类描述：Agent收到下发报文快速回复
 * @date: 2017年10月24日
 * @author: dingfan
 *
 */
public class AgentFastResponse {
    public static String fastResponse(String data){
	   	
    	String   responseData=""; 	
    	responseData="00|"+data.substring(0, 29)+"";    	
    	return responseData;
    	
    }
    public static void main(String[] args) {
		String  abc="301000|123456|20171023162320|RDP-1.0.9|ADV-2012-3-29|CFG-1.0.9";
		System.out.println(fastResponse(abc));
    }
}
