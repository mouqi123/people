package com.people.sotp.dataobject;

public class TransferLogDO {

	
	
	private String userName;
	private String peerName;
	private String peerCount;
	private String transMoney;
	private String phoneModel;
	private String transferTime;
	private int status;
	private String ipAddress;
	
	private int type;
	private String tradeNum;
	
	
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getTradeNum() {
		return tradeNum;
	}
	public void setTradeNum(String tradeNum) {
		this.tradeNum = tradeNum;
	}
	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPeerName() {
		return peerName;
	}
	public void setPeerName(String peerName) {
		this.peerName = peerName;
	}
	public String getPeerCount() {
		return peerCount;
	}
	public void setPeerCount(String peerCount) {
		this.peerCount = peerCount;
	}
	public String getTransMoney() {
		return transMoney;
	}
	public void setTransMoney(String transMoney) {
		this.transMoney = transMoney;
	}
	public String getPhoneModel() {
		return phoneModel;
	}
	public void setPhoneModel(String phoneModel) {
		this.phoneModel = phoneModel;
	}
	public String getTransferTime() {
		return transferTime;
	}
	public void setTransferTime(String transferTime) {
		this.transferTime = transferTime;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	
	
}
