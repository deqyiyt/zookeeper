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

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.util.StringUtils;

import com.ias.assembly.zkpro.zk.http.util.Utils;

@SuppressWarnings("serial")
public abstract class ResourceServlet extends HttpServlet {

    public static final String SESSION_USER_KEY    = "zk-manager-user";
    public static final String PARAM_NAME_USERNAME = "loginUsername";
    public static final String PARAM_NAME_PASSWORD = "loginPassword";

    protected String           username            = null;
    protected String           password            = null;

    protected final String     resourcePath;

    public ResourceServlet(String resourcePath){
        this.resourcePath = resourcePath;
    }

    public void init() throws ServletException {
    	 String paramUserName = getInitParameter(PARAM_NAME_USERNAME);
         if (!StringUtils.isEmpty(paramUserName)) {
             this.username = paramUserName;
         }

         String paramPassword = getInitParameter(PARAM_NAME_PASSWORD);
         if (!StringUtils.isEmpty(paramPassword)) {
             this.password = paramPassword;
         }
    }

    protected String getFilePath(String fileName) {
        return resourcePath + fileName;
    }

    protected void returnResourceFile(String fileName, String uri, HttpServletResponse response)
                                                                                                throws ServletException,
                                                                                                IOException {

        String filePath = getFilePath(fileName);
        
        if (filePath.endsWith(".html")) {
            response.setContentType("text/html; charset=utf-8");
        }
        if (fileName.endsWith(".jpg") || fileName.endsWith(".png") || fileName.endsWith(".gif")) {
            byte[] bytes = Utils.readByteArrayFromResource(filePath);
            if (bytes != null) {
                response.getOutputStream().write(bytes);
            }

            return;
        }

        String text = Utils.readFromResource(filePath);
        if (text == null) {
            response.sendRedirect(uri + "/index.html");
            return;
        }
        if (fileName.endsWith(".css")) {
            response.setContentType("text/css;charset=utf-8");
        } else if (fileName.endsWith(".js")) {
            response.setContentType("text/javascript;charset=utf-8");
        }
        response.getWriter().write(text);
    }

    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String contextPath = request.getContextPath();
        String servletPath = request.getServletPath();
        String requestURI = request.getRequestURI();

        response.setCharacterEncoding("utf-8");

        if (contextPath == null) { // root context
            contextPath = "";
        }
        String uri = contextPath + servletPath;
        String path = requestURI.substring(contextPath.length() + servletPath.length());

        if ("/submitLogin".equals(path)) {
            String usernameParam = request.getParameter(PARAM_NAME_USERNAME);
            String passwordParam = request.getParameter(PARAM_NAME_PASSWORD);
            if (username.equals(usernameParam) && password.equals(passwordParam)) {
                request.getSession().setAttribute(SESSION_USER_KEY, username);
                response.getWriter().print("success");
            } else {
                response.getWriter().print("error");
            }
            return;
        }

        if (isRequireAuth() //
            && !ContainsUser(request)//
            && !checkLoginParam(request)//
            && !("/login.html".equals(path) //
                 || path.startsWith("/css")//
                 || path.startsWith("/scripts") //
            || path.startsWith("/img"))) {
            if (contextPath.equals("") || contextPath.equals("/")) {
                response.sendRedirect("/zk-manager/login.html");
            } else {
                if ("".equals(path)) {
                    response.sendRedirect("zk-manager/login.html");
                } else {
                    response.sendRedirect("login.html");
                }
            }
            return;
        }

        if ("".equals(path)) {
            if (contextPath.equals("") || contextPath.equals("/")) {
                response.sendRedirect("/zk-manager/index.html");
            } else {
                response.sendRedirect("zk-manager/index.html");
            }
            return;
        }

        if ("/".equals(path)) {
            response.sendRedirect("index.html");
            return;
        }

        if (path.contains(".json")) {
            String fullUrl = path;
            if (request.getQueryString() != null && request.getQueryString().length() > 0) {
                fullUrl += "?" + request.getQueryString();
            }
            response.getWriter().print(process(fullUrl, request, response));
            return;
        }

        // find file in resources path
        returnResourceFile(path, uri, response);
    }

    public boolean ContainsUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session != null && session.getAttribute(SESSION_USER_KEY) != null;
    }

    public boolean checkLoginParam(HttpServletRequest request) {
        String usernameParam = request.getParameter(PARAM_NAME_USERNAME);
        String passwordParam = request.getParameter(PARAM_NAME_PASSWORD);
        if(null == username || null == password){
            return false;
        } else if (username.equals(usernameParam) && password.equals(passwordParam)) {
            return true;
        }
        return false;
    }

    public boolean isRequireAuth() {
        return this.username != null;
    }

    protected abstract String process(String url, HttpServletRequest request, HttpServletResponse response);
}
