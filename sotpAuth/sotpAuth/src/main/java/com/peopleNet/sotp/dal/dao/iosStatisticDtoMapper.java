package com.peopleNet.sotp.dal.dao;

import java.sql.SQLException;

import com.peopleNet.sotp.dal.model.iosStatisticDto;

public interface iosStatisticDtoMapper {
	int insert(iosStatisticDto record) throws SQLException;

	int insertSelective(iosStatisticDto record) throws SQLException;

	iosStatisticDto select() throws SQLException;

	iosStatisticDto selectByPrimaryKey() throws SQLException;

	int delete() throws SQLException;

	int deleteByPrimaryKey(Integer id) throws SQLException;

	int updateByPrimaryKeySelective(iosStatisticDto record) throws SQLException;
}