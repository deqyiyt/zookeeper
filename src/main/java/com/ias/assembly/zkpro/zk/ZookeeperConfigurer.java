package com.ias.assembly.zkpro.zk;

import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.util.StringUtils;

import com.ias.assembly.zkpro.zk.bean.Ztree;
import com.ias.assembly.zkpro.zk.common.ZkClient;
import com.ias.assembly.zkpro.zk.common.ZkClientUtils;

import lombok.extern.slf4j.Slf4j;

//@Component("zkConfigBean")
@Slf4j
public class ZookeeperConfigurer extends PropertyPlaceholderConfigurer implements Watcher {

	ZooKeeper zk = null;
	
	// 重写zookeeper中存储的配置
	private List<String> overrideLocaltions;

	private final static Properties sysProp = new Properties();

	/**
	 * 获取所有系统属性
	 * 
	 * @return
	 */
	public static Properties getProps() {
		return sysProp;
	}

	public List<String> getOverrideLocaltions() {
		return overrideLocaltions;
	}

	public void setOverrideLocaltions(List<String> overrideLocaltions) {
		this.overrideLocaltions = overrideLocaltions;
	}

	/**
	 * 将source中的属性覆盖到dest属性中
	 * 
	 * @param dest
	 * @param source
	 */
	private void copyProperties(Properties dest, Properties source) {
		Enumeration<?> enums = source.propertyNames();
		while (enums.hasMoreElements()) {
			String key = (String) enums.nextElement();
			dest.put(key, source.get(key));
		}
	}

	/**
	 * 保存到本地内存中，可作为非spring bean代码中获取配置的方式
	 * @param props
	 */
	private void saveProperties(Properties props) {
		Enumeration<?> enums = props.propertyNames();
		while (enums.hasMoreElements()) {
			String key = (String) enums.nextElement();
			sysProp.put(key, props.get(key));
		}
	}

	private Properties queryOverrideLocation() {
		Properties props = new Properties();
		if(overrideLocaltions != null && overrideLocaltions.size() > 0) {
			for (String location : overrideLocaltions) {
				PathMatchingResourcePatternResolver pmrpr = new PathMatchingResourcePatternResolver();
				try {
					Resource[] resource = pmrpr.getResources(location);
					Properties prop = PropertiesLoaderUtils.loadProperties(resource[0]);
					copyProperties(props, prop);
				} catch (Exception e) {
				}
			}
		}
		return props;
	}
	
	protected void processProperties(ConfigurableListableBeanFactory beanFactoryToProcess, Properties zkprops)
			throws BeansException {
		String zkhost = zkprops.getProperty("ias.zk.cofing.host");
		String znodes = zkprops.getProperty("ias.zk.cofing.root");
		if(zkhost != null && !"".equals(zkhost)) {
			ZkClient zk = ZkClientUtils.getInstance(zkhost);
			for (String znode : znodes.split(",")) {
				znode = znode.trim();
				if(zk.exists(znode, true)) {
					List<Ztree> tree = zk.tree(znode);
					tree.stream().filter(a -> !StringUtils.isEmpty(a.getValue())).forEach(t -> {
						zkprops.setProperty(t.getTitle(), t.getValue());
					});
				}
			}

			Properties overProps = queryOverrideLocation();
			// 将扩展的properties信息覆盖zookeeper获取的属性
			copyProperties(zkprops, overProps);
			saveProperties(zkprops);
		}
		super.processProperties(beanFactoryToProcess, zkprops);
	}

	public void process(WatchedEvent event) {
	}
}
