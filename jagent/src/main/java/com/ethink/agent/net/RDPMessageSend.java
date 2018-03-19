package com.ethink.agent.net;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.Socket;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ethink.agent.Config;
import com.ethink.agent.util.Util;

/* @类描述：RDP报文发送
 * @date: 2017年10月16日
 * @author: dingfan
 *
 */
public class RDPMessageSend{
	
	private final static Logger log = LoggerFactory.getLogger(RDPMessageSend.class);	
	
	public static String send(String rdpMessage) throws ConnectException {
		try {
			 //RDP报文发送端口
			String rdp_Port = Config.get(Config.RDP_PORT);
			int rdp_PORT = Integer.parseInt(rdp_Port);
			Socket socket = new Socket("127.0.0.1", rdp_PORT);
			log.info("向127.0.0.1发送报文，端口号为："+rdp_Port);
			OutputStream os = socket.getOutputStream();
			
			// 添加结束符
			rdpMessage = rdpMessage + "\n";
			log.info("向地址["+socket.getRemoteSocketAddress()+"]发送报文:::" + rdpMessage);
			IOUtils.write(rdpMessage, os, "utf-8");
			os.flush();
			
			InputStream is = socket.getInputStream();
			String result = Util.readInput(is, 4, "utf-8");
			log.info("收到返回报文:::" + result);
			
			is.close();
			os.close();
			socket.close();
			
			return result;
		} catch (ConnectException e) {
			throw e;
		} catch (IOException e) {
			log.error(null, e);
		}
		
		return null;
	}	 
}