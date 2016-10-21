package com.peopleNet.sotp.dal.dao;

import java.sql.SQLException;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.peopleNet.sotp.dal.model.authPolicyDto;

public interface authPolicyDtoMapper {
	authPolicyDto selectAuthPolicyDtoByAppId(@Param("appId") String appId, @Param("businessName") String businessName)
	        throws SQLException;

	List<authPolicyDto> selectAuthPolicyDtoListByAppId(@Param("appId") String appId) throws SQLException;
}
