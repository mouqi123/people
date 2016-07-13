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

	/**
	 * 系统菜单
	 */
	public static final String SESSION_SYSTEMMUNU = "session-systemmenu";

	/**
	 * redis标识
	 */
	public static boolean REDIS_FLAG = true;


	//redis 注册短信
	public static final String NodeRegisterCode ="redis.register.note.code";
	//redis 登陆短信
	public static final String NodeLoginCode ="redis.login.note.code";
	
	public static final String project = "/sotpAuth/fidoportal";
	
	public static final String picPath ="D:/download/";
	
	// 文件存储路径
	private String basePath;

	private String orderPath;

	public String getBasePath() {
		return basePath;
	}

	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}

	public String getOrderPath() {
		return orderPath;
	}

	public void setOrderPath(String orderPath) {
		this.orderPath = orderPath;
	}

	public static boolean isREDIS_FLAG() {
		return REDIS_FLAG;
	}

	public static void setREDIS_FLAG(boolean rEDIS_FLAG) {
		REDIS_FLAG = rEDIS_FLAG;
	}

	
	public static final int ParameterError=220;  //系统错误
	public static final int AppIdError=221;   //appId不匹配
	
	
	public static final int SignMatch=223;   //sign 不匹配
	
	public static final int paramError=224;   //参数错误
	public static final int UserExusts=225;  //手机号已注册
	
	public static final int UserExist=226;  //账号密码错误
 
	public static final int CardExist=227; //银行卡不存在 	

	
	
	/**
	 * 获取web工程跟路径
	 * 
	 * @return
	 */
	public String getWebRootPath() {
		String rootPath = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
		if (rootPath.indexOf("WEB-INF") > 0) {
			rootPath = rootPath.substring(0, rootPath.indexOf("/WEB-INF/classes"));//
		}
		if (rootPath.indexOf("%20") > 0) {
			rootPath = rootPath.replace("%20", " ");
		}
		if (rootPath.startsWith("/")) {
			rootPath = rootPath.substring(1);
		}
		return rootPath;
	}
	
	

}
