package com.peopleNet.sotp.dal.dao;

import java.sql.SQLException;

import com.peopleNet.sotp.dal.model.pluginUpdatePolicyDto;

public interface pluginUpdatePolicyDtoMapper {

	int deleteByPrimaryKey(Integer id) throws SQLException;

	int insert(pluginUpdatePolicyDto record) throws SQLException;

	int insertSelective(pluginUpdatePolicyDto record) throws SQLException;

	pluginUpdatePolicyDto selectByPrimaryKey(Integer id) throws SQLException;

	int updateByPrimaryKeySelective(pluginUpdatePolicyDto record) throws SQLException;

	int updateByPrimaryKey(pluginUpdatePolicyDto record) throws SQLException;

	pluginUpdatePolicyDto selectByPolicyStatus(Integer status) throws SQLException;
}