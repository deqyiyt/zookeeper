package com.ias.assembly.zkpro.zk.change.data;

import org.springframework.stereotype.Service;

import com.ias.assembly.zkpro.zk.bean.ZkDataChangeManager;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ZkDateChangeManagerImpl implements ZkDataChangeManager {

	private String changePath = "/ias/zk/demo/web.test";

	@Override
	public void changeData(String dataPath, Object data) {
		log.debug("节点 {} >>>>>>>>>> {}", dataPath, data);
	}

	@Override
	public void deleteData(String dataPath) {

	}

	@Override
	public String changePath() {
		return changePath;
	}

}
