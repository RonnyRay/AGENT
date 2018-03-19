package com.ethink.agent.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @类描述 TaskExecutors注解
 * @创建时间 2017年10月18日
 * @author wangluliang
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface TaskExecutors {

	/**
	 * 该Task执行器中所使用task对象的taskType属性
	 * @return
	 */
	public String value();
	
}
