package com.ethink.agent.util;

import java.io.*;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FtpUtil {

	private final static Logger LOG = LoggerFactory.getLogger(FtpUtil.class);
	// Ftp客户端对象
	private static FTPClient ftp = null;
	
	/**
	 * 连接Ftp服务器
	 * @return
	 */
	public static boolean connectFtp(String ftp_ip,int ftp_port,String ftp_username,
    		String ftp_password){
		ftp = new FTPClient();
		boolean result = false;
		try {
			// 连接
			ftp.connect(ftp_ip, ftp_port);
			// 登录
			ftp.login(ftp_username, ftp_password);
			// 判断连接结果
			if (!FTPReply.isPositiveCompletion(ftp.getReplyCode())) {
				LOG.error("连接ftp服务器失败");
				return result;
			}
			result = true;
		} catch (Exception e) {
			LOG.error(null, e);
		}
		LOG.info("连接ftp服务器成功");
		return result;
	}
	
	/**
	 * 关闭Ftp服务器
	 * @return
	 */
	public static void closeFtp(){
		if (ftp != null) {
			try {
				ftp.logout();
			} catch (IOException e) {
				LOG.error("closeFtp.ftp.logout failed : ", e);
			}
			if (ftp.isConnected()) {
				try {
					ftp.disconnect();
				} catch (IOException ioe) {
					LOG.error("closeFtp.ftp.disconnect failed : ", ioe);
				}
			}
		}
	}

	
	/**
	 * 下载文件
	 * @param ftp_ip ip地址
	 * @param ftp_port 端口
	 * @param ftp_username 用户名
	 * @param ftp_password 密码
	 * @param ftp_downloadpath 下载目录
	 * @param filePath 下载保存目录
	 * @param file 将要下载的文件
	 */
    public static boolean downloadFile(String ftp_ip, int ftp_port, String ftp_username,
    		String ftp_password, String ftp_downloadpath, String filePath, String file) {
    	boolean result = false;
        InputStream input = null;
        FileOutputStream os = null;
        try {
        	if(!connectFtp(ftp_ip, ftp_port, ftp_username, ftp_password)){
				return result;
			}
        	if (!ftp.changeWorkingDirectory(ftp_downloadpath)) {   
        		LOG.info("ftp 目录不存在");   
        		return result;
        	}   
        	input = ftp.retrieveFileStream(file); 
        	File dirFile = new File(filePath);
			if (!dirFile.exists()) {
				dirFile.mkdir();
			}
        	os = new FileOutputStream(filePath+File.separator+file);
        	byte[] buffer = new byte[4 * 1024];
        	int read;
        	while ((read = input.read(buffer)) > 0) {
				os.write(buffer, 0, read);
			}
        	LOG.info("文件下载成功");
        	result = true;
        }catch (Exception e) {
			LOG.error("文件下载失败 : " + e);
		} finally {
			if (ftp != null) {
				try {
					os.flush();
					os.close();
					input.close();
				} catch (Exception e) {
					LOG.error("关闭下载流失败",e);
				}
			}
			closeFtp();
		}
        return result;
    }   
    
}
