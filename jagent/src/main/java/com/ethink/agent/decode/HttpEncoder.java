package com.ethink.agent.decode;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ethink.agent.task.bean.ServerTask;

/**
 * @类描述 ServerTask对象转为xml
 * @创建时间 2017年10月16日
 * @author wangluliang
 */
public class HttpEncoder implements Encoder {
	
	private final static Logger log = LoggerFactory.getLogger(HttpEncoder.class);
	
	@Override
	public String encode(Object obj) {
		ServerTask serverTask = null;
		if (obj instanceof ServerTask) {
			serverTask = (ServerTask) obj;
		}
		String xmlStr = xmlEncoder(serverTask);
		return xmlStr;
	}

	/**使用反射将bean转为xml字符串
	 * @param obj
	 * @return
	 */
	public <T> String xmlEncoder(T obj) {

		try {

			Document document = DocumentHelper.createDocument();

			Element root = document.addElement("root");// 添加根节点
			Field[] properties = obj.getClass().getDeclaredFields();// 获得实体类的所有属性

			for (int i = 0; i < properties.length; i++) {
				// 反射get方法
				Method meth = obj.getClass().getMethod("get" + properties[i].getName().substring(0, 1).toUpperCase()
						+ properties[i].getName().substring(1));
				// 为二级节点添加属性，属性值为对应属性的值
				Object objPro = meth.invoke(obj);
				if(objPro!=null){
					root.addElement(properties[i].getName()).setText(objPro.toString());
				}

			}
			return root.asXML();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public <T> String stringEncoder(T obj) {
		StringBuffer sb = new StringBuffer("");
		Field[] properties = obj.getClass().getDeclaredFields();// 获得实体类的所有属性

		for (int i = 0; i < properties.length; i++) {
			// 反射get方法
			Method meth;
			try {
				meth = obj.getClass().getMethod("get" + properties[i].getName().substring(0, 1).toUpperCase()
						+ properties[i].getName().substring(1));
				Object objPro = meth.invoke(obj);
				if(objPro!=null){
					String param = properties[i].getName()+"="+objPro.toString();
					if(!("".equals(sb.toString()))){
						param = "&"+param;
					}
					sb.append(param);
				}
			} catch (Exception e) {
				log.error("对象转成get请求参数字符串出错",e);
			}
			

		}
		return sb.toString();
	}

}
