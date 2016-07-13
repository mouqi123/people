package com.people.sotp.commons.base;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

public class SessionInvalidListener implements HttpSessionListener {

	@Override
	public void sessionCreated(HttpSessionEvent arg0) {
		// TODO Auto-generated method stub

	}

	/**
	 * 监听Session失效
	 */
	@Override
	public void sessionDestroyed(HttpSessionEvent arg0) {
		try {
			arg0.getSession().removeAttribute(GlobalParam.SESSION_USER);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
