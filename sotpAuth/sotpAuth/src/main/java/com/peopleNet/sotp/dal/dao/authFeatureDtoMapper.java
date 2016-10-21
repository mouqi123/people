package com.peopleNet.sotp.dal.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.peopleNet.sotp.dal.model.authFeatureDto;

public interface authFeatureDtoMapper {
	int deleteByPrimaryKey(Integer id) throws SQLException;

	int insert(authFeatureDto record) throws SQLException;

	authFeatureDto selectByPrimaryKey(Integer id) throws SQLException;

	authFeatureDto selectByPluginId(String pluginId) throws SQLException;

	authFeatureDto selectByPluginIdOptimize(@Param("random") int random, @Param("pluginId") String pluginId)
	        throws SQLException;
	
	List<String> selectByUuidAndAppCode(Map<String,Object> params) throws SQLException;

	List<String> selectByPhoneNum(Map<String, Object> parms) throws SQLException;

	List<String> selectByPhoneNumAndStatus(Map<String, Object> parms) throws SQLException;

	List<Map<String, String>> selectByPhoneNumWithDevId(Map<String, Object> parms) throws SQLException;

	List<Map<String, String>> selectByPhoneNumAndStatusWithDevId(Map<String, Object> parms) throws SQLException;

	int updateDevNameByPluginId(@Param("devName") String devName, @Param("pluginId") String pluginId)
	        throws SQLException;
	int updateDevInfoByPluginId(@Param("dev_info") String devName, @Param("pluginId") String pluginId)
	        throws SQLException;
}
