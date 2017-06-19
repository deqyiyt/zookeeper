package com.ias.assembly.zkpro.zk.http.bean;

import java.io.Serializable;

import lombok.Data;

@Data
public class Ztree implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2972776510175721338L;
	
	public int id;
	public Integer pId;
	public String name;
	public boolean open;
	public String title;
	public String selection;
}
