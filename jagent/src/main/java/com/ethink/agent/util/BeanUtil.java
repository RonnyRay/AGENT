package com.ethink.agent.util;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.ethink.agent.task.bean.ServerTask;

/**
 * @类描述 bean对象工具
 * @创建时间 2017年10月25日
 * @author wangluliang
 */
public class BeanUtil {


	/**
	 * 筛选bean里需要的字段
	 * 
	 * @param bean
	 * @param params(需要的字段)
	 * @return
	 * @throws Exception
	 */
	public static Object filterBean(Object bean, String[] params) throws Exception {
		Object newbean = bean.getClass().newInstance();
		Map<String, Object> mapField = new HashMap<String, Object>();
		// 包括父类
		for (Class<?> clazz = bean.getClass(); clazz != Object.class; clazz = clazz.getSuperclass()) {
			Method methods[] = clazz.getDeclaredMethods();
			for (Method method : methods) {
				String methodName = method.getName();
				// 获得get方法
				if ("get".equals(methodName.substring(0, 3))) {
					Object field = method.invoke(bean);
					if(field!=null){
						mapField.put(methodName.substring(3), field);						
					}
				}
			}
			for (Method method : methods) {
				String methodName = method.getName();
				// 获得set方法
				if ("set".equals(methodName.substring(0, 3))) {
					//首字母小写
					String fieldname = methodName.substring(3, 4).toLowerCase() + methodName.substring(4);
					if (StringArrayContains(params, fieldname)||StringArrayContains(params, methodName.substring(3))) {
						Object fieldGet = mapField.get(methodName.substring(3));
						if(fieldGet!=null){
							method.invoke(newbean, fieldGet);
						}
					}
				}
			}
		}

		return newbean;

	}

	/**
	 * 筛选bean里需要的字段，不需要的置为null 忽视大小写
	 * 
	 * @param bean
	 * @param params(需要的字段)
	 * @return
	 * @throws Exception
	 */
	public static Object filterBeanIgnoreCase(Object bean, String[] params) throws Exception {
		Object newbean = bean.getClass().newInstance();
		Map<String, Object> mapField = new HashMap<String, Object>();
		// 包括父类
		for (Class<?> clazz = bean.getClass(); clazz != Object.class; clazz = clazz.getSuperclass()) {
			Method methods[] = clazz.getDeclaredMethods();
			for (Method method : methods) {
				String methodName = method.getName();
				// 获得get方法
				if ("get".equals(methodName.substring(0, 3))) {
					Object field = method.invoke(bean);
					if(field!=null){
						mapField.put(methodName.substring(3), field);						
					}
				}
			}
			for (Method method : methods) {
				String methodName = method.getName();
				// 获得set方法
				if ("set".equals(methodName.substring(0, 3))) {
					//首字母小写
					String fieldname = methodName.substring(3, 4).toLowerCase() + methodName.substring(4);
					if (StringArrayContainsIgnoreCase(params, fieldname)) {
						Object fieldGet = mapField.get(methodName.substring(3));
						if(fieldGet!=null){
							method.invoke(newbean, fieldGet);
						}
					}
				}
			}
		}

		return newbean;

	}

	private static boolean StringArrayContains(String[] params, String str) {
		for (String param : params) {
			if (param.equals(str)) {
				return true;
			}
		}
		return false;
	}

	private static boolean StringArrayContainsIgnoreCase(String[] params, String str) {
		for (String param : params) {
			if (param.equalsIgnoreCase(str)) {
				return true;
			}
		}
		return false;
	}

	public static void main(String[] args) {
		String filterField[] = { "TaskType", "planid", "taskid", "termid", "fileid", "taskstatus", "percent",
				"FileURL" };
		ServerTask ser = new ServerTask();
		ser.setFileid("field");
		ser.setTaskId("taskId");
		ser.setTaskid("taskid");
		ser.setTaskType("TaskType");
		ser.setTaskData("taskData");
		ser.setFileURL("FileURL");
		try {
			ServerTask newT = (ServerTask) BeanUtil.filterBeanIgnoreCase(ser, filterField);
			System.out.println(newT);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
