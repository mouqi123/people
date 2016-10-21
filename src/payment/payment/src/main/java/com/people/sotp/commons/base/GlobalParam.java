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

	// redis更新策略信息
	public static final String POLICY_CODE_KEY_PREFIX = "redis.policy.code.";
	// redis更新插件状态
	public static final String PLUGIN_KEY_PREFIX = "redis.plugin.";

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

}
