package com.ias.assembly.zkpro.zk.change.data;

import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.CreateMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ias.assembly.zkpro.zk.bean.ZkDataChangeManager;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ZkDateChangeManagerImpl implements ZkDataChangeManager {

	private String changePath = "/ias/zk/demo/web.test";
	
	@Autowired
	private ZkClient zkClient;

	@Override
	public void changeData(String dataPath, Object data) {
		log.debug("节点 {} >>>>>>>>>> {}", dataPath, data);
	}

	@Override
	public void deleteData(String dataPath) {

	}

	@Override
	public String changePath() {
		if(!zkClient.exists(changePath)) {
            zkClient.create(changePath, null, CreateMode.EPHEMERAL);
        }
		return changePath;
	}

}
