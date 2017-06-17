package com.ias.assembly.zkpro.zk.bean;

import java.util.List;

/**
 * 子节点发生改变（新增或删除）时，会进行通知，值改变时不会通知
 * 查看test中的例子com.ias.assembly.zkpro.zk.change.path.ZkChildChangeManagerImpl
 */
public interface ZkChildChangeManager {
	
	/** 
	 * 需要监听的节点
	 * @author: jiuzhou.hu
	 * @date:2017年6月17日上午12:25:01 
	 * @return
	 */
	String changePath();
	
	/** 
	 * 通知事件
	 * @author: jiuzhou.hu
	 * @date:2017年6月17日上午12:25:09 
	 * @param parentPath
	 * @param currentChilds
	 */
	void changeData(String parentPath, List<String> currentChilds);
}
