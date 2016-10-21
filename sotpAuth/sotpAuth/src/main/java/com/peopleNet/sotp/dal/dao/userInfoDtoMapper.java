package com.peopleNet.sotp.dal.dao;

import java.sql.SQLException;

import org.apache.ibatis.annotations.Param;

import com.peopleNet.sotp.dal.model.userInfoDto;

public interface userInfoDtoMapper {

	int deleteByPrimaryKey(Integer id) throws SQLException;

	int insert(userInfoDto record) throws SQLException;

	int insertSelective(userInfoDto record) throws SQLException;

	userInfoDto selectByPrimaryKey(Integer id) throws SQLException;

	userInfoDto selectByUserId(String userId) throws SQLException;

	int updateByPrimaryKeySelective(userInfoDto record) throws SQLException;

	int updateByPrimaryKey(userInfoDto record) throws SQLException;

	int insertIfNotExist(@Param("userId") String userId, @Param("userPhone") String userPhone,
	        @Param("appId") String appId) throws SQLException;
}