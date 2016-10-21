package com.peopleNet.sotp.dal.dao;

import java.sql.SQLException;

import org.apache.ibatis.annotations.Param;

import com.peopleNet.sotp.dal.model.androidStatisticDto;

public interface androidStatisticDtoMapper {
	int insert(androidStatisticDto record) throws SQLException;

	int insertSelective(androidStatisticDto record) throws SQLException;

	androidStatisticDto select() throws SQLException;

	androidStatisticDto selectByPrimaryKey() throws SQLException;

	int delete() throws SQLException;

	int deleteByPrimaryKey(Integer id) throws SQLException;

	int updateByPrimaryKeySelective(androidStatisticDto record) throws SQLException;

	int update_AddTotalNum(@Param("increNum") int increNum) throws SQLException;
}