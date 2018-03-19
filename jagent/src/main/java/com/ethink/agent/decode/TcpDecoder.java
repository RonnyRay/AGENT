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

import com.ethink.agent.Config;
import com.ethink.agent.task.bean.ServerTask;

public class TcpDecoder implements Decoder {
	private final static Logger log = LoggerFactory.getLogger(TcpDecoder.class);
	String devNum = Config.get(Config.DEV_NUM);

	@Override
	public List<ServerTask> decode(String xmlMess) throws Exception {
		// 报文格式：6位报文头长度+8位设备编号+XML报文域
		String devNumer = xmlMess.substring(0, 8);
		// 判断设备编号是否符合，不符合抛弃
		if (!isDevNum(devNumer)) {
			log.info("设备编号不符合,抛弃报文");
			return null;
		}
		;
		String xmlFields = xmlMess.substring(8);

		return xmlDecoder(xmlFields);
	}

	private List<ServerTask> xmlDecoder(String xmlFields) throws Exception {
		List<ServerTask> list = new ArrayList<ServerTask>();

		Document document = DocumentHelper.parseText(xmlFields);
		Element root = document.getRootElement();
		List taskList = root.elements();
		if (taskList.size() == 0) {
			return null;
		}
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
	 * 填充task的属性
	 * 
	 * @param node
	 * @param task
	 * @throws InvocationTargetException
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

	private boolean isDevNum(String devNumer) {

		return devNum.equals(devNumer);
	}

}
