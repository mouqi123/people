package com.peopleNet.sotp.thrift.pool;

import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import com.peopleNet.sotp.log.LogUtil;
import com.peopleNet.sotp.thrift.service.SotpService.Client;

public class ConnectionProviderImpl implements ConnectionProvider, InitializingBean, DisposableBean {
	private static LogUtil logger = LogUtil.getLogger(ConnectionProviderImpl.class);
	/** 服务端接口 */
	private String serviceInterface;

	/** 服务的IP地址 */
	private String serviceIP;
	/** 服务的端口 */
	private int servicePort;
	/** 连接超时配置 */
	private int conTimeOut;
	/** 可以从缓存池中分配对象的最大数量 */
	private int maxActive = GenericObjectPool.DEFAULT_MAX_ACTIVE;
	/** 缓存池中最大空闲对象数量 */
	private int maxIdle = GenericObjectPool.DEFAULT_MAX_IDLE;
	/** 缓存池中最小空闲对象数量 */
	private int minIdle = GenericObjectPool.DEFAULT_MIN_IDLE;
	/** 获取对象时阻塞的最长时间 */
	private long maxWait = GenericObjectPool.DEFAULT_MAX_WAIT;

	/** 从缓存池中分配对象，是否执行PoolableObjectFactory.validateObject方法 */
	private boolean testOnBorrow = GenericObjectPool.DEFAULT_TEST_ON_BORROW;
	private boolean testOnReturn = GenericObjectPool.DEFAULT_TEST_ON_RETURN;
	private boolean testWhileIdle = GenericObjectPool.DEFAULT_TEST_WHILE_IDLE;

	/** 对象缓存池 */
	private ObjectPool<Client> objectPool = null;

	@Override
	public Object getConnection() {

		try {
			logger.debug(Thread.currentThread().getId() + "pool active number:" + objectPool.getNumActive());
			logger.debug(Thread.currentThread().getId() + "pool idle number:" + objectPool.getNumIdle());
			// 从对象池取对象
			Object client = objectPool.borrowObject();
			logger.debug(Thread.currentThread().getId() + "pool active number:" + objectPool.getNumActive());
			logger.debug(Thread.currentThread().getId() + "pool idle number:" + objectPool.getNumIdle());
			logger.debug(Thread.currentThread().getId() + "invole getConnection, get a socket!");
			return client;
		} catch (Exception e) {

			logger.error(Thread.currentThread().getId() + "invoke getConnection, error! %s", e.toString());
			throw new RuntimeException("error getConnection()", e);
		}
	}

	@Override
	public void returnCon(Object client) {
		try {
			logger.debug("invoke returnCon!");
			logger.debug("pool active number:" + objectPool.getNumActive());
			logger.debug("pool idle number:" + objectPool.getNumIdle());
			// 将对象放回对象池
			if (null != client)
				objectPool.returnObject((Client) client);
			logger.debug("pool active number:" + objectPool.getNumActive());
			logger.debug("pool idle number:" + objectPool.getNumIdle());
		} catch (Exception e) {
			logger.error("invoke returnCon, error! %s", e.toString());
			throw new RuntimeException("error returnCon()", e);
		}
	}

	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	public void afterPropertiesSet() throws Exception {
		// 对象池
		objectPool = new GenericObjectPool<Client>();
		//
		((GenericObjectPool<Client>) objectPool).setMaxActive(maxActive);
		((GenericObjectPool<Client>) objectPool).setMaxIdle(maxIdle);
		((GenericObjectPool<Client>) objectPool).setMinIdle(minIdle);
		((GenericObjectPool<Client>) objectPool).setMaxWait(maxWait);
		((GenericObjectPool<Client>) objectPool).setTestOnBorrow(testOnBorrow);
		((GenericObjectPool<Client>) objectPool).setTestOnReturn(testOnReturn);
		((GenericObjectPool<Client>) objectPool).setTestWhileIdle(testWhileIdle);
		((GenericObjectPool<Client>) objectPool).setWhenExhaustedAction(GenericObjectPool.WHEN_EXHAUSTED_BLOCK);

		// 设置factory
		ThriftPoolableObjectFactory thriftPoolableObjectFactory = new ThriftPoolableObjectFactory(serviceIP,
		        servicePort, conTimeOut, serviceInterface);
		objectPool.setFactory(thriftPoolableObjectFactory);
		logger.debug("invoke afterPropertiesSet");
		logger.debug("pool active number:" + objectPool.getNumActive());
		logger.debug("pool idle number:" + objectPool.getNumIdle());
	}

	@Override
	public void destroy() throws Exception {
		try {
			logger.debug("invode destroy");
			logger.debug("------pool active number:" + objectPool.getNumActive());
			logger.debug("------pool idle number:" + objectPool.getNumIdle());
			objectPool.close();
			logger.debug("------pool active number:" + objectPool.getNumActive());
			logger.debug("------pool idle number:" + objectPool.getNumIdle());
		} catch (Exception e) {

			logger.error("invole destroy, error! %s", e.toString());
			throw new RuntimeException("erorr destroy()", e);
		}
	}

	public String getServiceIP() {
		return serviceIP;
	}

	public void setServiceIP(String serviceIP) {
		this.serviceIP = serviceIP;
	}

	public int getServicePort() {
		return servicePort;
	}

	public void setServicePort(int servicePort) {
		this.servicePort = servicePort;
	}

	public int getConTimeOut() {
		return conTimeOut;
	}

	public void setConTimeOut(int conTimeOut) {
		this.conTimeOut = conTimeOut;
	}

	public int getMaxActive() {
		return maxActive;
	}

	public void setMaxActive(int maxActive) {
		this.maxActive = maxActive;
	}

	public int getMaxIdle() {
		return maxIdle;
	}

	public void setMaxIdle(int maxIdle) {
		this.maxIdle = maxIdle;
	}

	public int getMinIdle() {
		return minIdle;
	}

	public void setMinIdle(int minIdle) {
		this.minIdle = minIdle;
	}

	public long getMaxWait() {
		return maxWait;
	}

	public void setMaxWait(long maxWait) {
		this.maxWait = maxWait;
	}

	public boolean isTestOnBorrow() {
		return testOnBorrow;
	}

	public void setTestOnBorrow(boolean testOnBorrow) {
		this.testOnBorrow = testOnBorrow;
	}

	public boolean isTestOnReturn() {
		return testOnReturn;
	}

	public void setTestOnReturn(boolean testOnReturn) {
		this.testOnReturn = testOnReturn;
	}

	public boolean isTestWhileIdle() {
		return testWhileIdle;
	}

	public void setTestWhileIdle(boolean testWhileIdle) {
		this.testWhileIdle = testWhileIdle;
	}

	public ObjectPool<Client> getObjectPool() {
		return objectPool;
	}

	public void setObjectPool(ObjectPool<Client> objectPool) {
		this.objectPool = objectPool;
	}

	public String getServiceInterface() {
		return serviceInterface;
	}

	public void setServiceInterface(String serviceInterface) {
		this.serviceInterface = serviceInterface;
	}

}
