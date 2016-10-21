package com.peopleNet.sotp.service.impl;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.peopleNet.sotp.constant.ErrorConstant;
import com.peopleNet.sotp.dal.dao.pluginInfoDtoMapper;
import com.peopleNet.sotp.dal.model.pluginInfoDto;
import com.peopleNet.sotp.log.LogUtil;
import com.peopleNet.sotp.security.PeopleProtect;
import com.peopleNet.sotp.service.IEncryptPluginInfoService;

@Service
public class EncryptPluginInfoServiceImpl implements IEncryptPluginInfoService {

	private static LogUtil logger = LogUtil.getLogger(EncryptPluginInfoServiceImpl.class);
	@Autowired
	private pluginInfoDtoMapper pluginInfoMapper;

	@Override
	public int deleteByPrimaryKey(Integer id) {
		int ret = ErrorConstant.SQL_READMYSQL_ERR;
		try {
			ret = this.pluginInfoMapper.deleteByPrimaryKey(id);
		} catch (SQLException e) {
			logger.error("deleteByPrimaryKey error.msg:%s", e.toString());
			return ret;
		}
		return ret;
	}

	@Override
	public int insert(pluginInfoDto record) {
		int ret = ErrorConstant.SQL_READMYSQL_ERR;
		String holdInfo = record.getHoldInfo();
		if (null != holdInfo) {
			record.setHoldInfo(PeopleProtect.transform(holdInfo));
		}
		String protectCode = record.getProtectCode();
		if (null != protectCode) {
			record.setProtectCode(PeopleProtect.transform(protectCode));
		}
		try {
			ret = this.pluginInfoMapper.insert(record);
		} catch (SQLException e) {
			logger.error("pluginInfoMapper insert error.msg:%s", e.toString());
			return ret;
		}
		return ret;
	}

	@Override
	public int insertSelective(pluginInfoDto record) {
		int ret = ErrorConstant.SQL_READMYSQL_ERR;
		String holdInfo = record.getHoldInfo();
		if (null != holdInfo) {
			record.setHoldInfo(PeopleProtect.transform(holdInfo));
		}
		String protectCode = record.getProtectCode();
		if (null != protectCode) {
			record.setProtectCode(PeopleProtect.transform(protectCode));
		}
		try {
			ret = this.pluginInfoMapper.insertSelective(record);
		} catch (SQLException e) {
			logger.error("pluginInfoMapper insertSelective error.msg:%s", e.toString());
			return ret;
		}
		return ret;
	}

	@Override
	public pluginInfoDto selectByPrimaryKey(Integer id) {
		pluginInfoDto record = null;
		try {
			record = this.pluginInfoMapper.selectByPrimaryKey(id);
		} catch (SQLException e) {
			logger.error("selectByPrimaryKey sql error.msg:%s", e.toString());
			return record;
		}

		if (null == record)
			return null;

		String holdInfo = record.getHoldInfo();
		if (null != holdInfo) {
			record.setHoldInfo(PeopleProtect.revert(holdInfo));
		}
		String protectCode = record.getProtectCode();
		if (null != protectCode) {
			record.setProtectCode(PeopleProtect.revert(protectCode));
		}
		return record;
	}

	@Override
	public pluginInfoDto selectByPluginId(String pluginId) {
		pluginInfoDto record = null;
		try {
			record = this.pluginInfoMapper.selectByPluginId(pluginId);
		} catch (SQLException e) {
			logger.error("selectByPluginId sql error.msg:%s", e.toString());
			return record;
		}

		if (null == record)
			return null;

		String holdInfo = record.getHoldInfo();
		if (null != holdInfo) {
			record.setHoldInfo(PeopleProtect.revert(holdInfo));
		}
		String protectCode = record.getProtectCode();
		if (null != protectCode) {
			record.setProtectCode(PeopleProtect.revert(protectCode));
		}
		return record;
	}

	@Override
	public pluginInfoDto selectByPluginIdOptimize(int random, String pluginId) {
		pluginInfoDto record = null;
		try {
			record = this.pluginInfoMapper.selectByPluginIdOptimize(random, pluginId);
		} catch (SQLException e) {
			logger.error("selectByPluginIdOptimize sql error.msg:%s", e.toString());
			return record;
		}

		if (null == record)
			return null;

		String holdInfo = record.getHoldInfo();
		if (null != holdInfo) {
			record.setHoldInfo(PeopleProtect.revert(holdInfo));
		}
		String protectCode = record.getProtectCode();
		if (null != protectCode) {
			record.setProtectCode(PeopleProtect.revert(protectCode));
		}
		return record;
	}

	@Override
	public pluginInfoDto selectByDevIdAndPhoneNum(int random, String devId, String phoneNum) {
		pluginInfoDto record = null;
		try {
			record = this.pluginInfoMapper.selectByDevIdAndPhoneNum(random, devId, phoneNum);
		} catch (SQLException e) {
			logger.error("selectByDevIdAndPhoneNum sql error.msg:%s", e.toString());
			return record;
		}

		if (null == record)
			return null;

		String holdInfo = record.getHoldInfo();
		if (null != holdInfo) {
			record.setHoldInfo(PeopleProtect.revert(holdInfo));
		}
		String protectCode = record.getProtectCode();
		if (null != protectCode) {
			record.setProtectCode(PeopleProtect.revert(protectCode));
		}
		return record;
	}

	@Override
	public List<pluginInfoDto> selectByUid(String uid) {
		List<pluginInfoDto> pluginInfoList = null;
		try {
			pluginInfoList = this.pluginInfoMapper.selectByUid(uid);
		} catch (SQLException e) {
			logger.error("selectByUid sql error.msg:%s", e.toString());
			return pluginInfoList;
		}

		if (null == pluginInfoList) {
			return null;
		}

		for (pluginInfoDto record : pluginInfoList) {
			String holdInfo = record.getHoldInfo();
			if (null != holdInfo) {
				record.setHoldInfo(PeopleProtect.revert(holdInfo));
			}
			String protectCode = record.getProtectCode();
			if (null != protectCode) {
				record.setProtectCode(PeopleProtect.revert(protectCode));
			}
		}

		return pluginInfoList;
	}

	@Override
	public List<pluginInfoDto> selectByphoneNum(String phoneNum) {
		List<pluginInfoDto> pluginInfoList = null;
		try {
			pluginInfoList = this.pluginInfoMapper.selectByphoneNum(phoneNum);
		} catch (SQLException e) {
			logger.error("selectByphoneNum sql error.msg:%s", e.toString());
			return pluginInfoList;
		}

		if (null == pluginInfoList) {
			return null;
		}

		for (pluginInfoDto record : pluginInfoList) {
			String holdInfo = record.getHoldInfo();
			if (null != holdInfo) {
				record.setHoldInfo(PeopleProtect.revert(holdInfo));
			}
			String protectCode = record.getProtectCode();
			if (null != protectCode) {
				record.setProtectCode(PeopleProtect.revert(protectCode));
			}
		}

		return pluginInfoList;
	}

	@Override
	public int updateByPrimaryKeySelective(pluginInfoDto record) {
		int ret = ErrorConstant.SQL_READMYSQL_ERR;
		String holdInfo = record.getHoldInfo();
		if (null != holdInfo) {
			record.setHoldInfo(PeopleProtect.transform(holdInfo));
		}
		String protectCode = record.getProtectCode();
		if (null != protectCode) {
			record.setProtectCode(PeopleProtect.transform(protectCode));
		}
		try {
			ret = this.pluginInfoMapper.updateByPrimaryKeySelective(record);
		} catch (SQLException e) {
			logger.error("updateByPrimaryKeySelective sql error.msg:%s", e.toString());
			return ret;
		}
		return ret;
	}

	@Override
	public int updateByPrimaryKey(pluginInfoDto record) {
		int ret = ErrorConstant.SQL_READMYSQL_ERR;
		String holdInfo = record.getHoldInfo();
		if (null != holdInfo) {
			record.setHoldInfo(PeopleProtect.transform(holdInfo));
		}
		String protectCode = record.getProtectCode();
		if (null != protectCode) {
			record.setProtectCode(PeopleProtect.transform(protectCode));
		}
		try {
			ret = this.pluginInfoMapper.updateByPrimaryKey(record);
		} catch (SQLException e) {
			logger.error("updateByPrimaryKey sql error.msg:%s", e.toString());
			return ret;
		}
		return ret;
	}

	@Override
	public String selectByPluginTypeLimit1(Integer pType) {
		String ret = null;
		try {
			ret = this.pluginInfoMapper.selectByPluginTypeLimit1(pType);
		} catch (SQLException e) {
			logger.error("selectByPluginTypeLimit1 sql error.msg:%s", e.toString());
			return ret;
		}
		return ret;
	}

	@Override
	public String selectByPluginTypeAndStatus(Integer pType, int Status) {
		String ret = null;
		try {
			ret = this.pluginInfoMapper.selectByPluginTypeAndStatus(pType, Status);
		} catch (SQLException e) {
			logger.error("selectByPluginTypeAndStatus sql error.msg:%s", e.toString());
			return ret;
		}
		return ret;
	}

	@Override
	public String selectPluginKeyByPluginIdAndStatus(int random, String pluginId, int status) {
		String ret = null;
		try {
			ret = this.pluginInfoMapper.selectPluginKeyByPluginIdAndStatus(random, pluginId, status);
		} catch (SQLException e) {
			logger.error("selectPluginKeyByPluginIdAndStatus sql error.msg:%s", e.toString());
			return ret;
		}
		return ret;
	}

	@Override
	public int update_IncTotalUsecnt(Integer id) {
		int ret = ErrorConstant.SQL_READMYSQL_ERR;
		try {
			ret = this.pluginInfoMapper.update_IncTotalUsecnt(id);
		} catch (SQLException e) {
			logger.error("update_IncTotalUsecnt sql error.msg:%s", e.toString());
			return ret;
		}
		return ret;
	}

	@Override
	public int updateHashMapByPluginId(Map<String, Object> mapObj) {
		int ret = ErrorConstant.SQL_READMYSQL_ERR;
		try {
			ret = this.pluginInfoMapper.updateHashMapByPluginId(mapObj);
		} catch (SQLException e) {
			logger.error("updateHashMapByPluginId sql error.msg:%s", e.toString());
			return ret;
		}
		return ret;
	}

	@Override
	public int updatePluginInfoByHashMap(Map<String, Object> mapObj) {
		int ret = ErrorConstant.SQL_READMYSQL_ERR;
		try {
			ret = this.pluginInfoMapper.updatePluginInfoByHashMap(mapObj);
		} catch (SQLException e) {
			logger.error("updatePluginInfoByHashMap sql error.msg:%s", e.toString());
			return ret;
		}
		return ret;
	}

	@Override
    public int batchUpdatePluginInfo(List<String> list) {
		int ret = ErrorConstant.SQL_READMYSQL_ERR;
		try {
			ret = this.pluginInfoMapper.batchUpdatePluginInfo(list);
		} catch (SQLException e) {
			logger.error("updatePluginInfoByList sql error.msg:%s", e.toString());
			return ret;
		}
		return ret;
	}
}
