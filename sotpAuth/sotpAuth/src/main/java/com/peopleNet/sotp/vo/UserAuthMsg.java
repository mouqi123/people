package com.peopleNet.sotp.vo;

import java.math.BigDecimal;
import java.util.Date;

public class UserAuthMsg {
    // 认证事件ID
    private String authEventID;

    // 认证时间
    private Date authTime;

    // 账号
	private String userId;

    // 手机号
	private String phoneNum;

    // 设备信息
	private String devInfo;

    // 业务名称
	private String businessName;

    // 接口名称
	private String service;

    // 地理位置信息
	private String location;

    // IP信息
	private String ip;

    // wifi信息
	private String wifiInfo;

    // 支付\转账卡号
    private String payCard;

    // 支付\转账卡类型
    private String payCardType;

    // 收账卡号
    private String recCard;

    // 价格
    private BigDecimal price;

    // 交易类型:支付\转账\理财等
    private String payAction;

    // 收款人
    private String payee;

    // 应用ID
    private String appID;

    // 城市ID
    private String cityID;

    // 插件id
    private String pluginId;

    // 插件状态
    private String pluginStatus;

    // 该插件使用累计错误次数
    private int errCnt;

    // 过去1分钟的认证次数
    private int visitNumLast1mins = 0;

    // 过去3分钟的认证次数
    private int visitNumLast3mins = 0;

    // 过去15分钟的认证次数
    private int visitNumLast15mins = 0;

    // 过去30分钟的认证次数
    private int visitNumLast30mins = 0;

    // 过去60分钟的认证次数
    private int visitNumLast60mins = 0;

    // 过去120分钟的认证次数
    private int visitNumLast120mins = 0;

    // 今天的认证次数
    private int verifyNumToday = 0;

    public UserAuthMsg() {

    }

    public UserAuthMsg(UserRequestMsgFido requestMsg) {
        this.authEventID = new Date().getTime() + "_" + requestMsg.getPhoneNum();
        this.authTime = new Date();
        this.userId = requestMsg.getUserId();
        this.phoneNum = requestMsg.getPhoneNum();
        this.devInfo = requestMsg.getDevInfo();
        this.businessName = requestMsg.getBusinessName();
        this.service = requestMsg.getService();
        this.location = requestMsg.getLocation();
        this.ip = requestMsg.getIp();
        this.wifiInfo = requestMsg.getWifiInfo();
        this.payAction = requestMsg.getPayAction();
        this.payCard = requestMsg.getPayCard();
        this.payCardType = requestMsg.getPayCardType();
        this.recCard = requestMsg.getRecCard();
        this.payee = requestMsg.getPayee();
        this.price = requestMsg.getPrice();
        this.appID = requestMsg.getAppId();
        this.cityID = "00001";
        this.pluginId = requestMsg.getPluginId();
        this.pluginStatus = "3";
        this.errCnt = 0;
    }

    public String getAuthEventID() {
        return authEventID;
    }

    public void setAuthEventID(String authEventID) {
        this.authEventID = authEventID;
    }

    public Date getAuthTime() {
        return authTime;
    }

    public void setAuthTime(Date authTime) {
        this.authTime = authTime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getDevInfo() {
        return devInfo;
    }

    public void setDevInfo(String devInfo) {
        this.devInfo = devInfo;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getWifiInfo() {
        return wifiInfo;
    }

    public void setWifiInfo(String wifiInfo) {
        this.wifiInfo = wifiInfo;
    }

    public String getPayCard() {
        return payCard;
    }

    public void setPayCard(String payCard) {
        this.payCard = payCard;
    }

    public String getPayCardType() {
        return payCardType;
    }

    public void setPayCardType(String payCardType) {
        this.payCardType = payCardType;
    }

    public String getRecCard() {
        return recCard;
    }

    public void setRecCard(String recCard) {
        this.recCard = recCard;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getPayAction() {
        return payAction;
    }

    public void setPayAction(String payAction) {
        this.payAction = payAction;
    }

    public String getPayee() {
        return payee;
    }

    public void setPayee(String payee) {
        this.payee = payee;
    }

    public String getAppID() {
        return appID;
    }

    public void setAppID(String appID) {
        this.appID = appID;
    }

    public String getCityID() {
        return cityID;
    }

    public void setCityID(String cityID) {
        this.cityID = cityID;
    }

    public String getPluginId() {
        return pluginId;
    }

    public void setPluginId(String pluginId) {
        this.pluginId = pluginId;
    }

    public String getPluginStatus() {
        return pluginStatus;
    }

    public void setPluginStatus(String pluginStatus) {
        this.pluginStatus = pluginStatus;
    }

    public int getErrCnt() {
        return errCnt;
    }

    public void setErrCnt(int errCnt) {
        this.errCnt = errCnt;
    }

    public int getVisitNumLast1mins() {
        return visitNumLast1mins;
    }

    public void setVisitNumLast1mins(int visitNumLast1mins) {
        this.visitNumLast1mins = visitNumLast1mins;
    }

    public int getVisitNumLast3mins() {
        return visitNumLast3mins;
    }

    public void setVisitNumLast3mins(int visitNumLast3mins) {
        this.visitNumLast3mins = visitNumLast3mins;
    }

    public int getVisitNumLast15mins() {
        return visitNumLast15mins;
    }

    public void setVisitNumLast15mins(int visitNumLast15mins) {
        this.visitNumLast15mins = visitNumLast15mins;
    }

    public int getVisitNumLast30mins() {
        return visitNumLast30mins;
    }

    public void setVisitNumLast30mins(int visitNumLast30mins) {
        this.visitNumLast30mins = visitNumLast30mins;
    }

    public int getVisitNumLast60mins() {
        return visitNumLast60mins;
    }

    public void setVisitNumLast60mins(int visitNumLast60mins) {
        this.visitNumLast60mins = visitNumLast60mins;
    }

    public int getVisitNumLast120mins() {
        return visitNumLast120mins;
    }

    public void setVisitNumLast120mins(int visitNumLast120mins) {
        this.visitNumLast120mins = visitNumLast120mins;
    }

    public int getVerifyNumToday() {
        return verifyNumToday;
    }

    public void setVerifyNumToday(int verifyNumToday) {
        this.verifyNumToday = verifyNumToday;
    }
}
