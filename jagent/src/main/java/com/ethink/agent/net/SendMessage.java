package com.ethink.agent.net;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HTTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ethink.agent.util.HttpUtil;

/**
 * @类描述 上传任务状态
 * @创建时间 2017年10月16日
 * @author wangluliang
 */
public class SendMessage {
	
	/** RDP报文发送 */
	public static final String TYPE_RDP = "TYPE_RDP";
	
	/** HTTP报文 GET请求发送 */
	public static final String TYPE_HTTP_GET = "TYPE_HTTP_GET";
	
	/** HTTP报文POST请求发送 */
	public static final String TYPE_HTTP_POST = "TYPE_HTTP_POST";

	private final static Logger log = LoggerFactory.getLogger(SendMessage.class);
	
	/**
	 * 发送请求并获得响应，抛出异常说明发送失败，响应值可能为null
	 * @param url 请求地址
	 * @param params 请求参数String
	 * @param messtype 报文发送类型
	 * @return response字符串
	 * @throws Exception 
	 */
	public static String send(String url,String params,String messtype) throws Exception{
		String response = "";
		if(TYPE_RDP.equals(messtype)){
			RDPMessageSend.send(params);
		}else if(TYPE_HTTP_GET.equals(messtype)){
			response = HttpUtil.sendGetRequest(url, params);
		}else if(TYPE_HTTP_POST.equals(messtype)){
			response = new SendMessage().httpSendPost(url,params);
		}
		return response;
	}
	
	/**
	 * HTTP报文POST请求发送
	 * @param url
	 * @param params
	 * @return
	 */
	private  String httpSendPost(String url,String params) {
		StringBuffer sb = new StringBuffer();
		CloseableHttpClient httpclient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		HttpPost httpPost = new HttpPost(url);
		//设置报文头
		httpPost.setHeader(HTTP.CONTENT_TYPE, "application/xml; charset=utf-8");
		//设置post参数
		StringEntity param = new StringEntity(params, ContentType.APPLICATION_JSON);
		httpPost.setEntity(param);
		InputStream is = null;
		BufferedReader br = null;
		try {
			response = httpclient.execute(httpPost);
			HttpEntity entity = response.getEntity();
			if(entity != null){
				is = entity.getContent();
				br = new BufferedReader(new InputStreamReader(is));
				String info = null;
				while((info=br.readLine())!=null){
					sb.append(info);
				}
				return sb.toString();
			}
		} catch (Exception e) {
			log.error("sendPostRequest error:", e);
		}finally {
			try {
				//关闭连接，释放资源
				is.close();
				br.close();
				response.close();
				if(httpclient!=null){
					httpclient.close();
				}
			} catch (Exception e) {
				log.error("HttpClient close error:", e);
			}
		}
		return sb.toString();
		
	}
	
}
