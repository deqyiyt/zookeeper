package com.ias.assembly.zkpro.zk.change.path;

import java.util.List;

import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.CreateMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ias.assembly.zkpro.zk.bean.ZkChildChangeManager;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ZkChildChangeManagerImpl implements ZkChildChangeManager {

	private String changePath = "/ias/zk/web";
	
	@Autowired
	private ZkClient zkClient;

	@Override
	public void changeData(String parentPath, List<String> currentChilds) {
		log.debug("节点 {} >>>>>>>>>> {}", parentPath, currentChilds.toString());
	}

	@Override
	public String changePath() {
		if(!zkClient.exists(changePath)) {
            zkClient.create(changePath, null, CreateMode.EPHEMERAL);
        }
		return changePath;
	}

	

}
