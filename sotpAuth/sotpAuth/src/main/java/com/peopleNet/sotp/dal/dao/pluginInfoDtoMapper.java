package com.peopleNet.sotp.dal.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.peopleNet.sotp.dal.model.pluginInfoDto;

public interface pluginInfoDtoMapper {

	int deleteByPrimaryKey(Integer id) throws SQLException;

	int insert(pluginInfoDto record) throws SQLException;

	int insertSelective(pluginInfoDto record) throws SQLException;

	pluginInfoDto selectByPrimaryKey(Integer id) throws SQLException;

	pluginInfoDto selectByPluginId(String pluginId) throws SQLException;

	pluginInfoDto selectByPluginIdOptimize(@Param("random") int random, @Param("pluginId") String pluginId)
	        throws SQLException;

	List<pluginInfoDto> selectByUid(String uid) throws SQLException;

	List<pluginInfoDto> selectByphoneNum(String phoneNum) throws SQLException;

	int updateByPrimaryKeySelective(pluginInfoDto record) throws SQLException;

	int updateByPrimaryKey(pluginInfoDto record) throws SQLException;

	String selectByPluginTypeLimit1(Integer pType) throws SQLException;

	String selectByPluginTypeAndStatus(@Param("ptype") Integer pType, @Param("state") int Status) throws SQLException;

	String selectPluginKeyByPluginIdAndStatus(@Param("random") int random, @Param("pluginId") String pluginId,
	        @Param("status") int status) throws SQLException;

	pluginInfoDto selectByDevIdAndPhoneNum(@Param("random") int random, @Param("devId") String devId,
	        @Param("phoneNum") String phoneNum) throws SQLException;

	int update_IncTotalUsecnt(@Param("id") Integer id) throws SQLException;

	int updateHashMapByPluginId(Map<String, Object> mapObj) throws SQLException;

	int updatePluginInfoByHashMap(Map<String, Object> mapObj) throws SQLException;
	
	int batchUpdatePluginInfo(List<String> list) throws SQLException;
}
