package com.peopleNet.sotp.crontab;

import java.sql.Date;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

import org.apache.thrift.TException;

import com.peopleNet.sotp.constant.Constant;
import com.peopleNet.sotp.dal.dao.pluginStatisticDtoMapper;
import com.peopleNet.sotp.dal.model.pluginInfoDto;
import com.peopleNet.sotp.dal.model.pluginStatisticDto;
import com.peopleNet.sotp.exception.BusinessException;
import com.peopleNet.sotp.log.LogUtil;
import com.peopleNet.sotp.service.*;
import com.peopleNet.sotp.thrift.service.SotpPlugin;
import com.peopleNet.sotp.util.CommonConfig;

/**
 * spring 定时任务service，此service由spring 来触发运行一些定时任务
 * 
 * @描述
 * @author wangxin
 * @created_at 2015年10月22日
 */

public class PreGenPlugin implements Runnable {
	private static LogUtil logger = LogUtil.getLogger(PreGenPlugin.class);
	private IEncryptPluginInfoService pluginInfoMapper;
	private IThriftInvokeService thriftInvokeService;
	private ICacheService cacheService;
	private IPluginService pluginService;
	private IDBOperationService dbOperationService;
	private pluginStatisticDtoMapper pluginStatistic;

	private int plugintype = 0;
	private int forlen = 0;

	private String PLUGIN_STRORE_MODE = CommonConfig.get("PLUGIN_SAVE_MODE").trim();

	PreGenPlugin() {
	}

	public PreGenPlugin(int plugintype, int count) {
		this.plugintype = plugintype;
		this.forlen = count;
	}

	public PreGenPlugin(int plugintype, int count, IEncryptPluginInfoService tpluginInfoMapper,
	        IThriftInvokeService thriftInvokeServicetmp, ICacheService tcacheService,
	        pluginStatisticDtoMapper pluginStatistic, IPluginService pluginService, ITestTransaction db,
	        IDBOperationService dbOperationService) {
		this.plugintype = plugintype;
		this.forlen = count;
		this.pluginInfoMapper = tpluginInfoMapper;
		this.thriftInvokeService = thriftInvokeServicetmp;
		this.pluginStatistic = pluginStatistic;
		this.cacheService = tcacheService;
		this.pluginService = pluginService;
		this.dbOperationService = dbOperationService;
	}

	/*
	 * 获取当前剩余的可用插件
	 */
	public static int getNowNum(int plugintype, pluginStatisticDtoMapper pluginStatisticM) {
		int sum = 0;
		int used = 0;

		logger.info("getNowNum start type:" + plugintype);

		// 获取 统计表
		pluginStatisticDto pluginStatisticinfo = null;
		try {
			pluginStatisticinfo = pluginStatisticM.selectByType(plugintype);
		} catch (SQLException e) {
			logger.error("selectByType sql error.msg:%s", e.toString());
			return (sum - used);
		}
		if (pluginStatisticinfo == null) {
			logger.info("pluginStatisticinfo no data type:" + plugintype);
		} else {
			sum = pluginStatisticinfo.getTotalNum();
			used = pluginStatisticinfo.getUsedNum();
			logger.info("androidStatistic --- sum:" + sum + ", used:" + used);
		}
		logger.info("getNowNum end sum:" + sum + ", used:" + used);
		return (sum - used);
	}

	@Override
	public void run() {
		int ret = 0;
		int oklen = 0;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
		// 开始预生成插件
		Thread current = Thread.currentThread();

		logger.info("thead:" + current.getId() + ", forlen:" + this.forlen);
		logger.error("pregen plugin start time: " + timeStamp(0) + ", time:"
		        + df.format(new Date(System.currentTimeMillis())));
		for (int i = 0; i < this.forlen; i++) {
			try {
				if (Constant.PLUGIN_CONTENT_SAVE_MODE.STORE_REDIS.equals(PLUGIN_STRORE_MODE)) {
					ret = TcpReqThorPlugin(this.plugintype, "18812341234", "TYPE@NAME@IMEI@MAC");
				} else if (Constant.PLUGIN_CONTENT_SAVE_MODE.STORE_DB.equals(PLUGIN_STRORE_MODE)) {
					ret = TcpReqThorPluginDB(this.plugintype, "18812341234", "TYPE@NAME@IMEI@MAC");
				} else if (Constant.PLUGIN_CONTENT_SAVE_MODE.STORE_FILE.equals(PLUGIN_STRORE_MODE)) {
					ret = TcpReqThorPluginFile(this.plugintype, "18812341234", "TYPE@NAME@IMEI@MAC");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (ret == 0)
				oklen += 1;

			logger.info("gen plugin ok oklen: " + oklen);
		}
		logger.error("pregen plugin over time: " + timeStamp(0) + ", time:"
		        + df.format(new Date(System.currentTimeMillis())));
		// 同步数据
		if (oklen > 0) {
			this.dbOperationService.updateStatistic(plugintype, oklen);
		}

		logger.info("thead:" + current.getId() + ", oklen:" + oklen);

	}

	/*
	 * 写入插件信息表: t_plugin_info
	 */
	public void insertPluginInfo(int type, String pluginId, String seed) {

		// logger.info("insertPluginInfo");

		Date date = new Date(System.currentTimeMillis());
		pluginInfoDto pluginInfo = new pluginInfoDto();

		pluginInfo.setPluginId(pluginId);
		pluginInfo.setPluginKey(seed);
		pluginInfo.setPluginType(type);
		pluginInfo.setGenTime(date);
		pluginInfo.setStatus(Constant.PLUGIN_STATUS.SOTP_PLUGIN_STATUS_NO_ACTIVATE);

		this.pluginInfoMapper.insert(pluginInfo);
		return;
	}

	/*
	 * 访问密码机请求插件 (tcp)
	 */
	public int TcpReqThorPlugin(int type, String phonenum, String devinfo) {
		int ret = 0;
		String pluginId = "";
		String seed = "";
		String plugin = "";

		logger.info("TcpReqThorPlugin start");

		// 解析硬件信息
		String[] splitd = devinfo.split("@");
		if (splitd.length != 4) {
			logger.info("devinfo not right：" + devinfo);
			return -1;
		}
		String dev = splitd[1] + "-" + splitd[2] + "-" + splitd[3];

		SotpPlugin sotpPluginInfo = null;
		try {
			sotpPluginInfo = this.thriftInvokeService.genSotpNew(type, phonenum, dev);
		} catch (TException e) {
			logger.error("invoke thrift genSotpNew error.msg:%s", e.toString());
		}
		if (sotpPluginInfo == null || sotpPluginInfo.status < 0) {
			logger.info("sotpthorReponse is null.");
			return -1;
		}

		pluginId = sotpPluginInfo.getVersion();
		seed = sotpPluginInfo.getSeed();
		plugin = sotpPluginInfo.getPlugin();

		// redis 写插件文件
		try {
			ret = this.cacheService.setPluginContent(pluginId, plugin);
		} catch (BusinessException e) {
			logger.error("setPluginContent exception. msg:%s[pluginId:%s]", e.toString(), pluginId);
		}
		if (ret != 0) {
			logger.error("this.cacheService.setPluginContent error");
			return -1;
		} else {
			logger.info("this.cacheService.setPluginContent ok");
		}

		// 插入插件信息表
		insertPluginInfo(type, pluginId, seed);

		return 0;
	}

	/*
	 * 访问密码机请求插件 (tcp)
	 */
	public int TcpReqThorPluginDB(int type, String phonenum, String devinfo) {
		int ret = 0;
		String pluginId = "";
		String seed = "";
		String plugin = "";

		logger.info("TcpReqThorPlugin start");

		// 解析硬件信息
		String[] splitd = devinfo.split("@");
		if (splitd.length != 4) {
			logger.info("devinfo not right：" + devinfo);
			return -1;
		}
		String dev = splitd[1] + "-" + splitd[2] + "-" + splitd[3];

		SotpPlugin sotpPluginInfo = null;
		try {
			sotpPluginInfo = this.thriftInvokeService.genSotpNew(type, phonenum, dev);
		} catch (TException e) {
			logger.error("invoke thrift genSotpNew error.msg:%s", e.toString());
		}
		if (sotpPluginInfo == null || sotpPluginInfo.status < 0) {
			logger.info("sotpthorReponse is null.");
			return -1;
		}

		pluginId = sotpPluginInfo.getVersion();
		seed = sotpPluginInfo.getSeed();
		plugin = sotpPluginInfo.getPlugin();

		logger.debug("pluginId:%s, pluginContent:%s", pluginId, plugin);

		ret = this.pluginService.savePluginIntoDB(pluginId, plugin);
		if (ret != 1) {
			logger.error("setPluginContent into db error");
			return -1;
		} else {
			logger.info("this.cacheService.setPluginContent ok");
		}

		// 插入插件信息表
		insertPluginInfo(type, pluginId, seed);

		return 0;
	}

	/*
	 * 访问密码机请求插件 (tcp)
	 */
	public int TcpReqThorPluginFile(int type, String phonenum, String devinfo) {
		int ret = 0;
		String pluginId = "";
		String seed = "";
		String plugin = "";

		// 解析硬件信息
		String[] splitd = devinfo.split("@");
		if (splitd.length != 4) {

			logger.info("devinfo not right：" + devinfo);
			return -1;
		}
		String dev = splitd[1] + "-" + splitd[2] + "-" + splitd[3];

		SotpPlugin sotpPluginInfo = null;
		try {
			sotpPluginInfo = this.thriftInvokeService.genSotpNew(type, phonenum, dev);
		} catch (TException e) {

			logger.error("invoke thrift genSotpNew error.msg:%s", e.toString());
		}
		if (sotpPluginInfo == null || sotpPluginInfo.status < 0) {

			logger.info("sotpthorReponse is null.");
			return -1;
		}

		pluginId = sotpPluginInfo.getVersion();
		seed = sotpPluginInfo.getSeed();
		plugin = sotpPluginInfo.getPlugin();

		logger.debug("pluginId:%s, pluginContent:%s", pluginId, plugin);

		ret = this.pluginService.savePluginIntoFile(pluginId, plugin);

		if (ret != 1) {

			logger.error("save plugin content into file error");
			return -1;
		} else {

			logger.info("save plugin content into file ok");
		}

		// 将插件id保存在redis中
		try {
			ret = this.cacheService.setPluginIdIntoList(Constant.REDIS_PLUGIN_ID_SAVE_LIST_NAME.WAIT_DOWNLOAD_LIST,
			        pluginId);
		} catch (BusinessException e) {

			logger.error("save waitingDownload plugin exception. msg:%s[pluginId:%s]", e.toString(), pluginId);
			return -1;
		}

		// 插入插件信息表
		insertPluginInfo(type, pluginId, seed);

		return 0;
	}

	public String timeStamp(long militime) {
		long time = 0;
		if (militime == 0) {
			time = System.currentTimeMillis();
		} else {
			time = militime;
		}

		String t = String.valueOf(time / 1000);
		return t;
	}

}
