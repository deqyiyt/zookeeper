package com.ias.assembly.zkpro.zk.prop;

import static com.ias.assembly.zkpro.zk.common.Constants.Zk.HOST;
import static com.ias.assembly.zkpro.zk.common.Constants.Zk.ROOT;
import static com.ias.assembly.zkpro.zk.common.Constants.Zk.SESSION_TIMEOUT;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Component
public class ZkProp {
	@Value("${ias.zk.cofing.host}")
	private String host;
	@Value("${ias.zk.cofing.root}")
	private String root;
	@Value("${ias.zk.cofing.sessionTimeout}")
	private int sessionTimeout;
	
	@PostConstruct
	public void postConstruct(){
		HOST = host;
		ROOT = root;
		SESSION_TIMEOUT = sessionTimeout;
	}
}
