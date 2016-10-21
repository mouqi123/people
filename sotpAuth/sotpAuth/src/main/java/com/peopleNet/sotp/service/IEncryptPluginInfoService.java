package com.peopleNet.sotp.service;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.peopleNet.sotp.dal.model.pluginInfoDto;

/**
 * 
 * @描述 封装插件表相关操作，对敏感信息做处理
 * @author wangxin
 * @created_at 2016年01月20日
 */
public interface IEncryptPluginInfoService {
	int deleteByPrimaryKey(Integer id);

	int insert(pluginInfoDto record);

	int insertSelective(pluginInfoDto record);

	pluginInfoDto selectByPrimaryKey(Integer id);

	pluginInfoDto selectByPluginId(String pluginId);

	pluginInfoDto selectByPluginIdOptimize(@Param("random") int random, @Param("pluginId") String pluginId);

	List<pluginInfoDto> selectByUid(String uid);

	List<pluginInfoDto> selectByphoneNum(String phoneNum);

	int updateByPrimaryKeySelective(pluginInfoDto record);

	int updateByPrimaryKey(pluginInfoDto record);

	String selectByPluginTypeLimit1(Integer pType);

	String selectByPluginTypeAndStatus(@Param("ptype") Integer pType, @Param("state") int Status);

	String selectPluginKeyByPluginIdAndStatus(@Param("random") int random, @Param("pluginId") String pluginId,
	        @Param("status") int status);

	pluginInfoDto selectByDevIdAndPhoneNum(@Param("random") int random, @Param("devId") String devId,
	        @Param("phoneNum") String phoneNum);

	int update_IncTotalUsecnt(@Param("id") Integer id);

	int updateHashMapByPluginId(Map<String, Object> mapObj);

	int updatePluginInfoByHashMap(Map<String, Object> mapObj);
	
	int batchUpdatePluginInfo(List<String> list);
}
