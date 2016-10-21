package com.people.sotp.commons.base;

/**
 * 全局参数
 * 
 * @author tianchk
 */
public class GlobalParam {

	/**
	 * 用户对象
	 */
	public static final String SESSION_USER = "user";

	public static final String project = "/sotpAuth/fidoportal";	
	public static final int authErroe=200;
	
	public static final int AppIdError=221;   //appId不匹配
	
	public static final int ParameterError=220;  //系统错误
	
	public static final int SignMatch=223;   //sign 不匹配
	
	public static final int Balance=224;  //余额不足
	
	public static final int UserAndPwd=225;  //密码错误
	
	public static final int UserExist=226;  //账号存在
 
	
	public static final int CardExist=227; //银行卡不存在 	
	
	public static final int UserNotExist= 228;//账号不存在



}
