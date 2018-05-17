package com.ias.assembly.zkpro.zk;

import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.ApplicationContextException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.PropertiesLoaderUtils;

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
			try {
				zk = new ZooKeeper(zkhost, 30000, this);
			} catch (IOException e) {
				log.error("Failed to connect to zk server" + zkhost, e);
				throw new ApplicationContextException("Failed to connect to zk server" + zkhost, e);
			}
			try {
				for (String znode : znodes.split(",")) {
					znode = znode.trim();
					if(zk.exists(znode, true) != null) {
						List<String> children = zk.getChildren(znode, true);
						for (String child : children) {
							try {
								byte[] data = zk.getData(znode + "/" + child, null, null);
								String value = "";
								if(data != null) {
									value = new String(data);
								}
								if(value != null && value.startsWith("/") && zk.exists(value, true) != null) {
									log.info("Zookeeper pathKey:{}\t value:{}", child, value);
									zkprops.setProperty(child, value);
									setProperty(zkprops, value);
								} else {
									log.info("Zookeeper key:{}\t value:{}", child, value);
									zkprops.setProperty(child, value);
								}
							} catch (Exception e) {
								log.error("Read property(key:{}) error", child);
								log.error("Exception:", e);
							}
						}
					}
				}
			} catch (KeeperException e) {
				log.error("Failed to get property from zk server" + zkhost, e);
//				throw new ApplicationContextException("Failed to get property from zk server" + zkhost, e);
			} catch (InterruptedException e) {
				log.error("Failed to get property from zk server" + zkhost, e);
//				throw new ApplicationContextException("Failed to get property from zk server" + zkhost, e);
			} finally {
				try {
					zk.close();
				} catch (InterruptedException e) {
					log.error("Error found when close zookeeper connection.", e);
				}
			}
	
			Properties overProps = queryOverrideLocation();
			// 将扩展的properties信息覆盖zookeeper获取的属性
			copyProperties(zkprops, overProps);
			saveProperties(zkprops);
		}
		super.processProperties(beanFactoryToProcess, zkprops);
	}
	
	private void setProperty(Properties zkprops, String key) throws KeeperException, InterruptedException {
		if(key.startsWith("/") && zk.exists(key, true) != null) {
			List<String> children = zk.getChildren(key, true);
			for (String child : children) {
				try {
					byte[] data = zk.getData(key + "/" + child, null, null);
					String value = "";
					if(data != null) {
						value = new String(data);
					}
					if(value != null && value.startsWith("/") && zk.exists(value, true) != null) {
						setProperty(zkprops, value);
					} else {
						log.info("Zookeeper key:{}\t value:{}", child, value);
						zkprops.setProperty(child, value);
					}
				} catch (Exception e) {
					log.error("Read property(key:{}) error", child);
					log.error("Exception:", e);
				}
			}
		}
	}

	public void process(WatchedEvent event) {
	}
}
