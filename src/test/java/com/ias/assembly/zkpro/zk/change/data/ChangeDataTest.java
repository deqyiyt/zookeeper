package com.ias.assembly.zkpro.zk.change.data;

import java.util.UUID;

import org.I0Itec.zkclient.ZkClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.ias.assembly.zkpro.config.AssemblyZkproConfig;

import lombok.extern.slf4j.Slf4j;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=AssemblyZkproConfig.class)
@Slf4j
public class ChangeDataTest {
	
	private String changePath = "/ias/zk/demo/web.test";
	
	@Autowired
	private ZkClient zkClient;
	
	@Test
	public void change() {
		String str = UUID.randomUUID().toString();
		log.info(">>>>>>>>>> 产生随机的数 {}" ,str);
        
        zkClient.writeData(changePath, str);
	}
}
