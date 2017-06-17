package com.ias.assembly.zkpro.zk.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * 明确某个定时方法，不需要进行单任务锁定，可以并发执行
 * 在加上此注解之前，一定要想好所有的可能性，如因为time out造成的并发调用，不会影响到业务逻辑
 * @author hujiuzhou
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TaskNoLock {
	String value() default "";
}
