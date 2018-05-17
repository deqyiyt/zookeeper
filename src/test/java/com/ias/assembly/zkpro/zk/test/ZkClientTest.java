package com.ias.assembly.zkpro.zk.test;

import java.util.UUID;

import org.apache.curator.framework.recipes.locks.InterProcessLock;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.junit.Test;

import com.ias.assembly.zkpro.zk.common.ZkClient;
import com.ias.assembly.zkpro.zk.common.ZkClientUtils;
import com.ias.assembly.zkpro.zk.http.json.JsonUtil;

import lombok.SneakyThrows;  
  
/** 
 * @date 2017年4月19日 
 * @author zhoushanbin 
 * 
 */  
public class ZkClientTest {  
      
    public String host = "127.0.0.1:2188";
    
    @Test
    public void testTree(){  
    		System.out.println(JsonUtil.buildNormalBinder().toJson(ZkClientUtils.getInstance(host, "/ias/zk/demo").tree(null)));
    }  
    
    @Test
    public void testCreateNode() {
        ZkClient zkClient = ZkClientUtils.getInstance(host, 30, null, null);  
        zkClient.createPersitentNode("/zk/demo/web.test", UUID.randomUUID().toString(), true);
    }  
    
    @Test
    @SneakyThrows
    public void testSetNodeData(){  
        ZkClient zkClient = ZkClientUtils.getInstance(host);  
        zkClient.setNodeData("/ias/zk/demo/web.test1", UUID.randomUUID().toString());
        zkClient.quit();
        Thread.sleep(5000);
        zkClient.setNodeData("/ias/zk/demo/web.test1", UUID.randomUUID().toString());
    }
    
    @Test
    public void testDeleteNode(){  
          
    }  
    
    @Test
    public void testSetData(){  
          
    }  
    
    @Test
    public void testGetChildren(){  
          
    }
    
    @Test
    public void testGetData(){  
        
    }  
    
    @Test
    public void testExists(){  
		ZkClient zkClient = ZkClientUtils.getInstance(host, 30, "ias", null);
		zkClient.exists("/zk/demo/web.test1", new Watcher() {
			@Override
			public void process(WatchedEvent event) {
				System.out.println(event);
			}
		}, true);
    }  
    
    @Test
    public void testDistributeLock(){  
          
        for(int i=0;i<50;i++){  
            new Thread(){  
  
                @Override  
                public void run() {  
                    InterProcessLock lock = null;  
                    try{  
                        ZkClient zkClient = ZkClientUtils.getInstance(host, 30, "dislock", null);  
                        lock = zkClient.getInterProcessLock("/distributeLock");  
                        System.out.println(Thread.currentThread().getName()+"申请锁");  
                        lock.acquire();  
                        System.out.println(Thread.currentThread().getName()+"持有锁");  
                        Thread.sleep(500);  
                    }  
                    catch(Exception e){  
                        e.printStackTrace();  
                    }  
                    finally{  
                        if(null != lock){  
                            try {  
                                lock.release();  
                                System.out.println(Thread.currentThread().getName()+"释放有锁");  
                            } catch (Exception e) {  
                                e.printStackTrace();  
                            }  
                        }  
                    }  
                }  
                  
            }.start();  
              
        }  
    }  
} 
