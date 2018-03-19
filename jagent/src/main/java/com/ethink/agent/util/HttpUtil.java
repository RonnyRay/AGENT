package com.ethink.agent.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HTTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

public class HttpUtil {

	private final static Logger LOG = LoggerFactory.getLogger(HttpUtil.class);

	/**
	 * 发送post请求
	 * 
	 * @param url
	 *            请求地址
	 * @param params
	 *            请求参数
	 * @return 响应数据
	 */
	public static Map<String, Object> sendPostRequest(String url, String params) {
		Map<String, Object> resultMap = null;
		System.out.println("url:" + url);
		System.out.println("params:" + params);
		// 创建默认的httpclient实例
		CloseableHttpClient httpclient = getHttpClient();
		CloseableHttpResponse response = null;
		// 创建httpPost
		HttpPost httpPost = new HttpPost(url);
		httpPost.setHeader(HTTP.CONTENT_TYPE, "application/json; charset=utf-8");
		try {
			StringEntity param = new StringEntity(params, ContentType.APPLICATION_JSON);
			httpPost.setEntity(param);
			LOG.info("即将要执行的post请求: " + httpPost.getURI());
			// Header[] headers = httpPost.getAllHeaders();
			response = httpclient.execute(httpPost);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream is = entity.getContent();
				String json = IOUtils.toString(is);
				LOG.info(json);
				ObjectMapper objectMapper = new ObjectMapper();
				resultMap = objectMapper.readValue(json, Map.class);
			}

		} catch (Exception e) {
			LOG.error("sendPostRequest error:", e);
		} finally {
			try {
				// 关闭连接，释放资源
				response.close();
				closeHttpClient(httpclient);
			} catch (Exception e) {
				LOG.error("HttpClient close error:", e);
			}
		}
		return resultMap;
	}

	/**
	 * 发送Get请求
	 * 
	 * @param url
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public static String sendGetRequest(String url, String param) throws Exception {
		StringBuffer sb = new StringBuffer();
		CloseableHttpClient httpclient = getHttpClient();
		CloseableHttpResponse response = null;
		String params = param.replaceAll(" ", "%20");
		System.out.println("*******************" + params);
		HttpGet httpget = new HttpGet(url + "?" + params);
		// 设置请求头
		httpget.setHeader(HTTP.CONTENT_TYPE, "text/html;charset=utf-8");
		InputStream is = null;
		BufferedReader br = null;
		try {
			response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				is = entity.getContent();
				br = new BufferedReader(new InputStreamReader(is));
				String info = null;
				while ((info = br.readLine()) != null) {
					sb.append(info);
				}
				return sb.toString();
			}
		} catch (Exception e) {
			throw e;
		} finally {
			// 关闭连接，释放资源
			is.close();
			br.close();
			response.close();
			closeHttpClient(httpclient);
		}
		return sb.toString();
	}

	/**
	 * 创建http客户端对象
	 * 
	 * @return
	 */
	private static CloseableHttpClient getHttpClient() {
		return HttpClients.createDefault();
	}

	/**
	 * 关闭http客户端
	 * 
	 * @param client
	 *            客户端对象
	 * @throws IOException
	 */
	private static void closeHttpClient(CloseableHttpClient client) throws IOException {
		if (client != null) {
			client.close();
		}
	}

	/**
	 * 文件下载
	 * 
	 * @param downUrl
	 *            下载地址
	 * @param filePath
	 *            下载保存路径
	 * @param file
	 *            文件保存名称
	 */
	public static boolean downloadFile(String downUrl, String filePath, String file) {
		boolean result = false;
		URL url;
		InputStream in = null;
		FileOutputStream os = null;
		try {
			url = new URL(downUrl);
			File dirFile = new File(filePath);
			if (!dirFile.exists()) {
				dirFile.mkdir();
			}
			URLConnection connection = url.openConnection();
			in = connection.getInputStream();
			os = new FileOutputStream(filePath + File.separator + file);
			byte[] buffer = new byte[4 * 1024];
			int read;
			while ((read = in.read(buffer)) > 0) {
				os.write(buffer, 0, read);
			}
			LOG.info("文件下载成功");
			result = true;
		} catch (Exception e) {
			LOG.error("文件下载失败 : " + e.getMessage());
		} finally {
			try {
				// 关闭连接，释放资源
				os.close();
				in.close();
			} catch (Exception e) {
				LOG.error("关闭下载流失败", e);
			}
		}
		return result;
	}
}
