package com.ias.assembly.zkpro.zk.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 确定当前这个类或是方法需要进行任务单线程锁定
 * 类与方法级别
 * @author hujiuzhou
 *
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SingleTaskLock {
	String value() default "";
	/**
	 * 超时毫秒数,任务执行超过这个时间，将会发送告警信息
	 * @return
	 */
	long timeout() default 0;
	
}
