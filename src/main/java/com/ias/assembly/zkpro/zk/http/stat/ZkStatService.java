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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.StringUtils;

import com.ias.assembly.zkpro.zk.common.ZkClient;
import com.ias.assembly.zkpro.zk.common.ZkClientUtils;
import com.ias.assembly.zkpro.zk.http.json.JsonUtil;

public final class ZkStatService implements ZkStatServiceMBean {

	private final static ZkStatService instance = new ZkStatService();

	public final static int RESULT_CODE_SUCCESS = 1;
	public final static int RESULT_CODE_ERROR = -1;

	public static final String ZK_HOST = "zk-manager-host";

	public static final String HOST_NAME = "host";
	public static final String PATH_NAME = "path";

	private ZkStatService() {
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
		if (client == null) {
			return returnJSONResult(RESULT_CODE_SUCCESS, null);
		}
		String path = request.getParameter(PATH_NAME);
		client.deleteNode(path, true);
		return returnJSONResult(RESULT_CODE_SUCCESS, "删除成功");
	}

	private String add(HttpServletRequest request, HttpServletResponse response) {
		ZkClient client = getClient(request);
		if (client == null) {
			return returnJSONResult(RESULT_CODE_SUCCESS, null);
		}
		String path = request.getParameter(PATH_NAME);
		String data = request.getParameter("data");
		if(!client.exists(path, false)) {
			client.createPersitentNode(path, data, true);
			return returnJSONResult(RESULT_CODE_SUCCESS, "创建成功");
		} else {
			client.setNodeData(path, data);
			return returnJSONResult(RESULT_CODE_SUCCESS, "修改成功");
		}
	}

	private String update(HttpServletRequest request, HttpServletResponse response) {
		ZkClient client = getClient(request);
		if (client == null) {
			return returnJSONResult(RESULT_CODE_SUCCESS, null);
		}
		String path = request.getParameter(PATH_NAME);
		path = path.replaceAll("//+", "/");
		String data = request.getParameter("data");
		client.setNodeData(path, data);
		return returnJSONResult(RESULT_CODE_SUCCESS, "修改成功");
	}

	private String imports(HttpServletRequest request, HttpServletResponse response) {
		try {
			ZkClient client = getClient(request);
			if (client == null) {
				return returnJSONResult(RESULT_CODE_SUCCESS, null);
			}
			InputStream input = request.getInputStream();
			Properties properties = new Properties();
			properties.load(input);
			Iterator<Entry<Object, Object>> it = properties.entrySet().iterator();
			while (it.hasNext()) {
				Entry<Object, Object> entry = (Entry<Object, Object>) it.next();
				String key = (String) entry.getKey();
				String value = (String) entry.getValue();
				if (key.startsWith("/")) {
					if (client.exists(key, false)) {
						client.setNodeData(key, value);
					} else {
						client.createPersitentNode(key, value, true);
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
		if (client == null) {
			return returnJSONResult(RESULT_CODE_SUCCESS, null);
		}
		StringBuffer data = new StringBuffer();
		String path = request.getParameter(PATH_NAME);
		loadTrees(client, data, path);
		PrintWriter out = null;
		try {
			String fileName = new String(path.replaceAll("/", "-").getBytes("utf-8"), "ISO8859-1");
			response.setHeader("content-disposition", "attachment;filename=" + fileName + ".properties");
			response.setContentType("text/html;charset=utf-8");
			response.setContentType("application/x-download");
			out = response.getWriter();
			out.print(data.toString());
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				out.close();
			}
		}

		return null;
	}

	private void loadTrees(ZkClient client, StringBuffer sf, String path) {
		if (StringUtils.isEmpty(path)) {
			return;
		}
		path = path.replaceAll("//+", "/");
		if (path.startsWith("/dubbo") || path.startsWith("/zookeeper")) {
			return;
		}
		String value = client.getNodeData(path);
		sf.append(path).append("=").append(StringUtils.isEmpty(value) ? "" : value.trim()).append("\r\n");
		List<String> paths = client.getChildren(path);
		if (paths != null && !paths.isEmpty()) {
			for (String p : paths) {
				loadTrees(client, sf, path + "/" + p);
			}
		}
	}

	private String child(HttpServletRequest request, HttpServletResponse response) {
		String path = request.getParameter(PATH_NAME);
		ZkClient client = getClient(request);
		if (client == null) {
			return returnJSONResult(RESULT_CODE_SUCCESS, null);
		}
		path = path.replaceAll("//+", "/");
		return returnJSONResult(RESULT_CODE_SUCCESS, client.getNodeData(path));
	}

	private String tree(HttpServletRequest request, HttpServletResponse response) {
		String host = request.getParameter(HOST_NAME);
		String path = request.getParameter(PATH_NAME);
		if (StringUtils.isEmpty(host)) {
			return returnJSONResult(RESULT_CODE_SUCCESS, null);
		}
		request.getSession().setAttribute(ZK_HOST, host);
		return returnJSONResult(RESULT_CODE_SUCCESS, ZkClientUtils.getInstance(host).tree(path));
	}

	private ZkClient getClient(HttpServletRequest request) {
		String host = (String) request.getSession().getAttribute(ZK_HOST);
		return ZkClientUtils.getInstance(host);
	}

	private String returnJSONResult(int resultCode, Object content) {
		Map<String, Object> dataMap = new LinkedHashMap<String, Object>();
		dataMap.put("ResultCode", resultCode);
		dataMap.put("Content", content);
		return JsonUtil.buildNormalBinder().toJson(dataMap);
	}
}
