package com.ias.assembly.zkpro.zk.listener;

import java.util.List;

import org.I0Itec.zkclient.IZkChildListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ias.assembly.zkpro.zk.bean.ZkChildChangeManager;

/**
 * 子节点发生改变（新增或删除）时，会进行通知，值改变时不会通知
 * 查看test中的例子com.ias.assembly.zkpro.zk.change.path.ZkChildChangeManagerImpl
 */
@Component
public class IZkChildListenerImpl implements IZkChildListener {

	@Autowired(required = false)
	private ZkChildChangeManager[] zkChildChangeManager;

	@Override
	public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
		if (zkChildChangeManager != null) {
			for (ZkChildChangeManager changeManager : zkChildChangeManager) {
				changeManager.changeData(parentPath, currentChilds);
			}
		}
	}
}
