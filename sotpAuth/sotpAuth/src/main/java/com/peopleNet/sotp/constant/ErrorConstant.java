package com.peopleNet.sotp.constant;

/**
 * 错误常量code
 * 
 * @描述
 * @author wangxin
 * @created_at 2015年10月14日
 */
public class ErrorConstant {

	// 成功
	public static final int RET_OK = 0;

	// 100-199 参数相关
	public static final int PARA_LEN_ERR = 100;
	public static final int PARA_ILLEGAL = 101;
	public static final int PARA_LACK_ITEM = 102;
	public static final int PARA_TYPE_UNKOWN = 103;
	public static final int PARA_SOTPCODEFORMAT_ERR = 104;
	public static final int PARA_DEVINFOFORMAT_ERR = 105;
	public static final int PARA_SOTPCODETYPE_ERR = 106;
	public static final int PARA_PAYINFOFORMAT_ERR = 107;
	public static final int PARA_SERVICE_ERR = 108;
	public static final int PARA_AUTHFORMAT_ERR = 109;

	// 200-299 参数内容相关
	public static final int PARACONT_PLUGINID_NOEXIST = 200;
	public static final int PARACONT_USERID_NOEXIST = 201;
	public static final int PARACONT_PHONENUM_NOEXIST = 202;
	public static final int PARACONT_DEVINFO_NOEXIST = 203;
	public static final int PARACONT_USERID_NOTMATCH_SID = 204;
	public static final int PARACONT_PNUM_NOTMATCH_SID = 205;
	public static final int PARACONT_DEV_NOTMATCH_SID = 206;
	public static final int PARACONT_PLUGINID_NOEXIST_OR_NOREADY = 207;
	public static final int PARACONT_SIGN_NOTMATCH = 208;
	public static final int PARACONT_PLUGIN_OR_DEVINFO_NOTEXIST = 209;
	public static final int PARACONT_HASH_NOTMATCH = 210;

	// 300-399 插件相关
	public static final int PLUGIN_NOTACTIVE = 300;
	public static final int PLUGIN_WAITDOWN = 301;
	public static final int PLUGIN_READY = 302;
	public static final int PLUGIN_LOCKED = 303;
	public static final int PLUGIN_HANDUP = 304;
	public static final int PLUGIN_USELESS = 305;
	public static final int PLUGIN_UNKOWN_STATE = 306;
	public static final int PLUGIN_UPDATE_CYCLE = 307;
	public static final int PLUGIN_UPDATE_USENUM = 308;
	public static final int PLUGIN_UPDATE_ERRNUM = 309;
	public static final int PLUGIN_PREGENNUM_NOENOUGH = 310;
	public static final int PLUGIN_DOWN_NOWAITDOWN = 311;
	public static final int PLUGIN_REQDEVTYPE_UNKOWN = 312;
	public static final int PLUGIN_DEL_UID_SID_ERR = 313;
	public static final int PLUGIN_DEL_DEV_NOEXIST = 314;
	public static final int PLUGIN_HAVE_DOWNLOAD_MAX_TIMES = 315;
	public static final int PLUGIN_CHALLENGE_CODE_TIMEOUT = 316;
	public static final int PLUGIN_ACTIVECODE_STATUS_ERR = 317;
	public static final int PLUGIN_HAVE_ACTIVE_MAX_TIMES = 318;
	public static final int PLUGIN_CHALLENGEANS_FAIL = 319;
	public static final int PLUGIN_HAVE_BEEN_COPIED = 320;
	public static final int PLUGIN_AUTH_PINCODE_ERROR = 321;
	public static final int PLUGIN_AUTH_CHALLENGEAUTHCODE_ERROR = 322;
	public static final int PLUGIN_AUTH_TIMEAUTHCODE_ERROR = 323;
	public static final int PLUGIN_AUTH_PINAUTHCODE_ERROR = 324;
	public static final int PLUGIN_AUTH_PINANDCHANLLENGEAUTHCODE_ERROR = 325;
	public static final int PLUGIN_AUTH_CLIENTCHANLLENGEAUTHCODE_ERROR = 326;
	public static final int PLUGIN_STATUS_ERROR = 327;
	public static final int PLUGIN_RESISSUE_OLDINFO_ERROR = 328;

	// 400-499 密码机相关
	public static final int THOR_CONNECT_TIMEOUT = 400;
	public static final int THOR_CONNRESP_FORMAT_WRONG = 401;
	public static final int THOR_CONNRESP_TIMEOUT = 402;
	public static final int THOR_VERIFYCODE_ERROR = 403;
	public static final int THOR_PARAFORMAT_ERR = 404;
	public static final int THOR_SYSTEM_ERR = 405;
	public static final int THOR_BASE64_ERR = 406;
	public static final int THOR_MALLOC_ERR = 407;
	public static final int THOR_CONNECT_PROTOCOL_ERR = 408;
	public static final int THOR_RESP_UNKOWNERR = 409;
	public static final int THOR_VERIFY_DECRYPT_SOTPCODE_ERROR = 410;
	public static final int THOR_VERIFY_GET_CHALLENGE_FAIL = 411;
	public static final int THOR_VERIFY_GENERATE_ENTITY_ERROR = 412;
	public static final int THOR_ENCRYPT_ERROR = 413;

	// 500-599 存储相关
	public static final int SQL_CONREDIS_ERR = 500;
	public static final int SQL_CONMYSQL_ERR = 501;
	public static final int SQL_WRREDIS_ERR = 502;
	public static final int SQL_READREDIS_ERR = 503;
	public static final int SQL_WRMYSQL_ERR = 504;
	public static final int SQL_READMYSQL_ERR = 505;
	public static final int SQL_WRITEDB_ERR = 506;
	public static final int SQL_OPS_ERR = 507;

	// 600-699 系统错误
	public static final int SYSTEM_UNKOWN_ERR = 600;
	public static final int SYSTEM_FUN_ERR = 601;
	public static final int SYSTEM_MALLOC_ERR = 602;

	// 密码机返回错误码
	public static final int THOR_RES_OK = 0;
	public static final int THOR_RES_PARA_ERR = -1;
	public static final int THOR_RES_SYSTEM_ERR = -2;
	public static final int THOR_RES_BASE64_ERR = -3;
	public static final int THOR_RES_MALLOC_ERR = -4;
	public static final int THOR_PARA_ILLEGAL = -5;

	// redis相关
	public static final int REDIS_CONNECT_ERROR = 700;
	public static final int REDIS_OP_ERROR = 701;

	// 设备已root，但是策略不允许root的设备验证通过
	public static final int DEV_ROOT_ERR = 800;

	// 策略相关
	public static final int POLICY_FETCH_ERROR = 900;
	
	// 风控相关
	public static final int RISK_HIGHEST = 1000;
	
	//密码键盘解密失败
	public static final int KEYBOARDDECRYPTION_ERROR = 1100;

	// 检查devInfo失败
	public static final int DEV_CHECK_FAIL = 901;
		
	
}
