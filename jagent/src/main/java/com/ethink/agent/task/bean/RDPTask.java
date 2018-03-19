/**
 * Ethink 2017 copyright
 * 
 */
package com.ethink.agent.task.bean;


/* @类描述：RDP收发参数pojo
 * @date: 2017年10月16日
 * @author: dingfan
 *
 */
public class RDPTask extends Task{
	//命令代码
	private String TRNCODE;	
	
	//命令流水号
	private String TRNEJ;
	//C端发包时间
	private String CTIME;
	//软件版本号
	private String SOF_VER ;
	//广告版本号
	private String ADV_VER;  
	//配置版本号
	private String CFG_VER;
	//失败原因
	private String FAIL_RESULT;
	//执行命令结果
	private String CMDRESULT;
	//设备号
	private String DEV_NUM;
	public String getDEV_NUM() {
		return DEV_NUM;
	}
	public void setDEV_NUM(String dEV_NUM) {
		DEV_NUM = dEV_NUM;
	}
	public String getCMDRESULT() {
		return CMDRESULT;
	}
	public void setCMDRESULT(String cMDRESULT) {
		CMDRESULT = cMDRESULT;
	}
	public String getFAIL_RESULT() {
		return FAIL_RESULT;
	}
	public void setFAIL_RESULT(String fAIL_RESULT) {
		FAIL_RESULT = fAIL_RESULT;
	}
	 public String getTRNCODE() {
		return TRNCODE;
	}
	public void setTRNCODE(String tRNCODE) {
		TRNCODE = tRNCODE;
	}
	public String getTRNEJ() {
		return TRNEJ;
	}
	public void setTRNEJ(String tRNEJ) {
		TRNEJ = tRNEJ;
	}
	public String getCTIME() {
		return CTIME;
	}
	public void setCTIME(String cTIME) {
		CTIME = cTIME;
	}
	public String getSOF_VER() {
		return SOF_VER;
	}
	public void setSOF_VER(String sOF_VER) {
		SOF_VER = sOF_VER;
	}
	public String getADV_VER() {
		return ADV_VER;
	}
	public void setADV_VER(String aDV_VER) {
		ADV_VER = aDV_VER;
	}
	public String getCFG_VER() {
		return CFG_VER;
	}
	public void setCFG_VER(String cFG_VER) {
		CFG_VER = cFG_VER;
	}
	@Override
	public String toString() {
		return "RDPTask [TRNCODE=" + TRNCODE + ", TRNEJ=" + TRNEJ + ", CTIME=" + CTIME + ", SOF_VER=" + SOF_VER
				+ ", ADV_VER=" + ADV_VER + ", CFG_VER=" + CFG_VER + ", FAIL_RESULT=" + FAIL_RESULT + ", CMDRESULT="
				+ CMDRESULT + ", DEV_NUM=" + DEV_NUM + "]";
	}
	
}
