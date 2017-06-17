package com.ias.assembly.zkpro.zk.change;

import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ias.assembly.zkpro.zk.bean.ZkStateChangeManager;
import com.ias.assembly.zkpro.zk.task.TaskLockCustomStateManager;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ZkStateChangeManagerImpl implements ZkStateChangeManager {

	@Autowired
	private TaskLockCustomStateManager taskLockCustomStateManager;
	@Override
	public void handleNewSession() throws Exception {
		//断后重连需要再次注册
		//ZkClientInitBean.afterPropertiesSet();
		taskLockCustomStateManager.handleNewSession();
		log.info("zk new Session create");
	}

	@Override
	public void handleStateChanged(KeeperState arg0) throws Exception {
		taskLockCustomStateManager.handleStateChanged(arg0);
		log.info("zk stateChange:"+arg0);
	}

	@Override
	public void handlerDisconnected() throws Exception {
		log.info("zk Disconnected");
	}

	@Override
	public void handlerExpired() throws Exception {
		log.info("zk Expired");
	}

	@Override
	public void handlerSyncConnected() throws Exception {
		log.info("zk SyncConnected");
	}

}
