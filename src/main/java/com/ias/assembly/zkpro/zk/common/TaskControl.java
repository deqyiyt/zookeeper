package com.ias.assembly.zkpro.zk.common;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用于本地任务的控制
 */
public class TaskControl {
	private static Map<String,Long> localTask=new ConcurrentHashMap<String,Long>();
	
	public static void put(String key,Long value){
		localTask.put(key, value);
	}
	
	public static Long get(String key){
		return localTask.get(key);
	}
	
	public static void remove(String key){
		localTask.remove(key);
	}
	
	public static Boolean exists(String key){
		Long v=localTask.get(key);
		if(v==null)
			return false;
		else
			return true;
	}
	
	public static List<String> getAllKey(){
		List<String> keyList=new ArrayList<String>();
		Iterator<String> ite= localTask.keySet().iterator();
		while(ite.hasNext()){
			keyList.add(ite.next());
		}
		return keyList;
	}
	
	
	
}
