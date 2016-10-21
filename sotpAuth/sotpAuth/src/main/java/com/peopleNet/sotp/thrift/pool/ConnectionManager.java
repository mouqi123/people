package com.peopleNet.sotp.thrift.pool;

import com.peopleNet.sotp.log.LogUtil;
import com.peopleNet.sotp.thrift.service.SotpService.Client;

public class ConnectionManager {

	/** 日志记录器 */
	private static LogUtil logger = LogUtil.getLogger(ConnectionManager.class);
	/** 连接提供池 */
	public ConnectionProvider connectionProvider;
	/** 保存local对象 */
	ThreadLocal<Client> socketThreadSafe = new ThreadLocal<Client>();

	/**
	 * 取socket
	 * 
	 * @return TSocket
	 */
	public Client getClient() {
		Client client = null;
		try {
			client = (Client) connectionProvider.getConnection();
			socketThreadSafe.set(client);
			logger.debug(Thread.currentThread().getId() + "invoke getSocket, and setInto ThreadLocal<TSocket>");
			return socketThreadSafe.get();
		} catch (Exception e) {
			if (null != client) {
				connectionProvider.returnCon(client);
				socketThreadSafe.remove();
			}

			logger.error(Thread.currentThread().getId() + "error ConnectionManager.invoke(), %s", e.toString());
		}

		return client;
	}

	public void retClient(Client client) {
		logger.debug("return client");
		connectionProvider.returnCon(client);
		socketThreadSafe.remove();
		logger.debug("return socket end");
	}

	public ConnectionProvider getConnectionProvider() {
		return connectionProvider;
	}

	public void setConnectionProvider(ConnectionProvider connectionProvider) {
		this.connectionProvider = connectionProvider;
	}

}
