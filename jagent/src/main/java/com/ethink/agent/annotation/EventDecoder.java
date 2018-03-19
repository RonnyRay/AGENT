package com.ethink.agent.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * @类描述 EventDecoder注解
 * @创建时间 2017年10月18日
 * @author wangluliang
 */
@Target(ElementType.FIELD)//元注解
@Retention(RetentionPolicy.RUNTIME)
public @interface EventDecoder {

	/**
	 * 该event事件解析成Task时,所对应的Decoder类
	 * @return
	 */
	public Class<?> value();
}
