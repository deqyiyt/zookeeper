package com.ias.assembly.zkpro.zk.task.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RunTaskServiceImpl implements RunTaskService {

	@Autowired
	private OrderManager orderManager;
	
	@Override
	public void runTask() {
		//orderManager.create("BZ2334343", null);
		orderManager.create();
	}

}
