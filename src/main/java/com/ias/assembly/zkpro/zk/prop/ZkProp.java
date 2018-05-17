package com.ias.assembly.zkpro.zk.prop;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@Component
@ConfigurationProperties(prefix = "ias.zk.cofing")
public class ZkProp {
	
	/**
	 * 服务器地址
	 * @type String
	 * @date 2018年5月17日 下午6:45:05
	 */
	private String host;
	
	/**
	 * 资源文件根路径，{@value}只能读取配置的资源文件下面的变量
	 * @type String
	 * @date 2018年5月17日 下午6:45:15
	 */
	private String root = "/ias/zk/demo";
	
	/**
	 * 连接服务器超时时间
	 * @type int
	 * @date 2018年5月17日 下午6:45:24
	 */
	private int sessionTimeout = 30;
	
	/**
	 * 是否将zookeeper中的资源文件覆盖掉本地资源文件
	 * true:覆盖
	 * false:不覆盖
	 * @date 2018年5月17日 下午6:45:24
	 */
	private boolean isOverlay = false;
	
	/**
	 * 管理控制台路径
	 * @type String
	 * @date 2018年5月17日 下午6:45:36
	 */
	private String contextPath = "/zk-manager/*";
	
	/**
	 * 管理控制台登陆用户名
	 * @type String
	 * @date 2018年5月17日 下午6:45:46
	 */
	private String userName = "root";
	
	/**
	 * 管理控制台登陆密码
	 * @type String
	 * @date 2018年5月17日 下午6:45:54
	 */
	private String password = "123456";
	
}
