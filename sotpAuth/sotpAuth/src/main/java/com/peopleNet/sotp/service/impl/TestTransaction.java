package com.peopleNet.sotp.service.impl;

import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.peopleNet.sotp.constant.Constant;
import com.peopleNet.sotp.dal.dao.androidStatisticDtoMapper;
import com.peopleNet.sotp.dal.dao.iosStatisticDtoMapper;
import com.peopleNet.sotp.dal.dao.pluginStatisticDtoMapper;
import com.peopleNet.sotp.dal.model.androidStatisticDto;
import com.peopleNet.sotp.dal.model.iosStatisticDto;
import com.peopleNet.sotp.dal.model.pluginStatisticDto;
import com.peopleNet.sotp.log.LogUtil;
import com.peopleNet.sotp.service.ITestTransaction;

@Service
public class TestTransaction implements ITestTransaction {

	private static LogUtil logger = LogUtil.getLogger(EncryptPluginInfoServiceImpl.class);
	@Autowired
	private androidStatisticDtoMapper androidStatisticMapper;
	@Autowired
	private iosStatisticDtoMapper iosStatisticMapper;
	@Autowired
	private pluginStatisticDtoMapper pluginStatisticMapper;

	public void insertDataForTestTransaction() throws Exception {
		androidStatisticDto android = new androidStatisticDto();
		android.setTotalNum(1000);
		iosStatisticDto ios = new iosStatisticDto();
		ios.setTotalNum(1001);
		try {
			androidStatisticMapper.insert(android);
		} catch (SQLException e) {
			logger.error("androidStatisticMapper insert error.msg:%s", e.toString());
		}
		try {
			iosStatisticMapper.insert(ios);
		} catch (SQLException e) {
			logger.error("iosStatisticMapper insert error.msg:%s", e.toString());
		}

		if (true)
			throw new Exception();
	}

	/*
	 * 更新统计表
	 */
	public synchronized void updateStatistic(int plugintype, int totalnum) {

		// 更新 统计表
		switch (plugintype) {
		case Constant.PLUGIN_TYPE.SOTP_PLUGIN_TYPE_ANDROID_ARM:

			// this.androidStatisticMapper.update_AddTotalNum(totalnum);

			pluginStatisticDto pStatistic = null;
			try {
				pStatistic = pluginStatisticMapper.selectByType(plugintype);
			} catch (SQLException e) {
				logger.error("selectByType sql error.msg:%s", e.toString());
			}
			if (pStatistic != null) {
				pStatistic.setTotalNum(pStatistic.getTotalNum() + totalnum);
				try {
					pluginStatisticMapper.updateByPrimaryKeySelective(pStatistic);
				} catch (SQLException e) {
					logger.error("updateByPrimaryKeySelective sql error.msg:%s", e.toString());
				}
			}

			/*
			 * androidStatisticDto androidStatisticinfo =
			 * this.androidStatisticMapper.select(); if (androidStatisticinfo ==
			 * null) { androidStatisticinfo = new androidStatisticDto(); }
			 * androidStatisticinfo.setTotalNum(totalnum +
			 * androidStatisticinfo.getTotalNum());
			 * this.androidStatisticMapper.updateByPrimaryKeySelective(
			 * androidStatisticinfo);
			 */
			break;

		case Constant.PLUGIN_TYPE.SOTP_PLUGIN_TYPE_IOS:
			// TODO
			break;

		case Constant.PLUGIN_TYPE.SOTP_PLUGIN_TYPE_WINDOWS_PHONE:
			// TODO
			break;
		case Constant.PLUGIN_TYPE.SOTP_PLUGIN_TYPE_PC:
			// TODO
			break;

		default:
			break;
		}

		return;
	}
}
