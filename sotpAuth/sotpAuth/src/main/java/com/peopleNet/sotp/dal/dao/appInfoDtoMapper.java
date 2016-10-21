package com.peopleNet.sotp.dal.dao;

import java.sql.SQLException;

import com.peopleNet.sotp.dal.model.appInfoDto;

public interface appInfoDtoMapper {
	appInfoDto selectByBusinessCode(appInfoDto app) throws SQLException;

	int updAppInfo(appInfoDto appInfoDto) throws SQLException;

	appInfoDto selectAppInfoByAppId(String appId) throws SQLException;

	String selectBusinessCodeByAppInfoId(int appInfoId) throws SQLException;

	String selectAppInfoCodeByAppInfoId(int appInfoId) throws SQLException;

}
