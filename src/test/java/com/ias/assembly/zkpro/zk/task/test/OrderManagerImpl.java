package com.ias.assembly.zkpro.zk.task.test;

import org.springframework.stereotype.Service;

import com.ias.assembly.zkpro.zk.annotation.SingleTaskLock;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OrderManagerImpl implements OrderManager {

	
	@Override
	public void create(String orderNumber, String time) {
		log.debug("code:"+orderNumber);
		try {
			Thread.sleep(100000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@SingleTaskLock(timeout=200)
	@Override
	public void create() {
		log.debug("null parameter");
	}

}
