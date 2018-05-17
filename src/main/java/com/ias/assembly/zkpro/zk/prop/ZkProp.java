package com.ias.assembly.zkpro.zk.prop;

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
	
}
