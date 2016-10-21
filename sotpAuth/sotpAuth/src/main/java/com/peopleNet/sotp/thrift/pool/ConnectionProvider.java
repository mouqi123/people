package com.peopleNet.sotp.thrift.pool;

public interface ConnectionProvider {
	/**
	 * 取链接池中的一个链接
	 * 
	 * @return Client
	 */
	public Object getConnection();

	/**
	 * 返回链接
	 * 
	 * @param client
	 */
	public void returnCon(Object client);

	public String getServiceIP();

	public int getServicePort();

}
