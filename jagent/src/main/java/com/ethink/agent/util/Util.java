package com.ethink.agent.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import sun.nio.ch.FileChannelImpl;
public class Util {
	
	private static final Logger log = LoggerFactory.getLogger(Util.class);
	
	/**16进制字符*/
	protected static char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
	/**md5算法实例*/
	protected static MessageDigest messagedigest = null;
	
	/**
	 * 计算文件的MD5
	 * @param file
	 * @return md5
	 * @throws IOException 
	 * @throws NoSuchAlgorithmException 
	 */
	
	public static String md5(File file) {
		FileInputStream in = null;

		try {
			if (messagedigest == null)
				messagedigest = MessageDigest.getInstance("MD5");

			in = new FileInputStream(file);
			FileChannel ch = in.getChannel();
			MappedByteBuffer byteBuffer = ch.map(FileChannel.MapMode.READ_ONLY,
					0, file.length());
			messagedigest.update(byteBuffer);
			byte[] bytes = messagedigest.digest();
			String hex = byteToHex(bytes, 0, bytes.length);
			ch.close();
			// 手动unmap,解除文件资源占用  
		    Method method = FileChannelImpl.class.getDeclaredMethod("unmap", MappedByteBuffer.class);  
		    method.setAccessible(true);  
		    method.invoke(FileChannelImpl.class, byteBuffer);
			
			return hex;
		} catch (Exception e) {
			log.error(null, e);
			return null;
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				log.error(null, e);
			}
		}
	}
	
	/**
	 * 字节数据转为hex
	 * @param bytes 字节数组
	 * @param m 开始下标
	 * @param n 长度
	 * @return hex字符串
	 */
	private static String byteToHex(byte bytes[], int m, int n) {
		StringBuffer stringbuffer = new StringBuffer(2 * n);
		int k = m + n;
		for (int l = m; l < k; l++) {
			byte bt = bytes[l];
			char c0 = hexDigits[(bt & 0xf0) >> 4];
			char c1 = hexDigits[bt & 0xf];
			stringbuffer.append(c0);
			stringbuffer.append(c1);
		}
		return stringbuffer.toString();
	}
	
	

	/** 
	 * 获取异常的堆栈信息 
	 *  
	 * @param t 
	 * @return 
	 */  
	public static String getStackTrace(Throwable t)  
	{  
	    StringWriter sw = new StringWriter();  
	    PrintWriter pw = new PrintWriter(sw);  
	  
	    try  
	    {  
	        t.printStackTrace(pw);  
	        return sw.toString();  
	    }  
	    finally  
	    {  
	        pw.close();  
	    }  
	} 
	
	/**
	 * 从params中获取头信息集合。
	 * params
	 */
	@SuppressWarnings("rawtypes")
	public static Map<String, Object> getHeadParams(Map params){
		String commons = "USER_ID," //服务
		   +"USER_NAME,"//DAO
		   +"ORG_ID,"//sql标识
		   +"ORG_NAME,"//是否分页
		   +"DEVICE_ID,"//是否分页
		   +"ACCOUNT_ID,"//成功状态
		   +"LOGTIME";//返回信息
				
		Map<String, Object> commParams = new HashMap<String, Object>();
		Iterator keys = params.keySet().iterator();
		
		while(keys.hasNext()){
			String key = (String) keys.next();
			if(commons.indexOf(key) > -1){
				commParams.put(key, params.get(key));
			}
		}
		
		return commParams;
	}

	/**
	 * 获取客户端IP
	 * @param request
	 * @return
	 */
	 public static String getClientIP(HttpServletRequest request) {  
	        String ip = request.getHeader("x-forwarded-for");  
	        if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {  
	            ip = request.getHeader("Proxy-Client-IP");  
	        }  
	        if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {  
	            ip = request.getHeader("WL-Proxy-Client-IP");  
	        }  
	        if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {  
	            ip = request.getRemoteAddr();  
	        }  
	        return ip;  
	    }  
	
	/**
	 * 获取全球唯一32位uuid 
	 * @return
	 */
	public static String getUUID(){
		UUID uuid = UUID.randomUUID();
		return uuid.toString().replaceAll("-", "");
	}
	
	/**
	 * 获取当前时间
	 * @return
	 */
	public static String getCurrentTime() {
		java.text.DateFormat format = new java.text.SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		String time = format.format(new Date());
		return time;
	}
	
   /**
    * 判断是否是数字
    * @param str
    * @return false 非数字， true 数字
    */
	public static boolean isNumeric(String str){ 
	    Pattern pattern = Pattern.compile("[0-9]*"); 
	    return pattern.matcher(str).matches();    
	} 
	
	/**
	 * 计算百分比
	 * @param x
	 * @param total
	 * @return
	 */
	public static String getPercent(int x,int total){  
		Double xdouble = x * 1.0d;
		String percent = String.valueOf(Math.round(xdouble / total * 100));
		return percent;
	}  
	

    /**
     * 
     * @return 当前系统是否为windows系统
     */
    public static boolean isWindow(){
    	return System.getProperty("os.name").toLowerCase().startsWith("windows");
    }
    
    /**
     * 
     * @return 当前系统是否为linux系统
     */
    public static boolean isLinux(){
    	return System.getProperty("os.name").toLowerCase().startsWith("linux");
    }
    
	public static void copyStream(InputStream in, OutputStream out) throws IOException {
		final int MAX = 4096;
		byte[] buf = new byte[MAX];
		for (int bytesRead = in.read(buf, 0, MAX); bytesRead != -1; bytesRead = in.read(buf, 0, MAX)) {
			out.write(buf, 0, bytesRead);
		}
	}
	
	
	public static void main(String[] args){
		long begin = System.currentTimeMillis();

		File big = new File("D:\\software\\gostimage/Softwares.iso");

		String md5 = md5(big);

		long end = System.currentTimeMillis();
		System.out.println("md5:" + md5 + " time:" + ((end - begin) / 1000)
				+ "s");
	}

	/**
	 * 读取附带报文长度的input数据
	 * @param is  InputStream
	 * @param msgLength 报文长度
	 * @param encode 字符编码
	 * @return 
	 */
	public static String readInput(InputStream input, int msgLength, String encoding) {
		
		int len = 0;
		try {
			byte[] length = new byte[4];
			input.read(length, 0, msgLength);
			len = Integer.valueOf(new String(length));
		}catch(Exception e) {
			log.error("报文长度读取失败", e);
			return null;
		}
		
		try {
			byte[] msg = new byte[len];
			input.read(msg, 0, len);
			return new String(msg);
		} catch (IOException e) {
			log.error("报文体读取失败", e);
		}
        
		return null;
	}
	
}
