/**
 * Ethink 2017 copyright
 * 
 */
package com.ethink.agent.task.bean;

/**
 *
 * 描述:
 * @author wangjing.dc@qq.com
 */
/**
 * @类描述 
 * @创建时间 2017年10月31日
 * @author wangluliang
 */
public class ServerTask extends Task{
	//任务类型
	private String tasktype;
	//planid连同taskid一起构成此次任务的全局唯一标识符
	private String planid;
	private String taskid;
	
	//本机8位设备编号
	private String termid;
	//任务计划执行时间
	private String exectime;
	//当前任务有效期，单位分钟
	private String valid;
	//文件服务器返回文件ID
	private String fileid;
	//任务状态
	private String taskstatus;
	//附加描述
	private String extradescription;
	//升级文件地址
	private String FileURL;
	//屏幕截取文件名
	private String filename;
	/** 文件校验MD5 */
	private String FileMD5;

	/** 完成百分比 */
	private String percent;
	//文件上传地址	
	private String filesvr;
	//查找类型
	private int SearchType;	
	//文件地址
	private String FilePath;
	//文件类型
	private String Wildcard;
	//比较类型
	private int CmpType;
	//搜索起始范围
	private String SearchRangeBegin;
	//搜索结束范围
	private String SearchRangeEnd;
	//否搜索子目录
	private String IncludeSubDir;
	public int getSearchType() {
		return SearchType;
	}
	public void setSearchType(int searchType) {
		SearchType = searchType;
	}
	public String getFilePath() {
		return FilePath;
	}
	public void setFilePath(String filePath) {
		FilePath = filePath;
	}
	public String getWildcard() {
		return Wildcard;
	}
	public void setWildcard(String wildcard) {
		Wildcard = wildcard;
	}
	public int getCmpType() {
		return CmpType;
	}
	public void setCmpType(int cmpType) {
		CmpType = cmpType;
	}
	public String getSearchRangeBegin() {
		return SearchRangeBegin;
	}
	public void setSearchRangeBegin(String searchRangeBegin) {
		SearchRangeBegin = searchRangeBegin;
	}
	public String getSearchRangeEnd() {
		return SearchRangeEnd;
	}
	public void setSearchRangeEnd(String searchRangeEnd) {
		SearchRangeEnd = searchRangeEnd;
	}
	public String getIncludeSubDir() {
		return IncludeSubDir;
	}
	public void setIncludeSubDir(String includeSubDir) {
		IncludeSubDir = includeSubDir;
	}
	

	public String getFilesvr() {
		return filesvr;
	}
	public void setFilesvr(String filesvr) {
		this.filesvr = filesvr;
	}

//-------------agent参数--------------
	/** 本地监听端口 */
	private String agentport;
	/** 任务服务器地址 */
	private String tasksvrip;
	/** 任务服务器端口 */
	private String tasksvrport;
	/** 推送服务器地址 */
	private String pushsvrip;
	/** 推送服务器端口 */
	private String pushsvrport;
	/** 电子流水地址 */
	private String ejsvr;
	/** 电子流水端口 */
	private String ejsvrport;
	/** 状态监控服务IP */
	private String amsip;
	/** 监控服务TCP端口 */
	private String amstcpport;
	/** 监控服务UDP端口 */
	private String amsudpport;
	/** 监控报文发送间隔（秒） */
	private String sendinterval;
	/** 1-上传电子流水，0-不上传电子流水 */
	private String ejupload;
	/** 只打印电子流水不打印纸质流水（ejupload必须为1才有效），0-打印电子流水和纸质流水 */
	private String ejonly;
	/** 电子流水丢失容忍笔数，硬盘未坏 */
	private String ejtorlerant;
	/** 电子流水丢失容忍笔数，硬盘已坏 */
	private String ejhddtolerant;
	/** 对账报文批量上传笔数 */
	private String transinfobatch;
	/** 上传文件大小限制（KB） */
	private String filelimit;
//---------------------------------
	
	
	public String getAgentport() {
		return agentport;
	}
	public void setAgentport(String agentport) {
		this.agentport = agentport;
	}
	public String getTasksvrip() {
		return tasksvrip;
	}
	public void setTasksvrip(String tasksvrip) {
		this.tasksvrip = tasksvrip;
	}
	public String getTasksvrport() {
		return tasksvrport;
	}
	public void setTasksvrport(String tasksvrport) {
		this.tasksvrport = tasksvrport;
	}
	public String getPushsvrip() {
		return pushsvrip;
	}
	public void setPushsvrip(String pushsvrip) {
		this.pushsvrip = pushsvrip;
	}
	public String getPushsvrport() {
		return pushsvrport;
	}
	public void setPushsvrport(String pushsvrport) {
		this.pushsvrport = pushsvrport;
	}
	public String getEjsvr() {
		return ejsvr;
	}
	public void setEjsvr(String ejsvr) {
		this.ejsvr = ejsvr;
	}
	public String getEjsvrport() {
		return ejsvrport;
	}
	public void setEjsvrport(String ejsvrport) {
		this.ejsvrport = ejsvrport;
	}
	public String getAmsip() {
		return amsip;
	}
	public void setAmsip(String amsip) {
		this.amsip = amsip;
	}
	public String getAmstcpport() {
		return amstcpport;
	}
	public void setAmstcpport(String amstcpport) {
		this.amstcpport = amstcpport;
	}
	public String getAmsudpport() {
		return amsudpport;
	}
	public void setAmsudpport(String amsudpport) {
		this.amsudpport = amsudpport;
	}
	public String getSendinterval() {
		return sendinterval;
	}
	public void setSendinterval(String sendinterval) {
		this.sendinterval = sendinterval;
	}
	public String getEjupload() {
		return ejupload;
	}
	public void setEjupload(String ejupload) {
		this.ejupload = ejupload;
	}
	public String getEjonly() {
		return ejonly;
	}
	public void setEjonly(String ejonly) {
		this.ejonly = ejonly;
	}
	public String getEjtorlerant() {
		return ejtorlerant;
	}
	public void setEjtorlerant(String ejtorlerant) {
		this.ejtorlerant = ejtorlerant;
	}
	public String getEjhddtolerant() {
		return ejhddtolerant;
	}
	public void setEjhddtolerant(String ejhddtolerant) {
		this.ejhddtolerant = ejhddtolerant;
	}
	public String getTransinfobatch() {
		return transinfobatch;
	}
	public void setTransinfobatch(String transinfobatch) {
		this.transinfobatch = transinfobatch;
	}
	public String getFilelimit() {
		return filelimit;
	}
	public void setFilelimit(String filelimit) {
		this.filelimit = filelimit;
	}

	public String getFileid() {
		return fileid;
	}
	public void setFileid(String fileid) {
		this.fileid = fileid;
	}
	public String getTaskstatus() {
		return taskstatus;
	}
	public void setTaskstatus(String taskstatus) {
		this.taskstatus = taskstatus;
	}
	public String getExtradescription() {
		return extradescription;
	}
	public void setExtradescription(String extradescription) {
		this.extradescription = extradescription;
	}

	public String getTasktype() {
		return tasktype;
	}
	public void setTasktype(String tasktype) {
		this.tasktype = tasktype;
	}
	public String getPlanid() {
		return planid;
	}
	public void setPlanid(String planid) {
		this.planid = planid;
	}
	public String getTaskid() {
		return taskid;
	}
	public void setTaskid(String taskid) {
		this.taskid = taskid;
	}
	public String getTermid() {
		return termid;
	}
	public void setTermid(String termid) {
		this.termid = termid;
	}
	public String getExectime() {
		return exectime;
	}
	public void setExectime(String exectime) {
		this.exectime = exectime;
	}
	public String getValid() {
		return valid;
	}
	public void setValid(String valid) {
		this.valid = valid;
	}
	public String getFileURL() {
		return FileURL;
	}
	public void setFileURL(String fileURL) {
		FileURL = fileURL;
	}
	public String getFileMD5() {
		return FileMD5;
	}
	public void setFileMD5(String fileMD5) {
		FileMD5 = fileMD5;
	}
	public String getPercent() {
		return percent;
	}
	public void setPercent(String percent) {
		this.percent = percent;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	
	
}
