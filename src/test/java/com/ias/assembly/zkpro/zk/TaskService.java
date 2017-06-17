package com.ias.assembly.zkpro.zk;

import org.springframework.stereotype.Service;

import com.ias.assembly.zkpro.zk.annotation.SingleTaskLock;

@Service
public class TaskService {
	
	@SingleTaskLock
	public String hello(String txt) {
		
		return "hello "+txt;
	}
}
