package com.ias.assembly.zkpro.zk.task;

import java.util.List;

import org.I0Itec.zkclient.ZkClient;
import org.junit.BeforeClass;
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
public class ZkClientTest {

	@Autowired(required = false)
	private ZkClient zkClient;

	@BeforeClass
	public static void init() {
		// ProfileConfigUtil.setMode("dev");
	}

	@Test
	public void testInsert() {

		// zkClient.createEphemeralSequential("/test2/","2");
		// zkClient.createEphemeralSequential("/test2/","3");

		zkClient.createEphemeral("/temp");

		try {
			Thread.sleep(50000l);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// zkClient.close();

	}

	@Test
	public void testGet() {

		List<String> list = zkClient.getChildren("/temp");

		log.debug(list.size()+"");
	}

}
