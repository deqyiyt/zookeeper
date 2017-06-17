package com.ias.assembly.zkpro.zk.task.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ias.assembly.zkpro.zk.aop.SingleTaskLockAop;
import com.ias.assembly.zkpro.zk.task.TaskWarningManager;

@Service
public class WarningManagerImpl implements TaskWarningManager {

    protected static final Logger logger = LoggerFactory.getLogger(SingleTaskLockAop.class);

	@Override
	public void warning(String con, Class<?> Clazz) {
		logger.error(con);
	}

}
