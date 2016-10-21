package com.peopleNet.sotp.context;

/**
 * 服务的上下文接口
 *
 * @author wangxin
 */
public interface Contextable {

	public String getUserId();

	public String getUserIp();

	public String getUserPhone();

	public String getPluginId();

	public String getInterfaceName();

	public String getAppId();

	public String getVersion();

	public void setVersion(String version);

	public String getLocation();

	public String getWifi();

	public int getStatus();

	public void setStatus(int status);

	public String toString();

}
