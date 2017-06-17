package com.ias.assembly.zkpro.zk.task;

import org.apache.zookeeper.Watcher.Event.KeeperState;

/**
 * 当zk的连接状态发生改变时，调用此类的对应方法
 * 主要 为了解决项目自己需要订阅 IZkStateListener，并且对相关的事件做处理 
 * @author hujiuzhou
 *
 */
public interface TaskLockCustomStateManager {
	

    /**
     * 当zookeeper的连接状态发生改变时，需要调用此方法
     * @param state The new state.
     * @throws Exception On any error.
     */
    public void handleStateChanged(KeeperState state) throws Exception;

    /**
     * 当session过期，且新创建session后，调用此方法
     * 需要注意的时：在新建session后，还得重新创建一些临时节点
     * @throws Exception On any error.
     */
    public void handleNewSession() throws Exception;
    
    
    /**
     * 当前是否已经连接
     * @return
     */
    public boolean hasConnected();

}
