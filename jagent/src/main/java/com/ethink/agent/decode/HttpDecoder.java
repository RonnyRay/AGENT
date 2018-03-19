package com.ethink.agent.decode;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ethink.agent.task.bean.ServerTask;

/**
 * @类描述 http报文解析
 * @创建时间 2017年10月12日
 * @author wangluliang
 */
public class HttpDecoder implements Decoder {
	private final static Logger log = LoggerFactory.getLogger(HttpDecoder.class);

	@Override
	public List<ServerTask> decode(String info) throws Exception {

		return xmlDecoder(info);
	}

	/**
	 * 解析xml域
	 * 
	 * @param xmlFields
	 * @return
	 * @throws Exception
	 */
	private List<ServerTask> xmlDecoder(String xmlFields) throws Exception {
		List<ServerTask> list = new ArrayList<ServerTask>();

		Document document = DocumentHelper.parseText(xmlFields);
		Element root = document.getRootElement();
		List taskList = root.elements();
		if (taskList.size() == 0) {
			return null;
		}
		// 遍历disttask，添加task对象
		for (Iterator iterator = taskList.iterator(); iterator.hasNext();) {
			Element element = (Element) iterator.next();
			if ("disttask".equals(element.getName())) {
				ServerTask task = new ServerTask();
				listNodes(element, task);
				task.setTaskType(task.getTasktype());
				task.setExecuteTime(task.getExectime());
				task.setTaskId(task.getTaskid());
				// 获得每个任务的原始报文（带 root）
				String taskdata = "<root>" + element.asXML() + "</root>";
				task.setTaskData(taskdata);

				list.add(task);
			}
		}

		return list;
	}

	private void listNodes(Element node, ServerTask task) throws Exception {
		List<Element> listElement = node.elements();
		if (listElement.size() == 0) {
			setTask(node, task);
		}
		for (Element e : listElement) {
			// 递归填充task
			listNodes(e, task);
		}

	}

	/**
	 * 填充所列举的task的属性
	 * 
	 * @param node
	 * @param task
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	private void setTask(Element node, ServerTask task) throws Exception {
		String nodename = node.getName();
		String nodeText = node.getTextTrim();

		Class<? extends ServerTask> clazz = task.getClass();
		Method methods[] = clazz.getDeclaredMethods();
		for (Method method : methods) {
			// 获得set方法
			String methodName = method.getName();
			if ("set".equals(methodName.substring(0, 3))) {
				String fieldname = methodName.substring(3, 4).toLowerCase() + methodName.substring(4);
				if (fieldname.substring(0, 1).equalsIgnoreCase(nodename.substring(0, 1))
						&& fieldname.substring(1).equals(nodename.substring(1))) {
					method.invoke(task, nodeText);

				}
			}
		}
	}

}
