package com.ias.assembly.zkpro.zk.task;

import java.util.List;

import com.ias.assembly.zkpro.zk.common.TaskControl;
import com.ias.assembly.zkpro.zk.util.IpUtil;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public class ZkExpired extends Thread {
	private TaskWarningManager taskWarningManager;
	private String sessionTimeout;
	private TaskLockCustomStateManager taskLockCustomStateManager;
    private TaskLogManager taskLogManager;
    
    /**
     * 输出日志
     * @param con
     */
    private void info(String con){
    	log.info(con);
    	if(taskLogManager!=null)
    		taskLogManager.log(con,ZkExpired.class);
    }
	
    public ZkExpired(){
    	
    }
    
	public ZkExpired(TaskLogManager taskLogManager,TaskWarningManager taskWarningManager,String sessionTimeout,TaskLockCustomStateManager taskLockCustomStateManager){
		this.taskWarningManager=taskWarningManager;
		this.sessionTimeout=sessionTimeout;
		this.taskLockCustomStateManager=taskLockCustomStateManager;
		this.taskLogManager=taskLogManager;
	}
	
	@Override
	public void run() {
		try{
			Thread.sleep(Long.parseLong(sessionTimeout));
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		//因为KeeperState.Expired只在重连后才会生效，所以这里加上新的逻辑
		//如果断开sessionTimeout的时间后，还没有被连接上的话，会发告警
		if(!taskLockCustomStateManager.hasConnected()){
			String desconnect="[desconnect]service zk session Expired, ip:"+IpUtil.getIp();
			info(desconnect);
			//todo session过期处理
			List<String> keyList=TaskControl.getAllKey();
			StringBuffer sb=new StringBuffer();
			for(String key:keyList){
				sb.append(key);
				sb.append(",");
			}
			if(keyList.size()>0){
				String warningCon="[Expired]service zk session Expired, these key of task is running in this service,but other service can run. please check these task. keys:" +sb.toString()+" ip:"+IpUtil.getIp();
				info(warningCon);
				if(taskWarningManager!=null)
		    		taskWarningManager.warning(warningCon,ZkExpired.class);
			}
		}
	}
}
