package com.peopleNet.sotp.dal.dao;

import java.sql.SQLException;

import com.peopleNet.sotp.dal.model.winphoneStatisticDto;

public interface winphoneStatisticDtoMapper {

	int insert(winphoneStatisticDto record) throws SQLException;

	int insertSelective(winphoneStatisticDto record) throws SQLException;

	winphoneStatisticDto select() throws SQLException;

	winphoneStatisticDto selectByPrimaryKey() throws SQLException;

	int delete() throws SQLException;

	int deleteByPrimaryKey(Integer id) throws SQLException;

	int updateByPrimaryKeySelective(winphoneStatisticDto record) throws SQLException;
}