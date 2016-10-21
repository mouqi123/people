package com.peopleNet.sotp.dal.dao;

import java.sql.SQLException;

import org.apache.ibatis.annotations.Param;

import com.peopleNet.sotp.dal.model.pluginStatisticDto;

public interface pluginStatisticDtoMapper {

	int insert(pluginStatisticDto record) throws SQLException;

	pluginStatisticDto selectByType(Integer type) throws SQLException;

	pluginStatisticDto selectByPrimaryKey(Integer id) throws SQLException;

	int updateByPrimaryKeySelective(pluginStatisticDto record) throws SQLException;

	int update_AddTotalNumByType(@Param("type") Integer type, @Param("increTotalNum") int increNum) throws SQLException;

	int update_AddUsedNumByType(@Param("type") Integer type, @Param("increUsedNum") int increUsedNum)
	        throws SQLException;

	int update_IncreTotalAndUsedNumByType(@Param("type") Integer type) throws SQLException;

	int updateOrInsert_IncreTotalAndUsedNumByType(@Param("type") Integer type) throws SQLException;
}