package com.people.sotp.dataobject;

public class IssuerDO {
	
	private String account;
	
	private String secret;
	
	private String issuer;
	
	private int userId;

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public String getIssuer() {
		return issuer;
	}

	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}
	
	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	@Override
	public String toString() {
		return "account:"+account+", secret:"+secret+", issuer:"+issuer+", userId:"+userId;
	}
}
