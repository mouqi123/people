package com.peopleNet.sotp.context;

/**
 * 上下文实现类
 *
 * @author wangxin
 */
public class UserContext implements Contextable {

	private String userId;

	private String userIp;

	private String userPhone;

	private String pluginId;

	private String interfaceName;

	private String appId;

	private String version;

	private String location;
	
	private String cityId;

	private String wifi;
	
	private String payee;
	
	private String price;
	
	private String action;
	
	private String devId;
	
	private String cardType;
	
	private String recCard;
	
	private String payCard;

	private int status;

	/**
	 * 默认构造方法
	 */
	public UserContext() {
		userId = "nil";
		userIp = "nil";
		userPhone = "nil";
		interfaceName = "nil";
		appId = "nil";
		version = "nil";
		pluginId = "nil";
		location = "nil";
		cityId = "nil";
		wifi = "nil";
		payee ="nil";
		price = "nil";
		action = "nil";
		devId = "nil";
		cardType = "nil";
		recCard = "nil";
		payCard = "nil";
		status = -10000;
	}

	public void setValue(String userId, String userIp, String userPhone, String pluginId, String interfaceName,
	        String appId, String version, String location,String cityId, String wifi, String payee, String price, String action,
	        String devId,String cardType,String recCard,String payCard) {
		this.userId = userId;
		this.userIp = userIp;
		this.userPhone = userPhone;
		this.pluginId = pluginId;
		this.interfaceName = interfaceName;
		this.appId = appId;
		this.version = version;
		this.location = location;
		this.cityId = cityId;
		this.wifi = wifi;
		this.payee = payee;
		this.price = price;
		this.action = action;
		this.devId = devId;
		this.cardType = cardType;
		this.recCard = recCard;
		this.payCard = payCard;
	}

	public void setValueExceptIp(String userId, String userPhone, String pluginId, String interfaceName, String appId,
	        String version, String location,String cityId, String wifi, String payee, String price, String action,String devId,
	        String cardType,String recCard,String payCard) {
		this.userId = userId;
		this.userPhone = userPhone;
		this.pluginId = pluginId;
		this.interfaceName = interfaceName;
		this.appId = appId;
		this.version = version;
		this.location = location;
		this.cityId = cityId;
		this.wifi = wifi;
		this.payee = payee;
		this.price = price;
		this.action = action;
		this.devId = devId;
		this.cardType = cardType;
		this.recCard = recCard;
		this.payCard = payCard;
		
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}
	
	public String getCityId() {
		return cityId;
	}

	public void setCityId(String cityId) {
		this.cityId = cityId;
	}

	public String getWifi() {
		return wifi;
	}

	public void setWifi(String wifi) {
		this.wifi = wifi;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserIp() {
		return userIp;
	}

	public void setUserIp(String userIp) {
		this.userIp = userIp;
	}

	public String getUserPhone() {
		return userPhone;
	}

	public void setUserPhone(String userPhone) {
		this.userPhone = userPhone;
	}

	public String getPluginId() {
		return pluginId;
	}

	public void setPluginId(String pluginId) {
		this.pluginId = pluginId;
	}

	public String getInterfaceName() {
		return interfaceName;
	}

	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}

	public String getPayee() {
		return payee;
	}

	public void setPayee(String payee) {
		this.payee = payee;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getDevId() {
		return devId;
	}

	public void setDevId(String devId) {
		this.devId = devId;
	}

	public String getCardType() {
		return cardType;
	}

	public void setCardType(String cardType) {
		this.cardType = cardType;
	}

	public String getRecCard() {
		return recCard;
	}

	public void setRecCard(String recCard) {
		this.recCard = recCard;
	}

	public String getPayCard() {
		return payCard;
	}

	public void setPayCard(String payCard) {
		this.payCard = payCard;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(" - [appId:" + ((null == appId) ? "nil" : appId) + "]");
		sb.append("[version:" + ((null == version) ? "nil" : version) + "]");
		sb.append("[user_id:" + ((null == userId) ? "nil" : userId) + "]");
		sb.append("[user_phone:" + ((null == userPhone) ? "nil" : userPhone) + "]");
		sb.append("[plugin_id:" + ((null == pluginId) ? "nil" : pluginId) + "]");
		sb.append("[interface_name:" + ((null == interfaceName) ? "nil" : interfaceName) + "]");
		sb.append("[status:" + status + "]");
		sb.append("[user_ip:" + ((null == userIp) ? "nil" : userIp) + "]");
		sb.append("[location:" + ((null == location) ? "nil" : location) + "]");
		sb.append("[cityId:" + ((null == cityId) ? "nil" : cityId) + "]");
		sb.append("[wifi:" + ((null == wifi) ? "nil" : wifi) + "]");
		sb.append("[payee:" + ((null == payee) ? "nil" : payee) + "]");
		sb.append("[price:" + ((null == price) ? "nil" : price) + "]");
		sb.append("[action:" + ((null == action) ? "nil" : action) + "]");
		sb.append("[devId:" + ((null == devId) ? "nil" : devId) + "]");
		sb.append("[cardType:" + ((null == cardType) ? "nil" : cardType) + "]");
		sb.append("[recCard:" + ((null == recCard) ? "nil" : recCard) + "]");
		sb.append("[payCard:" + ((null == payCard) ? "nil" : payCard) + "]");
		return sb.toString();
	}
}
