package com.peopleNet.sotp.dal.dao;

import java.sql.SQLException;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.peopleNet.sotp.dal.model.policyDetailDto;

public interface RulePolicyDtoMapper {

	// 查找启用的策略ID
	int selectPolicyId(@Param("policyCode") String policyCode, @Param("appId") String appId) throws SQLException;

	// 根据策略ID查找策略详情
	List<policyDetailDto> selectByPolicyId(Integer policyId) throws SQLException;

	int selectAuthPolicyByAppId(@Param("appId") String appId, @Param("businessName") String businessName)
	        throws SQLException;

}