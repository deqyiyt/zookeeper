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
package com.ias.assembly.zkpro.zk.http.stat;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.ZkSerializer;
import org.springframework.util.StringUtils;

import com.ias.assembly.zkpro.zk.bean.ZkSerializerImpl;
import com.ias.assembly.zkpro.zk.http.bean.Ztree;
import com.ias.assembly.zkpro.zk.http.json.JsonUtil;
import com.ias.assembly.zkpro.zk.http.util.CookieUtils;

public final class ZkStatService implements ZkStatServiceMBean {
	
    private final static ZkStatService instance = new ZkStatService();

    public final static int RESULT_CODE_SUCCESS = 1;
    public final static int RESULT_CODE_ERROR  = -1;
    
    public static final String ZK_HOST = "zk-manager-host";
    public static final String ZK_PATH = "zk-manager-path";
    
    public static final String HOST_NAME = "host";
    public static final String PATH_NAME = "path";
    private Map<String, ZkClient> clientMap = new HashMap<String, ZkClient>();
    
    private ZkSerializer serializer = new ZkSerializerImpl();

    private ZkStatService(){
    }

    public static ZkStatService getInstance() {
        return instance;
    }

    public String service(String url, HttpServletRequest request, HttpServletResponse response) {
        if (url.equals("/tree.json")) {
        	return tree(request, response);
        }
        if (url.equals("/child.json")) {
        	return child(request, response);
        }
        if (url.equals("/add.json")) {
        	return add(request, response);
        }
        if (url.equals("/update.json")) {
        	return update(request, response);
        }
        if (url.equals("/remove.json")) {
        	return remove(request, response);
        }
        if (url.equals("/import.json")) {
        	return imports(request, response);
        }
        if (url.startsWith("/export.json")) {
        	return export(request, response);
        }

        return returnJSONResult(RESULT_CODE_ERROR, "Do not support this request, please contact with administrator.");
    }
    private String remove(HttpServletRequest request, HttpServletResponse response) {
    	ZkClient client = getClient(request);
		if(client == null) {
    		return returnJSONResult(RESULT_CODE_SUCCESS, null);
    	}
		String path = request.getParameter(PATH_NAME);
		path = path.replaceAll("//+", "/");
		CookieUtils.addCookie(request, response, ZK_PATH, path, -1, null);
		if(client.exists(path)) {
			client.deleteRecursive(path);
			return returnJSONResult(RESULT_CODE_SUCCESS, "删除成功");
		} else {
			return returnJSONResult(RESULT_CODE_SUCCESS, null);
		}
    }
    private String add(HttpServletRequest request, HttpServletResponse response) {
    	ZkClient client = getClient(request);
		if(client == null) {
    		return returnJSONResult(RESULT_CODE_SUCCESS, null);
    	}
		String path = request.getParameter(PATH_NAME);
		path = path.replaceAll("//+", "/");
		String data = request.getParameter("data");
		CookieUtils.addCookie(request, response, ZK_PATH, path, -1, null);
		if(client.exists(path)) {
			client.writeData(path, data);
			return returnJSONResult(RESULT_CODE_SUCCESS, "修改成功");
		} else {
			client.createEphemeral(path, data);
			return returnJSONResult(RESULT_CODE_SUCCESS, "创建成功");
		}
    }
    private String update(HttpServletRequest request, HttpServletResponse response) {
    	ZkClient client = getClient(request);
		if(client == null) {
    		return returnJSONResult(RESULT_CODE_SUCCESS, null);
    	}
		String path = request.getParameter(PATH_NAME);
		path = path.replaceAll("//+", "/");
		String data = request.getParameter("data");
		String oldData = client.readData(path);
		if (oldData == null || oldData instanceof String) {
			client.writeData(path, data);
			CookieUtils.addCookie(request, response, ZK_PATH, path, -1, null);
			return returnJSONResult(RESULT_CODE_SUCCESS, null);
		} else {
			return returnJSONResult(RESULT_CODE_ERROR, null);
		}
    }
    private String imports(HttpServletRequest request, HttpServletResponse response) {
    	try {
    		ZkClient client = getClient(request);
    		if(client == null) {
        		return returnJSONResult(RESULT_CODE_SUCCESS, null);
        	}
			InputStream input = request.getInputStream();
			Properties properties = new Properties();
			properties.load(input);
			Iterator<Entry<Object, Object>> it = properties.entrySet().iterator();
			while(it.hasNext()){
			    Entry<Object, Object> entry=(Entry<Object, Object>)it.next();
			    String key = (String)entry.getKey();
			    String value = (String)entry.getValue();
			    if(key.startsWith("/")) {
			    	if(client.exists(key)) {
			    		client.writeData(key, value);
			    	} else {
			    		client.createEphemeral(key, value);
			    	}
			    }
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return returnJSONResult(RESULT_CODE_SUCCESS, "上传成功");
    }
    private String export(HttpServletRequest request, HttpServletResponse response) {
    	ZkClient client = getClient(request);
		if(client == null) {
    		return returnJSONResult(RESULT_CODE_SUCCESS, null);
    	}
		StringBuffer data = new StringBuffer();
		String path = request.getParameter(PATH_NAME);
		loadTrees(client, data, path);
		PrintWriter out = null;
		try {
			String fileName = new String(path.replaceAll("/", "-").getBytes("utf-8"), "ISO8859-1");
			response.setHeader("content-disposition","attachment;filename="+fileName+".properties");
			response.setContentType("text/html;charset=utf-8");
			response.setContentType("application/x-download");
			out = response.getWriter();
			out.print(data.toString());
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(out != null) {
				out.close();
			}
		}
		
    	return null;
    }
    
    private void loadTrees(ZkClient client, StringBuffer sf, String path) {
    	if(StringUtils.isEmpty(path)){return;}
		path = path.replaceAll("//+", "/");
		if(path.startsWith("/dubbo") || path.startsWith("/zookeeper")){
			return ;
		}
		String value = (String)client.readData(path);
		sf.append(path).append("=").append(StringUtils.isEmpty(value)?"":value.trim()).append("\r\n");
		List<String> paths = client.getChildren(path);
		if(paths != null && !paths.isEmpty()) {
	    	for(String p:paths) {
	    		loadTrees(client, sf, path + "/"+p);
	    	}
		}
	}
    
    private String child(HttpServletRequest request, HttpServletResponse response) {
		String path = request.getParameter(PATH_NAME);
		ZkClient client = getClient(request);
		if(client == null) {
    		return returnJSONResult(RESULT_CODE_SUCCESS, null);
    	}
    	path = path.replaceAll("//+", "/");
    	CookieUtils.addCookie(request, response, ZK_PATH, path, -1, null);
    	return returnJSONResult(RESULT_CODE_SUCCESS, client.readData(path));
    }
    
    private String tree(HttpServletRequest request, HttpServletResponse response) {
    	String host = request.getParameter(HOST_NAME);
    	if(StringUtils.isEmpty(host)) {
    		Cookie cookie = CookieUtils.getCookie(request, ZK_HOST);
    		if(cookie != null) {
    			host = cookie.getValue();
    		}
    	}
    	String path = request.getParameter(PATH_NAME);
    	if(StringUtils.isEmpty(path)) {
    		Cookie cookie = CookieUtils.getCookie(request, ZK_PATH);
    		if(cookie != null) {
    			path = cookie.getValue();
    		}
    	}
    	if(StringUtils.isEmpty(host)) {
    		return returnJSONResult(RESULT_CODE_SUCCESS, null);
    	}
    	if(StringUtils.isEmpty(path)) {
    		path = "/";
    	}
    	request.getSession().setAttribute(ZK_HOST, host);
    	CookieUtils.addCookie(request, response, ZK_HOST, host, -1, null);
    	CookieUtils.addCookie(request, response, ZK_PATH, path, -1, null);
    	
    	ZkClient client = clientMap.get(host);
    	if(client == null) {
    		client = new ZkClient(host, 2500, Integer.MAX_VALUE, serializer);
    	}
    	clientMap.put(host, client);
    	
    	List<String> paths = client.getChildren(path);
    	
    	List<Ztree> ztrees = new ArrayList<Ztree>();
    	Ztree rootTree = new Ztree();
		rootTree.setId(0);
		rootTree.setOpen(true);
		rootTree.setName(path);
		rootTree.setTitle(path);
		rootTree.setPId(null);
		ztrees.add(rootTree);
		
		loadTrees(client, ztrees, paths, rootTree);
    	
        return returnJSONResult(RESULT_CODE_SUCCESS, ztrees);
    }
    private void loadTrees(ZkClient client, List<Ztree> ztrees, List<String> paths, Ztree ptree) {
    	if(paths == null){return;}
    	for(String path:paths) {
    		path = ("/"+ptree.getName() + "/"+path).replaceAll("//+", "/");
    		Ztree tree = new Ztree();
			tree.setId(ztrees.size() + 1);
			tree.setPId(ptree.getId());
			tree.setName(path);
			tree.setTitle(path);
			ztrees.add(tree);
			
			loadTrees(client, ztrees, client.getChildren(path), tree);
    	}
	}
    
    private ZkClient getClient(HttpServletRequest request) {
    	String host = (String)request.getSession().getAttribute(ZK_HOST);
    	if(StringUtils.isEmpty(host)) {
    		return null;
    	} else {
			ZkClient client = clientMap.get(host);
	    	if(client == null) {
	    		client = new ZkClient(host, 2500, Integer.MAX_VALUE, serializer);
	    	}
	    	clientMap.put(host, client);
	    	return client;
    	}
    }

    private String returnJSONResult(int resultCode, Object content) {
        Map<String, Object> dataMap = new LinkedHashMap<String, Object>();
        dataMap.put("ResultCode", resultCode);
        dataMap.put("Content", content);
        return JsonUtil.buildNormalBinder().toJson(dataMap);
    }
}
