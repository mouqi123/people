package com.peopleNet.sotp.dal.dao;

import java.sql.SQLException;

import com.peopleNet.sotp.dal.model.appVersionInfoDto;

public interface appVersionInfoDtoMapper {

	appVersionInfoDto selectAppVersionInfoByAppId(String appId) throws SQLException;

}
