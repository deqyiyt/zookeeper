/*
 * Copyright 1999-2017 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ias.assembly.zkpro.zk.http;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ias.assembly.zkpro.zk.http.stat.ZkStatService;

/**
 * 注意：避免直接调用Druid相关对象例如DruidDataSource等，相关调用要到DruidStatManagerFacade里用反射实现
 * 
 * @author sandzhang[sandzhangtoo@gmail.com]
 */
public class StatViewServlet extends ResourceServlet {

    /**
	 * 
	 */
	private static final long serialVersionUID = 5487585165581998301L;
	
	private ZkStatService      statService             = ZkStatService.getInstance();

	public StatViewServlet(){
        super("support/http/resources");
    }

    public void init() throws ServletException {
        super.init();
    }

    /**
     * @param url 要连接的服务地址
     * @return 调用服务后返回的json字符串
     */
    protected String process(String url, HttpServletRequest request, HttpServletResponse response) {
        return statService.service(url, request, response);
    }
}
