package com.ias.assembly.zkpro.zk.common;

import java.util.HashMap;
import java.util.Map;

import org.apache.zookeeper.data.ACL;
import org.springframework.util.StringUtils;  
  
/** 
 * @date 2017年4月19日 
 * @author zhoushanbin 
 * 
 */  
public class ZkClientUtils {  
  
    private static Map<String, ZkClient> cacheMap = new HashMap<>();
    
    public static ZkClient getInstance(String host) {
		return getInstance(host, 30, "", null);
    }
    
    public static ZkClient getInstance(String host, String namespace) {
		return getInstance(host, 30, namespace, null);
    }
    
    public static ZkClient getInstance(String host, int timeOut, String namespace) {
    		return getInstance(host, timeOut, namespace, null);
	}
    
    /**
     * @author jiuzhou.hu
     * @date 2018年5月17日 下午1:49:54
     * @param host
     * @param namespace	空间/一级节点名称
     * @return
     */
    public static ZkClient getInstance(String host, int timeOut, String namespace, ACL acl) {
    		if(!StringUtils.isEmpty(namespace) && namespace.startsWith("/")) {
    			namespace = namespace.substring(1, namespace.length());
    		}
		if(!cacheMap.containsKey(host)) {
			try {
				cacheMap.put(host, new ZkClient(host, timeOut, namespace, acl));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		ZkClient client = cacheMap.get(host);
		if(!client.isConnected()) {
			try {
				cacheMap.put(host, new ZkClient(host, timeOut, namespace, acl));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return client;
	}
} 
