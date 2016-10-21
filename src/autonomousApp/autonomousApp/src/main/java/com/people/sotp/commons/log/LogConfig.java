package com.people.sotp.commons.log;

/**
 * 日志配置
 */
public class LogConfig {

	/**
	 * 模块名称--登录认证模块
	 */
	public final static String SYS_LOGIN = "认证模块";

	/**
	 * 模块名称--系统配置
	 */
	public final static String SYS_MASTER = "管理员管理";
	public final static String SYS_ROLE = "平台角色管理";

	/**
	 * 模块名称--插件管理
	 */
	public final static String PLUGIN_MANAGER = "插件管理";

	/**
	 * 模块名称--策略管理
	 */
	public final static String RULE_POLICY = "规则类策略管理";

	/**
	 * 日志级别
	 */
	public final static int LOW_LEVEL = 1; // 低级
	public final static int MIDDLE_LEVEL = 2; // 中级
	public final static int HIGH_LEVEL = 3; // 高级

	/**
	 * 操作类型
	 */
	public final static String OPT_ADD = "添加";

	public final static String OPT_UPDATE = "修改";

	public final static String OPT_DELETE = "删除";

	public final static String OPT_AUTHENTICATION = "认证";

	public final static String OPT_AUTHORIZATION = "授权";

	public final static String OPT_RELOAD = "重载";

}
