package com.peopleNet.sotp.dal.dao;

import java.sql.SQLException;
import java.util.List;

import com.peopleNet.sotp.dal.model.verifyDataDto;

public interface verifyDataDtoMapper {

	List<verifyDataDto> getVerifyDataList(String appId) throws SQLException;
}
