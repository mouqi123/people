package com.peopleNet.sotp.service.impl;

import java.io.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.peopleNet.sotp.constant.Constant;
import com.peopleNet.sotp.constant.ErrorConstant;
import com.peopleNet.sotp.dal.dao.pluginContentDtoMapper;
import com.peopleNet.sotp.dal.model.RulePolicyDto;
import com.peopleNet.sotp.dal.model.pluginContentDto;
import com.peopleNet.sotp.exception.BusinessException;
import com.peopleNet.sotp.log.LogUtil;
import com.peopleNet.sotp.service.ICacheService;
import com.peopleNet.sotp.service.IEncryptPluginInfoService;
import com.peopleNet.sotp.service.IPluginService;
import com.peopleNet.sotp.util.CommonConfig;
import com.peopleNet.sotp.util.CreateFileUtil;
import com.peopleNet.sotp.util.UpdateUtil;

@Service
public class PluginServiceImpl implements IPluginService {

	private static LogUtil logger = LogUtil.getLogger(PluginServiceImpl.class);
	@Autowired
	private pluginContentDtoMapper contentMapper;
	@Autowired
	private ICacheService cacheService;
	@Autowired
	private IEncryptPluginInfoService pluginInfoMapper;

	private String PLUGIN_STRORE_FILE_DIR = CommonConfig.get("PLUGIN_SAVE_FILE_PATH").trim();
	private int dirNumber = Integer.parseInt(CommonConfig.get("PLUGIN_SAVE_DIRECTORY_NUMBER").trim());

	@Override
	public int savePluginIntoDB(String pluginId, String pluginContent) {
		pluginContentDto contentDto = new pluginContentDto();
		contentDto.setPluginId(pluginId);
		contentDto.setPluginContent(pluginContent.getBytes());
		contentDto.setStatus(Constant.PLUGIN_DOWNLOAD_TYPE.SOTP_PLUGIN_DOWNlOAD_TYPE_WAIT);
		int ret = ErrorConstant.SQL_READMYSQL_ERR;
		try {
			ret = this.contentMapper.insert(contentDto);
		} catch (SQLException e) {
			logger.error("contentMapper insert error.msg:%s", e.toString());
			return ret;
		}
		return ret;
	}

	@Override
	public pluginContentDto getPluginContentByPluginId(String pluginId) {
		pluginContentDto contentDto = null;
		try {
			contentDto = this.contentMapper.selectByPluginId(pluginId);
		} catch (SQLException e) {
			logger.error("contentMapper selectByPluginId sql error" + e.toString());
			return contentDto;
		}
		return contentDto;
	}

	@Override
	public void updatePluginStatusById(Integer id, int status) {
		pluginContentDto contentDto = new pluginContentDto();
		contentDto.setId(id);
		contentDto.setStatus(status);
		try {
			this.contentMapper.updateByPrimaryKeySelective(contentDto);
		} catch (SQLException e) {
			logger.error("contentMapper insert error.msg:%s", e.toString());
		}

	}

	@Override
	public pluginContentDto getPluginContentAndUpdateStatus(String pluginId, int status) {
		pluginContentDto contentDto = null;
		try {
			contentDto = this.contentMapper.selectByPluginId(pluginId);
		} catch (SQLException e) {
			logger.error("contentMapper selectByPluginId sql error" + e.toString());
			return contentDto;
		}
		this.updatePluginStatusById(contentDto.getId(), status);
		return contentDto;
	}

	@Override
	public int savePluginIntoFile(String pluginId, String content) {
		int beginIndex = pluginId.length() > 6 ? (pluginId.length() - 3) : pluginId.length();
		int dirIndex = Integer.parseInt(pluginId.substring(beginIndex, pluginId.length())) % dirNumber;
		String fileName = PLUGIN_STRORE_FILE_DIR + dirIndex + File.separator + pluginId;
		CreateFileUtil.createFile(fileName);
		File pluginContent = new File(fileName);

		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(pluginContent, false));
			bw.write(content);
			bw.close();
		} catch (IOException e) {

			logger.error(e.getMessage());
			logger.error("save plugin content into file error! pluginId:%s", pluginId);
			return -1;
		}
		return 1;
	}

	@Override
	public String getPluginContentByPluginIdFromFile(String pluginId) {
		String content = null;
		int beginIndex = pluginId.length() > 6 ? (pluginId.length() - 3) : pluginId.length();
		int dirIndex = Integer.parseInt(pluginId.substring(beginIndex, pluginId.length())) % dirNumber;
		String fileName = PLUGIN_STRORE_FILE_DIR + dirIndex + File.separator + pluginId;
		File pluginContent = new File(fileName);
		try {
			BufferedReader br = new BufferedReader(new FileReader(pluginContent));
			content = br.readLine();
			br.close();
		} catch (FileNotFoundException e) {

			logger.error("save plugin content into file error! pluginId:%s", pluginId);
			return null;
		} catch (IOException e) {

			logger.error("save plugin content into file error! pluginId:%s", pluginId);
			return null;
		}
		return content;
	}

	public boolean renamePluginFile(String pluginId, String suffix) {
		int beginIndex = pluginId.length() > 6 ? (pluginId.length() - 3) : pluginId.length();
		int dirIndex = Integer.parseInt(pluginId.substring(beginIndex, pluginId.length())) % dirNumber;
		String srcFileName = PLUGIN_STRORE_FILE_DIR + dirIndex + File.separator + pluginId;

		SimpleDateFormat df = new SimpleDateFormat("_yyyyMMdd_");
		String date = df.format(new Date());
		String dstFileName = PLUGIN_STRORE_FILE_DIR + dirIndex + File.separator + pluginId + date + suffix;
		return CreateFileUtil.renameToNewFile(srcFileName, dstFileName);
	}

	@Override
	public void updatePluginInfoStatusById(String pluginId, int status) {
		UpdateUtil updateStatusInfo = new UpdateUtil();
		updateStatusInfo.addField(Constant.COL_NAME_OF_T_PLUGIN_INFO_TABLE.STATUS, status);
		updateStatusInfo.addAbsoluteKey(Constant.COL_NAME_OF_T_PLUGIN_INFO_TABLE.PLUGIN_ID, pluginId);
		Map<String, Object> updateStatusMap = updateStatusInfo.getUpdateInfomap();
		this.pluginInfoMapper.updateHashMapByPluginId(updateStatusMap);
	}

	public int getPluginInitStatus(String appId) {
		RulePolicyDto reqPolicyInfo = null;
		int pluginInitStatus = Constant.PLUGIN_STATUS.SOTP_PLUGIN_STATUS_READY;
		try {
			reqPolicyInfo = cacheService.getApplyPolicy("pluginapplypolicy", appId);
		} catch (BusinessException e) {
			String errMsg = "redis getApplyPolicy exception. msg:%s" + e.toString();
			logger.error(errMsg);
		}
		logger.debug("pluginInitStatus: " + reqPolicyInfo.getPluginInitStatus());
		if (null == reqPolicyInfo.getPluginInitStatus()) {
			return pluginInitStatus;
		}
		pluginInitStatus = reqPolicyInfo.getPluginInitStatus();
		return pluginInitStatus;
	}
}
