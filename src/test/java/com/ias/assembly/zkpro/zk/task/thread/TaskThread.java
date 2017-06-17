package com.ias.assembly.zkpro.zk.task.thread;

import com.ias.assembly.zkpro.zk.task.test.RunTaskService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TaskThread  extends Thread {

	private RunTaskService runTaskService;
	
	public TaskThread(RunTaskService runTaskService){
		super();
		this.runTaskService=runTaskService;
	}
	
	@Override
	public void run() {
		log.debug("run-task in thread");
		runTaskService.runTask();
		
	}

}
