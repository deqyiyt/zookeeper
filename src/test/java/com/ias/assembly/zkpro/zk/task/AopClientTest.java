package com.ias.assembly.zkpro.zk.task;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.ias.assembly.zkpro.zk.task.test.RunTaskService;
import com.ias.assembly.zkpro.zk.task.thread.TaskThread;

import lombok.extern.slf4j.Slf4j;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class AopClientTest {

	@Autowired(required = false)
	private RunTaskService runTaskService;

	public void testOrder() {

		runTaskService.runTask();

	}

	@Test
	public void testInsert() throws InterruptedException {
		int count = 10;
		/*String strDate = "2015-04-07 14:17:00";

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		while (true) {
			Thread.sleep(1000);
			if (sdf.format(new Date()).equalsIgnoreCase(strDate))
				break;
		}*/

		// runTaskService.runTask();
		List<Thread> tList = new ArrayList<Thread>();

		for (int i = 0; i < count; i++) {
			TaskThread tt = new TaskThread(runTaskService);

			tList.add(tt);
		}

		log.info("thread-start:" + tList.size());

		for (Thread t : tList) {
			// log.info("t-start");
			t.start();
		}

		while (true) {
			Thread.sleep(1000);
		}
	}

}
