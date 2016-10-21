package com.peopleNet.sotp.dal.dao;

import java.sql.SQLException;
import java.util.List;

import com.peopleNet.sotp.dal.model.sotpVerifyPolicyDto;

public interface sotpVerifyPolicyDtoMapper {

	int deleteByPrimaryKey(Integer id) throws SQLException;

	int insert(sotpVerifyPolicyDto record) throws SQLException;

	int insertSelective(sotpVerifyPolicyDto record) throws SQLException;

	sotpVerifyPolicyDto selectByPrimaryKey(Integer id) throws SQLException;

	int updateByPrimaryKeySelective(sotpVerifyPolicyDto record) throws SQLException;

	int updateByPrimaryKey(sotpVerifyPolicyDto record) throws SQLException;

	List<sotpVerifyPolicyDto> selectByStatus(Integer status) throws SQLException;
}