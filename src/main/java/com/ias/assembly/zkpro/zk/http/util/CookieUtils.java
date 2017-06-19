package com.ias.assembly.zkpro.zk.http.util;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.StringUtils;

/**
 * Cookie 辅助类
 */
public class CookieUtils {

	/**
	 * 获得cookie
	 * @param request HttpServletRequest
	 * @param name cookie name
	 * @return if exist return cookie, else return null.
	 */
	public static Cookie getCookie(HttpServletRequest request, String name) {
		Map<String, Cookie> cookieMap = ReadCookieMap(request);
		if (cookieMap.containsKey(name)) {
			Cookie cookie = (Cookie) cookieMap.get(name);
			return cookie;
		} else {
			return null;
		}
	}
	
	/**
	 * 将cookie封装到Map里面
	 * @param request
	 * @return
	 */
	private static Map<String, Cookie> ReadCookieMap(HttpServletRequest request) {
		Map<String, Cookie> cookieMap = new HashMap<String, Cookie>();
		Cookie[] cookies = request.getCookies();
		if (null != cookies) {
			for (Cookie cookie : cookies) {
				cookieMap.put(cookie.getName(), cookie);
			}
		}
		return cookieMap;
	}

	/**
	 * 根据部署路径，将cookie保存在根目录。
	 * @param request
	 * @param response
	 * @param name
	 * @param value
	 * @param expiry
	 * @param domain
	 * @return
	 */
	public static Cookie addCookie(HttpServletRequest request,
			HttpServletResponse response, String name, String value,
			Integer expiry, String domain) {
		Cookie cookie = new Cookie(name, value);
		if (expiry != null) {
			cookie.setMaxAge(expiry);
		}
		if (!StringUtils.isEmpty(domain)) {
			cookie.setDomain(domain);
		}
		String ctx = request.getContextPath();
		cookie.setPath(StringUtils.isEmpty(ctx) ? "/" : ctx);
		response.addCookie(cookie);
		return cookie;
	}

	/**
	 * 取消cookie
	 * @param request
	 * @param response
	 * @param name
	 * @param domain
	 */
	public static void cancleCookie(HttpServletRequest request,
			HttpServletResponse response, String name, String domain) {
		Cookie cookie = new Cookie(name, "");
		cookie.setMaxAge(0);
		String ctx = request.getContextPath();
		cookie.setPath(StringUtils.isEmpty(ctx) ? "/" : ctx);
		if (!StringUtils.isEmpty(domain)) {
			cookie.setDomain(domain);
		}
		response.addCookie(cookie);
	}
}
