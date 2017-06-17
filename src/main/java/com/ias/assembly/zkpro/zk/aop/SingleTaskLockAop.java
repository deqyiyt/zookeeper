package com.ias.assembly.zkpro.zk.aop;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Date;

import org.I0Itec.zkclient.ZkClient;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ias.assembly.zkpro.zk.annotation.SingleTaskLock;
import com.ias.assembly.zkpro.zk.common.TaskControl;
import com.ias.assembly.zkpro.zk.task.TaskLogManager;
import com.ias.assembly.zkpro.zk.task.TaskWarningManager;
import com.ias.assembly.zkpro.zk.util.IpUtil;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
public class SingleTaskLockAop {

	@Autowired
	private ZkClient zkClient;

	@Autowired(required = false)
	private TaskWarningManager taskWarningManager;

	@Autowired(required = false)
	private TaskLogManager taskLogManager;

	@Value("${zk.cofing.zkKeyStart:/zktask/}")
	private String zkKeyStart;

	/**
	 * 生成key的规则
	 * @param pjp
	 * @return
	 */
	private String makeKey(ProceedingJoinPoint pjp) {
		MethodSignature ms = (MethodSignature) pjp.getSignature();
		StringBuffer key = new StringBuffer();
		key.append(zkKeyStart);
		key.append(ms.getDeclaringTypeName());
		key.append("-");
		key.append(ms.getMethod().getName());
		if (pjp.getArgs() != null) {
			for (Object obj : pjp.getArgs()) {
				if (obj != null && !(obj instanceof Date)) {
					String str = obj.toString().replace(" ", "-");
					key.append("-");
					key.append(str);
				}
			}
		}
		return key.toString();
	}

	/**
	 * 输出日志
	 * @param con
	 */
	private void info(String con, boolean isPersist) {
		log.info(con);
		if (true == isPersist) {
			if (taskLogManager != null)
				taskLogManager.log(con, SingleTaskLockAop.class);
		}
	}

	/**
	 * 告警邮件
	 * @param pjp
	 */
	private void alert(ProceedingJoinPoint pjp) {
		MethodSignature ms = (MethodSignature) pjp.getSignature();
		String warningCon = "[timeout]task timeout daemon--method:" + ms.getMethod().getName() + " class:"
				+ pjp.getSignature().getDeclaringTypeName() + " ip:" + IpUtil.getIp();
		info(warningCon, true);
		if (taskWarningManager != null)
			taskWarningManager.warning(warningCon, SingleTaskLockAop.class);
	}

	/**
	 * 检查是否有超时的情况 有任务已经在执行，直接检查超时
	 * @param pjp
	 * @param children
	 * @param key
	 * @param newNode
	 */
	private void checkTimeOut(ProceedingJoinPoint pjp, String key) {
		Method m = getMethod(pjp, SingleTaskLock.class);
		SingleTaskLock singleTaskLock = m.getAnnotation(SingleTaskLock.class);
		long timeout = 3600000;// 默认为3600秒
		if (singleTaskLock.timeout() != 0) {
			timeout = singleTaskLock.timeout();
		}
		String strTime = "";
		try {
			strTime = zkClient.readData(key);
		} catch (Exception e) {
			info("task lock node exists after,but clear now.", false);
			return;
		}
		Long time = Long.parseLong(strTime);
		if (new Date().getTime() - time >= timeout)
			alert(pjp);

	}

	private void commonLog(ProceedingJoinPoint pjp, String title) {
		MethodSignature ms = (MethodSignature) pjp.getSignature();
		info(title + " daemon task check method:" + ms.getMethod().getName() + " class:"
				+ pjp.getSignature().getDeclaringTypeName() + " ip:" + IpUtil.getIp(), true);
	}

	/**
	 * 执行任务内容，并清理执行标志结点
	 * 
	 * @param pjp
	 * @param key
	 * @param newNode
	 * @return
	 * @throws Throwable
	 */
	private Object runTask(ProceedingJoinPoint pjp, String key) throws Throwable {
		Object rtn = null;
		try {
			rtn = pjp.proceed(pjp.getArgs());
		} catch (Throwable e) {
			throw e;
		}
		// 无论任务执行是否异常，都需要删除执行标志结点
		finally {

			clearTaskLock(key);
		}
		return rtn;
	}

	/**
	 * 先检查本地方法是否正在执行 如果正在执行，直接返回true 如果本地没有执行，再通过zk进行查询，再返回结果
	 * @param key
	 * @return
	 */
	private boolean checkNodeExits(String key) {
		boolean isKeyExists = false;
		isKeyExists = TaskControl.exists(key);
		if (!isKeyExists) {
			isKeyExists = zkClient.exists(key);
		}
		return isKeyExists;
	}

	/**
	 * 锁定任务执行 先去zookeeper中创建任务执行标志 再本地建立任务执行标志
	 * @param key
	 */
	private void lockTastNode(String key) {
		long now = new Date().getTime();
		zkClient.createEphemeral(key, now);
		TaskControl.put(key, now);

	}

	/**
	 * 清除zk与本地的任务lock标志
	 * @param key
	 */
	private void clearTaskLock(String key) {
		boolean state = zkClient.delete(key);
		if (state)
			TaskControl.remove(key);
	}

	private Method getMethod(ProceedingJoinPoint pjp, Class<? extends Annotation> clazz) {
		MethodSignature ms = (MethodSignature) pjp.getSignature();
		if (ms.getMethod().isAnnotationPresent(clazz))
			return ms.getMethod();
		Method m = ms.getMethod();
		try {
			Method m1 = pjp.getTarget().getClass().getMethod(m.getName(), m.getParameterTypes());
			if (m1.isAnnotationPresent(clazz))
				return m1;
		} catch (Exception e) {
			// do nothing
			e.printStackTrace();
		}
		throw new RuntimeException("No Proper annotation found.");
	}

	@Pointcut("@annotation(com.ias.assembly.zkpro.zk.annotation.SingleTaskLock)")
	public void zkTaskCut() {
	}

	// @Around("execution(* com.jumbo.wms.daemon..*.*(..))")
	// @Around("this(com.ias.assembly.zkpro.zk.annotation.SingleTaskLock)")
	@Around("zkTaskCut()")
	public Object doQuery(ProceedingJoinPoint pjp) throws Throwable {
		String key = makeKey(pjp);
		boolean isKeyExists = checkNodeExits(key);
		// 当前结点存在，表示当前有任务在执行
		if (isKeyExists) {
			checkTimeOut(pjp, key);
			commonLog(pjp, "[exit]other task is running,so exit.");
			throw new RuntimeException(key + " is exists");
		}

		// 当前结点不存在，表示没有任务在执行
		else {
			// 创建标志结点，如果创建失败则直接退出，因为这里可能发生了并发
			try {
				lockTastNode(key);
			} catch (Exception e) {
				commonLog(pjp, "[exit]other task is not running...but create node fail,so exit. path:" + key);
				throw new RuntimeException(key + " is exists", e);
			}
			commonLog(pjp, "[running]other task is not running,will run task.");
			return runTask(pjp, key);
		}
	}
}
