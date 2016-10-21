package com.peopleNet.sotp.dal.dao;

import java.sql.SQLException;

import com.peopleNet.sotp.dal.model.userVerifyInfoDto;

public interface userVerifyInfoDtoMapper {

	int insert(userVerifyInfoDto record) throws SQLException;

	int updateStatus(userVerifyInfoDto record) throws SQLException;

}
