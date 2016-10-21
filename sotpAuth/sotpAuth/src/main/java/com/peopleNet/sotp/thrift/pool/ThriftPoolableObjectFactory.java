package com.peopleNet.sotp.thrift.pool;

import java.lang.reflect.Constructor;

import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import com.peopleNet.sotp.log.LogUtil;
import com.peopleNet.sotp.thrift.service.SotpService.Client;

public class ThriftPoolableObjectFactory implements PoolableObjectFactory {

	/** 日志记录器 */
	private static LogUtil logger = LogUtil.getLogger(ThriftPoolableObjectFactory.class);
	/** 服务的IP */
	private String serviceIP;
	/** 服务的端口 */
	private int servicePort;
	/** 超时设置 */
	private int timeOut;

	private String serviceInterface;

	public ThriftPoolableObjectFactory(String serviceIP, int servicePort, int timeOut, String serviceInterface) {
		super();
		this.serviceIP = serviceIP;
		this.servicePort = servicePort;
		this.timeOut = timeOut;
		this.serviceInterface = serviceInterface;
	}

	public String getServiceInterface() {
		return serviceInterface;
	}

	public void setServiceInterface(String serviceInterface) {
		this.serviceInterface = serviceInterface;
	}

	/**
	 * 激活对象
	 */
	@Override
	public void activateObject(Object arg0) throws Exception {

	}

	/**
	 * 销毁对象
	 */
	@Override
	public void destroyObject(Object arg0) throws Exception {
		logger.debug("start---invoke destroyObject");

		if (arg0 instanceof Client) {
			logger.debug("invoke destroyObject");
			Client client = (Client) arg0;
			if (client.getInputProtocol().getTransport().isOpen()) {
				client.getInputProtocol().getTransport().close();
			}
		}
		arg0 = null;
		logger.debug("end ---- invoke destroyObject");

	}

	/**
	 * 创建个性化对象
	 */
	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Object makeObject() throws Exception {
		try {
			TTransport transport = new TSocket(this.serviceIP, this.servicePort, this.timeOut);
			transport.open();
			logger.debug(Thread.currentThread().getId() + "invoke makeObject");

			TProtocol protocol = new TBinaryProtocol(transport);
			Class client = Class.forName(getServiceInterface() + "$Client");
			Constructor con = client.getConstructor(TProtocol.class);
			Object object = con.newInstance(protocol);
			return object;
		} catch (Exception e) {

			logger.error("error ThriftPoolableObjectFactory(), %s", e.toString());
			throw new RuntimeException(e);
		}
	}

	@Override
	public void passivateObject(Object arg0) throws Exception {
	}

	/**
	 * 检验对象是否可以由pool安全返回
	 */
	@Override
	public boolean validateObject(Object arg0) {
		logger.debug("start---invoke validateObject");

		try {
			logger.debug(Thread.currentThread().getId() + "invoke validateObject");
			if (arg0 instanceof Client) {

				Client client = (Client) arg0;

				logger.debug("inputProtocol is open [%s]",
				        client.getInputProtocol().getTransport().isOpen() ? "yes" : "no");
				logger.debug("outputProtocol is open [%s]",
				        client.getOutputProtocol().getTransport().isOpen() ? "yes" : "no");

				if (client.getInputProtocol().getTransport().isOpen()
				        && client.getOutputProtocol().getTransport().isOpen()) {
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		} catch (Exception e) {
			logger.error("invoke validateObject, %s", e.toString());
			return false;
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

	public int getTimeOut() {
		return timeOut;
	}

	public void setTimeOut(int timeOut) {
		this.timeOut = timeOut;
	}

}
