package com.people.sotp.commons.base;

import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import com.people.sotp.dataobject.MasterDO;

public class SessionListener implements HttpSessionBindingListener {

	public static ConcurrentHashMap<String, MasterDO> ONLINE_USER_LIST = new ConcurrentHashMap<String, MasterDO>();

	public void valueBound(HttpSessionBindingEvent event) {

		HttpSession session = event.getSession();
		String sessionid = session.getId();
		MasterDO master = (MasterDO) session.getAttribute("");
		ONLINE_USER_LIST.put(sessionid, master);
	}

	public void valueUnbound(HttpSessionBindingEvent event) {
		ONLINE_USER_LIST.remove(event.getSession().getId());
	}

}
