package com.ias.assembly.zkpro.config;

import java.io.IOException;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.ZkSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertyResourceConfigurer;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.StringUtils;

import com.ias.assembly.zkpro.zk.ZookeeperConfigurer;
import com.ias.assembly.zkpro.zk.http.StatViewServlet;
import com.ias.assembly.zkpro.zk.prop.ZkProp;

@Configuration
@PropertySources({
    @PropertySource(value = "file:/ias/config/ias-assembly-zkpro.properties", ignoreResourceNotFound = true)
})
@ComponentScan(basePackages = {"com.ias.assembly.zkpro.zk"})
public class AssemblyZkproConfig {
	
	@Bean
	@Autowired
	public ZkClient zkClient(ZkProp zkProp, ZkSerializer zkSerializer) {
		if(!StringUtils.isEmpty(zkProp.getHost())) {
			return new ZkClient(zkProp.getHost(), zkProp.getSessionTimeout(), Integer.MAX_VALUE, zkSerializer);
		} else {
			return null;
		}
	}
	
	@Bean
	public PropertyResourceConfigurer zkConfigBean() throws IOException {
		ZookeeperConfigurer configurer = new ZookeeperConfigurer();
		ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		configurer.setLocations(resolver.getResources("classpath*:config/**/*.properties"));
		configurer.setIgnoreResourceNotFound(true);
		/*List<String> overrideLocaltions = new ArrayList<String>();
		overrideLocaltions.add("file:/ias/config/ias-assembly-zkpro.properties");
		configurer.setOverrideLocaltions(overrideLocaltions);*/
		return configurer;
	}
	
	@Bean
    public ServletRegistrationBean druidStatViewServlet() {
		ServletRegistrationBean registration = new ServletRegistrationBean(new StatViewServlet(), "/zk-manager/*");
		registration.addInitParameter("loginUsername", "admin");// 用户名
		registration.addInitParameter("loginPassword", "admin");// 密码
        return registration;
    }
}
