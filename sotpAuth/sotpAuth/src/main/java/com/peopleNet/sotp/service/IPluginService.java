package com.peopleNet.sotp.service;

import com.peopleNet.sotp.dal.model.pluginContentDto;

/**
 * 
 * @描述 插件下载、保存等接口
 * @author wangxin
 * @created_at 2015年11月30日
 */
public interface IPluginService {

	public int savePluginIntoDB(String pluginId, String content);

	public pluginContentDto getPluginContentByPluginId(String pluginId);

	public pluginContentDto getPluginContentAndUpdateStatus(String pluginId, int status);

	public void updatePluginStatusById(Integer id, int status);

	public int savePluginIntoFile(String pluginId, String content);

	public String getPluginContentByPluginIdFromFile(String pluginId);

	public boolean renamePluginFile(String pluginId, String suffix);

	public void updatePluginInfoStatusById(String pluginId, int status);

	public int getPluginInitStatus(String appId);
}
