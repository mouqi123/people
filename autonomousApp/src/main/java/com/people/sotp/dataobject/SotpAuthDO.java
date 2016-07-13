package com.people.sotp.dataobject;

import com.people.sotp.commons.base.BaseDO;

public class SotpAuthDO extends BaseDO {
	private long id;
	private String ip;
	private String post;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getPost() {
		return post;
	}

	public void setPost(String post) {
		this.post = post;
	}

}
