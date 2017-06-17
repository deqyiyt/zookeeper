package com.ias.assembly.zkpro.zk.task;

import java.util.Date;
import java.util.List;

import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ias.assembly.zkpro.zk.common.TaskControl;
import com.ias.assembly.zkpro.zk.listener.IZkStateListenerImpl;
import com.ias.assembly.zkpro.zk.prop.ZkProp;
import com.ias.assembly.zkpro.zk.util.IpUtil;

@Service
public class TaskLockCustomStateManagerImpl implements TaskLockCustomStateManager {

	protected static final Logger logger = LoggerFactory.getLogger(TaskLockCustomStateManagerImpl.class);

	@Autowired
	private ZkClient zkClient;

	@Autowired(required = false)
	private TaskWarningManager taskWarningManager;

	private boolean isConnected = true;

	@Autowired(required = false)
	private TaskLogManager taskLogManager;

	@Autowired
	private ZkProp zkProp;

	/**
	 * 输出日志
	 * 
	 * @param con
	 */
	private void info(String con) {

		logger.info(con);

		if (taskLogManager != null)
			taskLogManager.log(con, ZkExpired.class);

	}

	private synchronized void setConnectState(boolean isConn) {
		isConnected = isConn;
	}

	public boolean hasConnected() {
		return isConnected;
	}

	/**
	 * Disconnected 表示断开 SyncConnected 表示重连上 Expired session过期
	 * 
	 * 如果session过期时，任务标志结点已经消失，本地还有任务在执行，那其它service结点有可能会并发执行任务 所以最好是检查一下数据
	 */
	public void handleStateChanged(KeeperState state) throws Exception {
		info("zk client state:" + state.toString() + " ip:" + IpUtil.getIp());

		if (KeeperState.Disconnected.equals(state)) {
			setConnectState(false);
			info("[Disconnected]" + " ip:" + IpUtil.getIp());

			ZkExpired ze = new ZkExpired(taskLogManager, taskWarningManager, zkProp.getSessionTimeout() + "", this);
			ze.start();
		} else if (KeeperState.SyncConnected.equals(state)) {
			setConnectState(true);
		}

		if (KeeperState.Expired.equals(state)) {
			info("[expired]" + " ip:" + IpUtil.getIp());
		}
	}

	/**
	 * session过期重新创建以后
	 * 
	 * 因为此时任务标志结点已经过期，但本地可能还有任务正在执行，所以第一时间连上后，需要再重新创建标志结点。
	 * 如果有结点无法创建，表示，有其它的任务已经开始了，这里就需要立即告警
	 */
	public void handleNewSession() throws Exception {
		List<String> keyList = TaskControl.getAllKey();

		StringBuffer sb = new StringBuffer();

		Date date = new Date();
		for (String key : keyList) {
			try {
				zkClient.createEphemeral(key, "[sessioninit]" + date.getTime());
			} catch (Exception e) {
				sb.append(key);
				sb.append(",");
			}

		}

		String warningCon = "[sessioninit]service zk session init again, find this key has concurrency. keys:"
				+ sb.toString() + " ip:" + IpUtil.getIp();

		info(warningCon);

		if (taskWarningManager != null)
			taskWarningManager.warning(warningCon, IZkStateListenerImpl.class);
	}

}
