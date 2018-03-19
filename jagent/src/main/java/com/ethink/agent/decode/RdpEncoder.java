package com.ethink.agent.decode;

import com.ethink.agent.task.bean.RDPTask;

public class RdpEncoder implements Encoder {
	@Override
	public String encode(Object obj) {
		StringBuffer stringBuffer = new StringBuffer();
		RDPTask rdpTask = (RDPTask) obj;
		
		if (null!=rdpTask.getTRNCODE()&& !"".equals(rdpTask.getTRNCODE())) {
			stringBuffer.append(rdpTask.getTRNCODE().trim()+"|");
		}
		if (null!=rdpTask.getTRNEJ()&& !"".equals(rdpTask.getTRNEJ())) {
			stringBuffer.append(rdpTask.getTRNEJ().trim()+"|");
		}
		if (null!=rdpTask.getCTIME()&& !"".equals(rdpTask.getCTIME())) {
			stringBuffer.append(rdpTask.getCTIME().trim());
		}		
		return stringBuffer.toString();
		
	}
}
