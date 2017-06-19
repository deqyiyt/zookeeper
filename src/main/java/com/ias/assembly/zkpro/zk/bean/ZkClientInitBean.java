package com.ias.assembly.zkpro.zk.bean;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.IZkStateListener;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 实始化订阅一部分zkclient的事件
 *    dataChange
 *    dataChild
 *    
 * @author hujiuzhou
 */
@Component
public class ZkClientInitBean implements InitializingBean{
	
	@Autowired(required=false)
	private IZkDataListener iZkDataListener;
	
	@Autowired(required=false)
	private IZkChildListener iZkChildListener;
	
	@Autowired(required=false)
	private IZkStateListener[] iZkStateListener;
	
	@Autowired(required=false)
	private ZkDataChangeManager[] zkDataChangeManager;
	
	@Autowired(required=false)
	private ZkChildChangeManager[] zkChildChangeManager;
	
	@Autowired
	private ZkClient zkClient;

	@Override
	public void afterPropertiesSet() throws Exception {
		if(zkClient == null) {return;}
		if(iZkStateListener != null){
			for(IZkStateListener listener : iZkStateListener){
				zkClient.subscribeStateChanges(listener);
			}
		}
			
		if(zkDataChangeManager != null){
			for(ZkDataChangeManager manager : zkDataChangeManager){
				zkClient.subscribeDataChanges(manager.changePath(), iZkDataListener);
			}
		}
		
		if(zkChildChangeManager != null) {
			for(ZkChildChangeManager manager : zkChildChangeManager){
				zkClient.subscribeChildChanges(manager.changePath(), iZkChildListener);
			}
		}
	}
}
