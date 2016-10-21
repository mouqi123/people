package com.peopleNet.sotp.dal.dao;

import java.sql.SQLException;

import com.peopleNet.sotp.dal.model.pluginContentDto;

public interface pluginContentDtoMapper {
	int deleteByPrimaryKey(Integer id) throws SQLException;

	int insert(pluginContentDto record) throws SQLException;

	int insertSelective(pluginContentDto record) throws SQLException;

	pluginContentDto selectByPrimaryKey(Integer id) throws SQLException;

	int updateByPrimaryKeySelective(pluginContentDto record) throws SQLException;

	int updateByPrimaryKeyWithBLOBs(pluginContentDto record) throws SQLException;

	int updateByPrimaryKey(pluginContentDto record) throws SQLException;

	pluginContentDto selectByPluginId(String pluginId) throws SQLException;
}