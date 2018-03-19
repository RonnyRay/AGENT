package com.ethink.agent.annotation;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;

import com.ethink.agent.event.Event;

/**
 * @类描述 
 * @创建时间 2017年10月18日
 * @author wangluliang
 */
/**
 * @类描述 注解工具类
 * @创建时间 2017年10月18日
 * @author wangluliang
 */
public class AnnotationUtil {

	/**
	 * @获得指定包目录下的所有含有指定注解的类 
	 * @注解类型：TaskExecutors
	 * @return
	 */
	public static Map<String, Class<?>> getTaskExecutorMap(String packageName) {
		Map<String, Class<?>> map = new HashMap<String, Class<?>>();

		// 扫描指定包下，并拥有指定注解的类
		Reflections reflections = new Reflections(packageName);
		Set<Class<?>> classesList = reflections.getTypesAnnotatedWith(TaskExecutors.class);

		for (Class<?> clazz : classesList) {
			// 获得该类上所有的TaskExecutors注解
			TaskExecutors taskExecutor = (TaskExecutors) clazz.getAnnotation(TaskExecutors.class);
			if (taskExecutor != null) {
				map.put(taskExecutor.value(), clazz);
				// System.out.println(taskExecutor.value());
			}
		}
		return map;

	}


	/**
	 * @获得类中含有指定注解的属性，并生成映射表
	 * @注解类型：EventDecoder
	 * @param clazz
	 * @return
	 */
	public static Map<String, Class<?>> getDecoderMap(Class<?> clazz) {
		Map<String, Class<?>> map = new HashMap<String, Class<?>>();

		// 获得clazz所有的属性
		Field fields[] = clazz.getFields();
		for (Field field : fields) {

			// 获得属性上的EventDecoder注解
			EventDecoder eventDecoder = (EventDecoder) field.getAnnotation(EventDecoder.class);
			if (eventDecoder != null) {
				map.put(field.getName(), eventDecoder.value());
				//System.out.println(field.getName());
			}
		}

		return map;
	}

	public static void main(String[] args) {
		AnnotationUtil.getDecoderMap(Event.class);
	}

}
