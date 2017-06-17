package com.ias.assembly.zkpro.zk.bean;

/**
 * 当某个path的数据发生改变后调用此方法
 * 查看test中的例子com.ias.assembly.zkpro.zk.change.data.ZkDateChangeManagerImpl
 */
public interface ZkDataChangeManager {
	
	/** 
	 * 需要监听的节点
	 * @author: jiuzhou.hu
	 * @date:2017年6月17日上午12:25:34 
	 * @return
	 */
	String changePath();
	
	/** 
	 * 数据改变时通知
	 * @author: jiuzhou.hu
	 * @date:2017年6月17日上午12:25:41 
	 * @param dataPath
	 * @param data
	 */
	void changeData(String dataPath,Object data);
	
	/** 
	 * 删除节点通知
	 * @author: jiuzhou.hu
	 * @date:2017年6月17日上午12:25:50 
	 * @param dataPath
	 */
	void deleteData(String dataPath);
}
