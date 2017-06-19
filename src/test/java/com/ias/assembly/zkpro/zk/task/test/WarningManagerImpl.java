package com.ias.assembly.zkpro.zk.task.test;

import org.springframework.stereotype.Service;

import com.ias.assembly.zkpro.zk.task.TaskWarningManager;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class WarningManagerImpl implements TaskWarningManager {

	@Override
	public void warning(String con, Class<?> Clazz) {
		log.error(con);
	}

}
