package com.peopleNet.sotp.constant;


/**
 * 常量类
 * 
 * @描述
 * @author wangxin
 * @created_at 2015年10月14日
 */
public class Constant {

	public static final long MILLISECONDS_ONE_DAY = 86400000;
    public static final long MILLISECONDS_15_MINITES = 900000;
    public static final long MILLISECONDS_30_MINITES = 1800000;
    public static final long MILLISECONDS_60_MINITES = 3600000;
    public static final long MILLISECONDS_120_MINITES = 7200000;
    public static final long MILLISECONDS_1_MINITES = 60000;
    public static final long MILLISECONDS_3_MINITES = 180000;

	public static final char seperator = '|';

	public interface REQUEST_FLAG {
		public static final int SUCCESS = 1;
		public static final int FAILURE = 0;
	}

	public interface USEDYNAMICPOLICY {
		public static final int USEDYNAMICPOLICY = 1;
		public static final int NOTUSEDYNAMICPOLICY = 0;
	}

	public interface ISALLOWROOT {
		public static final int ISALLOWROOT = 1;
		public static final int NOTALLOWROOT = 0;
	}

	public interface ISROOT {
		public static final String ISROOT = "yes";
		public static final String NOTROOT = "no";
	}
	public interface ISCHECKSIGN{
		public static final String ISCHECK="yes";
	}	
	
	public interface APPGRADE {
		public static final int FULL = 100;
	}

	public interface AUTOUNLOCK {
		public static final int ALLOW = 1;
		public static final int DENY = 0;
	}
	public interface CHECKDEVINFO{
		public static final double MINMATCH=0.5;
		public static final int LEASTMATCH=1;
	}
	
	
	
	// !证件种类
	public interface IDTYPE {

		public static final int IDCARD = 0; // !居民身份证
		public static final int PARSPORT = 1; // !护照
		public static final int OFFICERS = 2; // !军官证
		public static final int HOMERETURNPERMIT = 3; // !回乡证
		public static final int TAIWANCOMPATRIOTS = 4; // !台胞证

		public static final int START = IDCARD; // !居民身份证
		public static final int MAX = 5;
	}

	public interface REDIS_CONSTANT {
		public static final String USER_KEY = "redis.user";
		public static final String USER_KEY_PREFIX = "redis.user.";
		public static final String PLUGIN_KEY_PREFIX = "redis.plugin.";
		public static final String PLUGINCONTENT_KEY_PREFIX = "redis.plugincontent.";
		public static final String POLICY_STATUS_KEY_PREFIX = "redis.policy.status.";
		public static final String POLICY_ID_KEY_PREFIX = "redis.policy.id.";
		public static final String STATISTIC_NUMBER_KEY_PREFIX = "redis.statistic.number.";
		public static final String POLICY_CODE_KEY_PREFIX = "redis.policy.code.";
		public static final String AUTH_POLICY_CODE_KEY_PREFIX = "redis.policy.code.authfactorpolicy.";
		public static final String USER_REQUEST_KEY_PREFIX = "redis.user_request.";
		public static final String USER_REQUEST_FREQUENCY_KEY_PREFIX = "redis.user_request_frequency.";
		public static final String PLUGIN_LOCKED_KEY_PREFIX = "redis.lockedplugin.";
		public static final String PLUGIN_USER_CHALLENGE_CODE = "redis.challengecode.";
		public static final String PLUGIN_USER_SESSION_KEY = "redis.sessionkey.";
		public static final String PLUGIN_AUTH_POLICY = "redis.authpolicydto.";
		public static final String APPLY_CODE_KEY_PREFIX = "redis.app.code.";
		public static final String PLUGIN_USER_VERFIY_ID = "redis.userverifyinfo.";
		public static final String VERIFYDATAITEM_KEY_PREFIX = "redis.app.code.verifydataitem.";
		public static final Long USER_VERIFY_WINDOWTIME = 300L;
		public static final Long AUTH_POLICY_WINDOWTIME = 300L;
        public static final String LAST15MIN = "15min.";
        public static final String LAST30MIN = "30min.";
        public static final String LAST60MIN = "60min.";
        public static final String LAST120MIN = "120min.";
        public static final String LAST1MIN = "1min.";
        public static final String LAST3MIN = "3min.";
	}

	// 认证服务业务类型
	public interface SERVICE_TYPE {
		public static final int SOTP_UNKOWN_TYPE = -1;

		public static final int SOTP_PLUGIN_REGISTER_TYPE = 1020;
		public static final int SOTP_PLUGIN_REGISTER_ONLINE_TYPE = 1021;
		public static final int SOTP_PLUGIN_REGISTER_OUTLINE_TYPE = 1022;
		public static final int SOTP_PLUGIN_DOWN_TYPE = 1025;
		public static final int SOTP_PLUGIN_UPDATE_TYPE = 1030;
		public static final int SOTP_PLUGIN_UPDATE_ONLINE_TYPE = 1031;
		public static final int SOTP_PLUGIN_UPDATE_OUTLINE_TYPE = 1032;
		public static final int SOTP_PLUGIN_DEL_TYPE = 1040;

		public static final int SOTP_CRYPTO_DATA_TYPE = 1061;
		public static final int SOTP_DECRYPTO_DATA_TYPE = 1062;

		public static final int SOTP_VERIFY_SOTPCODE_TYPE = 1070;
		public static final int SOTP_REGISTER_SMS_TYPE = 1080;

		public static final int SOTP_GET_PLUGIN_STATUS = 1010;
		public static final int SOTP_GET_BIND_DEVLIST = 1050;
		public static final int SOTP_GET_CHALLENGE_CODE = 1090;

		public static final int SOTP_MOD_PROTECT_CODE = 1091;
		public static final int SOTP_MOD_HOLD_INFO = 1092;

		public static final int SOTP_VERIFY_ACTIVE_CODE = 1100;

		public static final int SOTP_SET_PIN = 1201;

		public static final int SOTP_SERVER_AUTH = 1301;
		public static final int SOTP_VERIFY_CLIENT = 1302;
		public static final int SESSKEY_CRYPTO = 1303;
	}

	// 业务请求参数名称
	public interface PARA_NAME {
		public static final String USERID = "userId";
		public static final String PHONENUM = "phoneNum";
		public static final String SOTPID = "sotpId";
		public static final String SOTPCODE = "sotpCode";
		public static final String DEVINFO = "devInfo";
		public static final String SOTPPAYINFO = "sotpPayInfo";
		public static final String ACTION = "action";
		// 预留信息
		public static final String HOLDINFO = "holdinfo";
		// 保护码
		public static final String PEOTECTCODE = "protect";
		public static final String SMS = "sms";
		// 交易信息hash
		public static final String SOTPCODEPARA = "sotpCodePara";
		// 下载插件url
		public static final String DOWNURL = "url";
		// 业务类型
		public static final String TYPE = "type";
		// 所在区域
		public static final String LOCATION = "location";
		// 应用Id
		public static final String APPID = "appId";
		// 参数签名值
		public static final String SIGN = "sign";
		// 随机数字符串
		public static final String NONCE_STR = "nonce_str";
		// 激活码
		public static final String ACTIVECODE = "activeCode";
		// 接口服务名称
		public static final String SERVICENAME = "service";
		// 版本信息
		public static final String VERSION = "version";
		// WIFI
		public static final String WIFI = "wifiInfo";
		//payee
		public static final String PAYEE = "payee";
		//价格
		public static final String PRICE = "price";
		// IP
		public static final String IP = "wifi";
		//dev_id
		public static final String DEV_ID = "dev";
		//payCardType 
		public static final String CARDTYPE = "payCardType";
		//recCard
		public static final String RECCARD = "recCard";
		//payCard
		public static final String PAYCARD = "payCard";
		//payAction
		public static final String PAYACTION = "payAction";
	}
	 // 认证业务类型
    public interface PAY_ACTION {
        public static final String PAY = "pay";
        public static final String TRANSFER = "transfer";
        public static final String FINANCE = "finance";
    }

	// 业务请求参数长度
	public interface PARA_LEN {
		public static final int USERID_LEN = 64;
		
		public static final int PHONENUM_LEN = 64;
		public static final int SIGN_LEN = 128;
		public static final int NONCE_STR_LEN = 32;
		public static final int SOTPID_LEN = 64;
		public static final int DEVID_LEN = 128;
		public static final int ENVINFO_LEN = 256;
		public static final int SOTPCODE_LEN = 64;
		public static final int ACTIVECODE_LEN = 64;
		public static final int DEVINFO_LEN = 512;
		public static final int APPID_LEN = 128;
		// 预留信息+保护码
		public static final int SOTPPAYINFO_LEN = 128;
		// 预留信息
		public static final int HOLDINFO_LEN = 32;
		// 保护码
		public static final int PEOTECTCODE_LEN = 16;
		// 业务编码businessName
		public static final int BUSINESSNAME = 16;
		public static final int SMS_LEN = 8;
		// 交易信息hash
		public static final int SOTPCODEPARA_LEN = 128;
		// 下载插件url
		public static final int SOTPDOWNURL_LEN = 128;
	}

	// 移动终端设备类型
	public interface MOBILE_TYPE {
		public static final String ANDROID = "Android";
		public static final String IOS = "Ios";
		public static final String WIN_PHONE = "winphone";

		public static final String ANDROID_ARM = "arm";
		public static final String ANDROID_X86 = "x86";
		public static final String ANDROID_MIPS = "mips";
	}

	// 连接密码机业务类型
	public interface SERVICE_THOR_TYPE {
		public static final int SOTP_THOR_VERIFY_SOTPCODE = 1005;
	}

	public interface PLUGIN_UPDATE {
		public static final int NEED_UPDATE = 1;
		public static final int NOT_NEED_UPDATE = 0;
	}

	// 插件状态码
	public interface PLUGIN_STATUS {
		public static final int SOTP_PLUGIN_STATUS_NO_ACTIVATE = 1;
		public static final int SOTP_PLUGIN_STATUS_WAIT_DOWN = 2;
		public static final int SOTP_PLUGIN_STATUS_READY = 3;
		public static final int SOTP_PLUGIN_STATUS_LOCKED = 4;
		public static final int SOTP_PLUGIN_STATUS_HANGUP = 5;
		public static final int SOTP_PLUGIN_STATUS_USELESS = 6;
		public static final int SOTP_PLUGIN_STATUS_NEEDUPDATE = 7;
	}

	// 策略名称 field
	public interface POLICY_NAME {
		public static final String MOTPTYPE = "motpType";
		public static final String PASSWORDTYPE = "passwordType";
		public static final String AUTHWINDOWSIZE = "authWindowSize";
		public static final String PASSWORDLENGTH = "passwordLength";
		public static final String ERRORTIMES = "errorTimes";
		public static final String ISUNLOCK = "isUnlock";
		public static final String AUTOUNLOCKTIME = "autoUnlockTime";
		public static final String SMSTIMEOUT = "smsTimeout";
		public static final String DEVICECNT = "deviceCnt";
		public static final String UPDATECYCLE = "updateCycle";
		public static final String TOTALCSECNT = "totalCsecnt";
		public static final String TOTALERRCNT = "totalErrcnt";
		public static final String PREGENERATCOUNT = "pregeneratCount";
		public static final String PREGMONITORCOUNT = "pregMonitorCount";
		public static final String REQSMSNUM = "reqsmsnum";
		public static final String REQNUM = "reqnum";
		public static final String TIMEOUT = "timeout";
		public static final String GENTYPE = "gentype";
		public static final String CHALLENGECODETIMEOUT = "challengeTimeout";
		public static final String PLUGININITSTATUS = "pluginInitStatus";
		public static final String ACTIVATIONCOUNT = "activationCount";
		public static final String ISINTEGRITYCHECK = "isIntegrityCheck";
		public static final String PLUGINTYPE = "pluginType";
		public static final String ISALLOWROOT = "isAllowRoot";

	}

	// 策略类型
	public interface POLICY_TYPE {
		public static final String APPLY = "pluginapplypolicy";
		public static final String UPDATE = "pluginpolicy";
		public static final String VERIFY = "sotpverifypolicy";
		public static final String ACTIVE = "pluginactivationpolicy";
	}

	// 策略状态
	public interface POLICY_STATUS {
		public static final int SOTP_POLICY_STATUS_NO_ACTIVATE = 0;
		public static final int SOTP_POLICY_STATUS_ACTIVATE = 1;
	}

	// 策略默认值
	public interface POLICY_DEFAULTVAL {
		public static final int POLICY_DEFAULTVAL_WINDOWTIME = 30;
	}

	// 插件生成方式
	public interface PLUGIN_GEN_TYPE {
		public static final int SOTP_PLUGIN_GEN_ONLINE = 1;
		public static final int SOTP_PLUGIN_GEN_PREGEN = 2;
		// 先预生成，若插件不足，则在线生成
		public static final int SOTP_PLUGIN_GEN_PREORONLINE = 3;
	}

	// 插件类型
	public interface PLUGIN_TYPE {
		public static final int SOTP_PLUGIN_TYPE_UNKOWN = 0;
		public static final int SOTP_PLUGIN_TYPE_TEST = 1;
		public static final int SOTP_PLUGIN_TYPE_IOS = 2;
		public static final int SOTP_PLUGIN_TYPE_ANDROID_ARM = 3;
		public static final int SOTP_PLUGIN_TYPE_ANDROID_X86 = 4;
		public static final int SOTP_PLUGIN_TYPE_ANDROID_MIPS = 5;
		public static final int SOTP_PLUGIN_TYPE_WINDOWS_PHONE = 6;
		public static final int SOTP_PLUGIN_TYPE_PC = 7;
		public static final int SOTP_PLUGIN_TYPE_NOENCRYPT = 10;
		public static final int SOTP_PLUGIN_TYPE_STATIC = 11;

	}

	// 插件下载状态
	public interface PLUGIN_DOWNLOAD_TYPE {
		// 等待下载
		public static final int SOTP_PLUGIN_DOWNlOAD_TYPE_WAIT = 0;
		// 下载中
		public static final int SOTP_PLUGIN_DOWNLOAD_TYPE_IN_PROCESS = 1;
		// 下载完成（成功）
		public static final int SOTP_PLUGIN_DOWNLOAD_TYPE_DONE = 2;
		// 下载失败
		public static final int SOTP_PLUGIN_DOWNLOAD_TYPE_FAIL = 3;
	}

	// 连接密码机协议
	public interface THOR_PROTOCOL {
		public static final int SOTP_CONNECT_THOR_PROTOCOL_HTTP = 1;
		public static final int SOTP_CONNECT_THOR_PROTOCOL_TCP = 2;
	}

	// 返回状态码
	public interface RES_STATE_CODE {
		public static final int SOTP_STATE_OK = 0;
		public static final int SOTP_STATE_PARAMETER_WRONG = 90001;
		public static final int SOTP_STATE_PLUGINID_NOT_EXIST = 90002;
		public static final int SOTP_STATE_USERID_NOT_MATCH = 90003;
		public static final int SOTP_STATE_USERPHONE_NOT_MATCH = 90004;
		public static final int SOTP_STATE_DEVINFO_NOT_MATCH = 90005;
		public static final int SOTP_STATE_PLUGIN_STATUS_NOT_READY = 90006;
		public static final int SOTP_STATE_CONN_THOR_WRONG = 90007;
		public static final int SOTP_STATE_THOR_VERYIFY_WRONG = 90008;
		public static final int SOTP_STATE_SYSTEM_ERROR = 90009;
		public static final int SOTP_STATE_UPDATE_CYCLE = 90010;
		public static final int SOTP_STATE_UPDATE_USENUM = 90011;
		public static final int SOTP_STATE_UPDATE_ERRNUM = 90012;
		public static final int SOTP_STATE_UPDATE_LOCKED_OR_HANGUP = 90013;
		public static final int SOTP_STATE_UPDATE_USELESS = 90014;
	}

	// 展示页面相关常量
	public interface EXHIBITION_NAME {
		public static final String BEIJING_TRADE = "beijing_trade";
		public static final String SHANGHAI_TRADE = "shanghai_trade";
		public static final String GUANGZHOU_TRADE = "guangzhou_trade";

		public static final String E_PAY = "e_pay";
		public static final String ONE_PAY = "one_pay";
		public static final String QUICK_PAY = "quick_pay";
		public static final String HCE = "hce";
		public static final String EPOS = "epos";

		public static final String BEIJING_USER = "beijing_user";
		public static final String SHANGHAI_USER = "shanghai_user";
		public static final String GUANGZHOU_USER = "guangzhou_user";

		public static final String BEIJING_REALTIME = "beijing_realtime";
		public static final String SHANGHAI_REALTIME = "shanghai_realtime";
		public static final String GUANGZHOU_REALTIME = "guangzhou_realtime";

		public static final String TRADE_LOG = "trade_log";

		public static final String PROVINCE_USER_AMOUNT = "province_user_amount";

		public static final String RT_CENTER_BUSINESS_AMOUNT = "realtime_center_business_amount";
	}

	// 插件内容保存方式
	public interface PLUGIN_CONTENT_SAVE_MODE {
		public static final String STORE_REDIS = "redis";
		public static final String STORE_DB = "db";
		public static final String STORE_FILE = "file";
	}

	// redis中保存插件id的list
	public interface REDIS_PLUGIN_ID_SAVE_LIST_NAME {
		public static final String WAIT_DOWNLOAD_LIST = "waitDownloadPluginList";
		public static final String HAS_DOWNLOAD_LIST = "hasDownloadPluginList";
		public static final String HAS_LOCKED_LIST = "hasLockedPluginList";
	}

	// 系统信息
	public interface SYSTEM_INFO {
		public static final String API_VISITOR = "apiVistor";
		public static final String API_VISIT_TIME = "apiVisitTime";
	}

	// 签名方法
	public interface SIGN_METHOD {
		public static final String MD5 = "md5";
		public static final String SHA = "sha1";
	}

	// t_plugin_info表个字段名称
	public interface COL_NAME_OF_T_PLUGIN_INFO_TABLE {
		public static final String ID = "id";
		public static final String PLUGIN_ID = "plugin_id";
		public static final String PLUGIN_KEY = "plugin_key";
		public static final String PLUGIN_TYPE = "plugin_type";
		public static final String STATUS = "status";
		public static final String DEVICE_INFO = "device_info";
		public static final String START_TIME = "start_time";
		public static final String GEN_TIME = "gen_time";
		public static final String HOLD_INFO = "hold_info";
		public static final String PROTECT_CODE = "protect_code";
		public static final String BIND_USERID = "bind_userid";
		public static final String BIND_USERPHONE = "bind_userphone";
		public static final String VERIFY_ERRCNT = "verify_errcnt";
		public static final String TOTAL_USECNT = "total_usecnt";
		public static final String TOTAL_ERRCNT = "total_errcnt";
		public static final String ERR_DAY = "err_day";
		public static final String CHALLENGE_CODE = "challenge_code";
		public static final String ACTIVE_USECNT = "active_usecnt";
		public static final String APP_CODE = "app_code";
		public static final String USE_COUNT = "use_count";
		public static final String NEED_UPDATE = "need_update";
		public static final String DEV_INFO="dev_info";
	}
}
