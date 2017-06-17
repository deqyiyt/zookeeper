package com.ias.assembly.zkpro.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.ias.assembly.zkpro.config.AssemblyZkproConfig;
import com.ias.assembly.zkpro.zk.TaskService;

import lombok.extern.slf4j.Slf4j;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=AssemblyZkproConfig.class)
@Slf4j
public class TestServiceTest {
	
	@Value("${ias.zk.cofing.host}")
	private String test;
	
	@Autowired
	private TaskService taskService;
	
	@Test
	public void test() {
		log.info(taskService.hello(test));
	}
}
