package com.ias.assembly.zkpro.zk.listener;

import org.I0Itec.zkclient.IZkStateListener;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ias.assembly.zkpro.zk.bean.ZkStateChangeManager;
/**
 * zk连接状态发生改变时进行通知
 * 如果你需要它生效:
 * @author hujiuzhou
 *
 */
@Service
public class IZkStateListenerImpl implements IZkStateListener {
	
	
	protected static final Logger logger = LoggerFactory.getLogger(IZkStateListenerImpl.class);
	
	@Autowired
	private ZkStateChangeManager zkStateChangeManager;
	 
	/**
	 * Disconnected 表示断开
	 * SyncConnected 表示重连上
	 * Expired session过期
	 */
	public void handleStateChanged(KeeperState state) throws Exception {
		if(KeeperState.Disconnected.equals(state)){
			zkStateChangeManager.handlerDisconnected();
		}
		else if(KeeperState.SyncConnected.equals(state)){
			zkStateChangeManager.handlerSyncConnected();
		}
		
		else if(KeeperState.Expired.equals(state)){
			zkStateChangeManager.handlerExpired();
		}
		
		zkStateChangeManager.handleStateChanged(state);
	}

	/**
	 * session过期重新创建以后
	 */
	public void handleNewSession() throws Exception {
		zkStateChangeManager.handleNewSession();
	}
}
