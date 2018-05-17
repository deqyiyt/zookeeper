package com.ias.assembly.zkpro.zk.bean;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Ztree implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2972776510175721338L;
	
	private int id;
	private Integer pId;
	private String path;
	private String name;
	private String title;
	private String value;
	private boolean open;
	private String selection;
}
