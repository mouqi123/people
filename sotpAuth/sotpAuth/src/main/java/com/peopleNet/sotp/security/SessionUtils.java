package com.peopleNet.sotp.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class SessionUtils {

	private static ThreadLocal<HttpSession> sessionContext = new ThreadLocal<HttpSession>();

	public static void setSessionContext(HttpSession session) {
		sessionContext.set(session);
	}

	public static void clearSessionContext() {
		sessionContext.remove();
	}

	public static void addAttribute2Session(String key, Object value) {
		HttpSession session = sessionContext.get();
		if (session != null) {
			session.setAttribute(key, value);
		}
	}

	public static void removeAttribute2Session(String key) {
		HttpSession session = sessionContext.get();
		if (session != null) {
			session.removeAttribute(key);
		}
	}

	public static Object getAttribute(String key) {
		HttpSession session = sessionContext.get();
		if (session != null) {
			return session.getAttribute(key);
		}
		return null;
	}

	public static void addAttribute2Session(String key, Object value, HttpServletRequest request) {
		request.getSession().setAttribute(key, value);
	}

	public static void removeAttribute2Session(String key, HttpServletRequest request) {
		request.getSession().removeAttribute(key);
	}

	public static Object getAttribute(String key, HttpServletRequest request) {
		return request.getSession().getAttribute(key);
	}

}
