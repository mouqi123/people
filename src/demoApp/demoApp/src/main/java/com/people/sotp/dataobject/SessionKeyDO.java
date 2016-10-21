package com.people.sotp.dataobject;

public class SessionKeyDO {
	
	private int id;
	private String sotpId;
	private String randa; //移动端生成的随机数
	private String randb;
	private String sessionKey;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getSotpId() {
		return sotpId;
	}
	public void setSotpId(String sotpId) {
		this.sotpId = sotpId;
	}
	public String getRanda() {
		return randa;
	}
	public void setRanda(String randa) {
		this.randa = randa;
	}
	public String getRandb() {
		return randb;
	}
	public void setRandb(String randb) {
		this.randb = randb;
	}
	public String getSessionKey() {
		return sessionKey;
	}
	public void setSessionKey(String sessionKey) {
		this.sessionKey = sessionKey;
	}
	
	
	

}
