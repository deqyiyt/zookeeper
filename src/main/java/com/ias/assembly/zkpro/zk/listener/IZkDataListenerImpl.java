package com.ias.assembly.zkpro.zk.listener;

import org.I0Itec.zkclient.IZkDataListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ias.assembly.zkpro.zk.bean.ZkDataChangeManager;

/**
 * 当某个path的数据发生改变后调用此方法
 * 查看test中的例子com.ias.assembly.zkpro.zk.change.data.ZkDateChangeManagerImpl
 */
@Service
public class IZkDataListenerImpl implements IZkDataListener {

	@Autowired(required = false)
	private ZkDataChangeManager[] zkDataChangeManager;

	@Override
	public void handleDataChange(String dataPath, Object data) throws Exception {
		if (zkDataChangeManager != null) {
			for (ZkDataChangeManager changeManager : zkDataChangeManager) {
				changeManager.changeData(dataPath, data);
			}
		}
	}

	@Override
	public void handleDataDeleted(String dataPath) throws Exception {
		if (zkDataChangeManager != null) {
			for (ZkDataChangeManager changeManager : zkDataChangeManager) {
				changeManager.deleteData(dataPath);
			}
		}
	}
}
