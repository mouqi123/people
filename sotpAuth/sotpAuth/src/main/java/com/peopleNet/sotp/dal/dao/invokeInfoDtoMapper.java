package com.peopleNet.sotp.dal.dao;

import java.sql.SQLException;
import java.util.Map;

import com.peopleNet.sotp.dal.model.invokeInfoDto;

public interface invokeInfoDtoMapper {
	int insert(invokeInfoDto record) throws SQLException;

	int insertBatch(Map<String, Object> list) throws SQLException;
}