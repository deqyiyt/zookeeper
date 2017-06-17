package com.ias.assembly.zkpro.zk.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class IpUtil {
	
	public static String getIp(){
    	String ip="unknow";
    	InetAddress addr;
		try {
			addr = InetAddress.getLocalHost();
			ip=addr.getHostAddress().toString();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		
		return ip;
    }

}
