package com.peopleNet.sotp.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.thrift.TException;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import com.peopleNet.sotp.log.LogUtil;
import com.peopleNet.sotp.service.IThriftInvokeService;
import com.peopleNet.sotp.thrift.heartbeat.DynamicHostSet;
import com.peopleNet.sotp.thrift.heartbeat.HeartBeatManager;
import com.peopleNet.sotp.thrift.heartbeat.ServerNode;
import com.peopleNet.sotp.thrift.pool.ConnectionManager;
import com.peopleNet.sotp.thrift.service.PluginSaveHelper;
import com.peopleNet.sotp.thrift.service.SotpPlugin;
import com.peopleNet.sotp.thrift.service.SotpRet;
import com.peopleNet.sotp.thrift.service.SotpService;

@Service
public class ThriftInvokeServiceImpl implements IThriftInvokeService, InitializingBean {
	private static LogUtil log = LogUtil.getLogger(ThriftInvokeServiceImpl.class);
	/**
	 * List of causes which suggest a restart might fix things (defined as
	 * constants in {@link org.apache.thrift.transport.TTransportException}).
	 */
	private static Set<Integer> RESTARTABLE_CAUSES = new HashSet<Integer>();
	private List<ConnectionManager> downloadClientConnList;
	private List<ConnectionManager> businessClientConnList;
	private List<ServerNode> nodeList;
	private Set<ServerNode> nodeSet;
	/** 心跳频率，毫秒。默认0，不会执行心跳。 */
	private int heartbeatFrequency;
	/** 心跳执行的超时时间 */
	private int heartbeatTimeout;
	/** 心跳重试次数 */
	private int heartbeatTimes;
	/** 心跳重试间隔 */
	private int heartbeatInterval;
	private DynamicHostSet dynamicHostSet = new DynamicHostSet();

	public ThriftInvokeServiceImpl() {
		RESTARTABLE_CAUSES.add(TTransportException.NOT_OPEN);
		RESTARTABLE_CAUSES.add(TTransportException.END_OF_FILE);
		RESTARTABLE_CAUSES.add(TTransportException.TIMED_OUT);
		RESTARTABLE_CAUSES.add(TTransportException.UNKNOWN);
	}

	public List<ConnectionManager> getDownloadClientConnList() {
		return downloadClientConnList;
	}

	public void setDownloadClientConnList(List<ConnectionManager> downloadClientConnList) {
		this.downloadClientConnList = downloadClientConnList;
	}

	public List<ConnectionManager> getBusinessClientConnList() {
		return businessClientConnList;
	}

	public void setBusinessClientConnList(List<ConnectionManager> businessClientConnList) {
		this.businessClientConnList = businessClientConnList;
	}

	public Set<ServerNode> getNodeSet() {
		return nodeSet;
	}

	public void setNodeSet(Set<ServerNode> nodeSet) {
		this.nodeSet = nodeSet;
	}

	public List<ServerNode> getNodeList() {
		return nodeList;
	}

	public void setNodeList(List<ServerNode> nodeList) {
		this.nodeList = nodeList;
	}

	public int getHeartbeatFrequency() {
		return heartbeatFrequency;
	}

	public void setHeartbeatFrequency(int heartbeatFrequency) {
		this.heartbeatFrequency = heartbeatFrequency;
	}

	public int getHeartbeatTimeout() {
		return heartbeatTimeout;
	}

	public void setHeartbeatTimeout(int heartbeatTimeout) {
		this.heartbeatTimeout = heartbeatTimeout;
	}

	public int getHeartbeatTimes() {
		return heartbeatTimes;
	}

	public void setHeartbeatTimes(int heartbeatTimes) {
		this.heartbeatTimes = heartbeatTimes;
	}

	public int getHeartbeatInterval() {
		return heartbeatInterval;
	}

	public void setHeartbeatInterval(int heartbeatInterval) {
		this.heartbeatInterval = heartbeatInterval;
	}

	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	public void afterPropertiesSet() throws Exception {
		if (null == nodeList)
			return;
		for (ServerNode node : nodeList) {
			log.debug(node.toString());
			dynamicHostSet.addServerInstance(node);
		}
		HeartBeatManager manager = new HeartBeatManager(dynamicHostSet, heartbeatFrequency, heartbeatTimeout,
		        heartbeatTimes, heartbeatInterval, null);
		// 添加ShutdownHook
		addShutdownHook(manager);

		manager.startHeatbeatTimer();
	}

	/**
	 * 添加关闭钩子
	 * <p>
	 *
	 * @param heartBeatManager
	 */
	protected void addShutdownHook(final HeartBeatManager heartBeatManager) {
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() {
				if (heartBeatManager != null) {
					heartBeatManager.stopHeartbeatTimer();
				}
				log.debug("shutdown heartBeatManager!!!");
			}
		}));
	}

	/*
	 * 判断当前服务节点是否有效. 返回值: true -- 有效; false -- 无效.
	 */
	private boolean checkServerNode(ServerNode sNode) {
		Set<ServerNode> liveNodes = dynamicHostSet.getLives();
		if (null != liveNodes && liveNodes.contains(sNode)) {
			return true;
		} else {
			log.info(sNode + "is not valid now !");
			return false;
		}
	}

	/*
	 * isPositive:表示遍历顺序。true表示正序，false表示逆序。
	 */
	private clientResult getAvailableClientVarable(List<ConnectionManager> args, boolean isPositive) {
		Thread current = Thread.currentThread();
		clientResult result = new clientResult();
		SotpService.Client client = null;
		try {
			Random random = new Random();
			int randomInt = Math.abs(random.nextInt()) % args.size();
			int i = randomInt;
			if (!isPositive) {
				i = args.size() - randomInt - 1;
			}
			int tryNum = 0;
			while (tryNum < args.size()) {
				i = i % args.size();
				log.debug(current.getId() + " --- try to getClient ip:%s, port:%s ,poolId:%d",
				        args.get(i).getConnectionProvider().getServiceIP(),
				        args.get(i).getConnectionProvider().getServicePort(), i);

				// 验证当前服务节点是否有效.若无效,则跳过
				ServerNode node = new ServerNode(args.get(i).getConnectionProvider().getServiceIP(),
				        args.get(i).getConnectionProvider().getServicePort());
				if (!checkServerNode(node)) {
					tryNum++;
					i++;
					continue;
				}

				client = (SotpService.Client) (args.get(i).getClient());
				// client =
				// (SotpService.Client)ReconnectingThriftClient.wrap(args.get(i).getClient());
				if (null != client && client.getInputProtocol().getTransport().isOpen()) {

					log.debug(current.getId() + "use getClient ip:%s, port:%s,poolId:%d",
					        args.get(i).getConnectionProvider().getServiceIP(),
					        args.get(i).getConnectionProvider().getServicePort(), i);
					result.setClient(client);
					result.setIndex(i);
					return result;
				}

				tryNum++;
				i++;
			}
			result = reconnectVarable(Options.defaults().getNumRetries(), Options.defaults().getTimeBetweenRetries(),
			        args, isPositive);
		} catch (Exception e) {

			log.error("no available client now");
			return null;
		}
		return result;
	}

	private clientResult reconnectVarable(int maxRetries, long timeBetweenRetries, List<ConnectionManager> args,
	        boolean isPositive) throws TTransportException {
		int errors = 0;

		clientResult result = new clientResult();

		while (errors < maxRetries) {
			Random random = new Random();
			int randomInt = Math.abs(random.nextInt()) % args.size();
			int i = randomInt;
			if (!isPositive) {
				i = args.size() - randomInt - 1;
			}
			int tryNum = 0;
			while (tryNum < args.size()) {
				i = i % args.size();
				log.debug("Attempting to reconnect client. ip:%s,port:%s",
				        args.get(i).getConnectionProvider().getServiceIP(),
				        args.get(i).getConnectionProvider().getServicePort());

				// 验证当前服务节点是否有效.若无效,则跳过
				ServerNode node = new ServerNode(args.get(i).getConnectionProvider().getServiceIP(),
				        args.get(i).getConnectionProvider().getServicePort());
				if (!checkServerNode(node)) {
					tryNum++;
					i++;
					continue;
				}

				try {
					SotpService.Client client = (SotpService.Client) (args.get(i).getClient());
					TTransport transport = client.getInputProtocol().getTransport();
					if (null != transport) {
						// transport.close();
						// transport.open();
						if (transport.isOpen()) {

							log.info("Reconnection client successful,use client ip:%s,port:%s",
							        args.get(i).getConnectionProvider().getServiceIP(),
							        args.get(i).getConnectionProvider().getServicePort());

							result.setClient(client);
							result.setIndex(i);
							return result;
						}
					}
				} catch (Exception e) {
					tryNum++;
					i++;
					continue;
				}
			}

			errors++;

			log.info("reconnect time:%d", errors);

			if (errors < maxRetries) {
				try {
					log.info("Sleeping for %d milliseconds before retrying", timeBetweenRetries);
					Thread.sleep(timeBetweenRetries);
				} catch (InterruptedException e2) {
				}
			}
		}

		if (errors >= maxRetries) {

			log.error("Failed to reconnect");
		}
		return null;
	}

	public SotpRet encryptNew(String seed, String plain) throws TException {
		SotpService.Client client = null;
		int index = -1;

		for (int invokeTime = 1; invokeTime < 3; invokeTime++) {
			try {
				log.debug("invoke thrift encrypt %d times .[seed:%s][plain:%s]", invokeTime, seed, plain);

				clientResult clientResult = getAvailableClientVarable(businessClientConnList, true);
				if (null == clientResult) {
					return null;
				}
				client = clientResult.getClient();
				index = clientResult.getIndex();
				SotpRet sr = client.sotpEncrypt(seed, plain);
				businessClientConnList.get(index).retClient(client);
				return sr;
			} catch (TException e) {
				log.error("invoke thrift encrypt %d times error.msg:%s", invokeTime, e.toString());

				client.getInputProtocol().getTransport().close();
				businessClientConnList.get(index).retClient(client);
			}
		}
		return null;
	}

	public SotpRet decryptNew(String seed, String cipher) throws TException {
		SotpService.Client client = null;
		int index = -1;

		for (int invokeTime = 1; invokeTime < 3; invokeTime++) {
			try {
				log.debug("invoke thrift decrypt %d times .[seed:%s][cipher:%s]", invokeTime, seed, cipher);
				clientResult clientResult = getAvailableClientVarable(businessClientConnList, true);
				if (null == clientResult) {
					return null;
				}
				client = clientResult.getClient();
				index = clientResult.getIndex();
				SotpRet sd = client.sotpDecrypt(seed, cipher);
				businessClientConnList.get(index).retClient(client);
				return sd;
			} catch (TException e) {
				log.error("invoke thrift decrypt %d times error.msg:%s", invokeTime, e.toString());

				client.getInputProtocol().getTransport().close();
				businessClientConnList.get(index).retClient(client);
			}
		}
		return null;
	}

	public int verifyNew(int type, String seed, int time, int window, String pin, String challenge, String verifycode)
	        throws TException {
		SotpService.Client client = null;
		int index = -1;

		for (int invokeTime = 1; invokeTime < 3; invokeTime++) {
			try {
				log.debug(
				        "invoke thrift verify %d times .[type:%d][seed:%s][time:%d][window:%d][pin:%s][challenge:%s][verifycode:%s]",
				        invokeTime, type, seed == null ? "" : seed, time, window, pin == null ? "" : pin,
				        challenge == null ? "" : challenge, verifycode == null ? "" : verifycode);

				clientResult clientResult = getAvailableClientVarable(businessClientConnList, true);
				if (null == clientResult) {
					return -2;
				}
				client = clientResult.getClient();
				int ret = client.sotpVerify(type, seed, time, window, pin, challenge, verifycode);
				businessClientConnList.get(index).retClient(client);
				return ret;
			} catch (TException e) {
				log.error("invoke thrift verify %d times error.msg:%s", invokeTime, e.toString());
				client.getInputProtocol().getTransport().close();
				businessClientConnList.get(index).retClient(client);
			}
		}
		return -2;
	}

	public SotpPlugin genSotpNew(int type, String phone, String hw) throws TException {
		SotpService.Client client = null;
		int index = -1;

		Thread current = Thread.currentThread();

		for (int invokeTime = 1; invokeTime < 3; invokeTime++) {
			try {
				log.debug(current.getId() + "invoke thrift genSotp %d times .[type:%d][phone:%s][hw:%s]", invokeTime,
				        type, phone == null ? "" : phone, hw == null ? "" : hw);

				clientResult clientResult = getAvailableClientVarable(downloadClientConnList, true);
				if (null == clientResult) {
					return null;
				}
				client = clientResult.getClient();
				index = clientResult.getIndex();
				SotpPlugin sp = client.sotpGen(type, phone, hw);
				downloadClientConnList.get(index).retClient(client);
				return sp;
			} catch (TException e) {
				log.error(current.getId() + " %d times gen plugin error.[exception:%s][try once more!]", invokeTime,
				        e.toString());

				client.getInputProtocol().getTransport().close();
				downloadClientConnList.get(index).retClient(client);
			}
		}
		return null;
	}

	public SotpRet sotpEncryptV2(String sn, String seed, String plain) throws TException {
		SotpService.Client client = null;
		int index = -1;

		for (int invokeTime = 1; invokeTime < 3; invokeTime++) {
			try {
				log.debug("invoke thrift encrypt %d times .[sn:%s][seed:%s][plain:%s]", invokeTime, sn, seed, plain);

				clientResult clientResult = getAvailableClientVarable(businessClientConnList, true);
				if (null == clientResult) {
					return null;
				}
				client = clientResult.getClient();
				index = clientResult.getIndex();
				SotpRet sr = client.merchant_sotpEncrypt(sn, seed, plain);
				businessClientConnList.get(index).retClient(client);
				return sr;
			} catch (TException e) {
				log.error("invoke thrift encrypt %d times. error. %s", invokeTime, e.toString());

				client.getInputProtocol().getTransport().close();
				businessClientConnList.get(index).retClient(client);
			}
		}
		return null;
	}

	public SotpRet sotpDecryptV2(String sn, String seed, String cipher) throws TException {
		SotpService.Client client = null;
		int index = -1;
		for (int invokeTime = 1; invokeTime < 3; invokeTime++) {
			try {
				log.debug("invoke thrift decrypt %d times .[sn:%s][seed:%s][cipher:%s]", invokeTime, sn, seed, cipher);
				clientResult clientResult = getAvailableClientVarable(businessClientConnList, true);
				if (null == clientResult) {
					return null;
				}
				client = clientResult.getClient();
				index = clientResult.getIndex();
				SotpRet sd = client.merchant_sotpDecrypt(sn, seed, cipher);
				businessClientConnList.get(index).retClient(client);
				return sd;
			} catch (TException e) {
				log.error("invoke thrift decrypt %d times error.msg:%s", invokeTime, e.toString());

				client.getInputProtocol().getTransport().close();
				businessClientConnList.get(index).retClient(client);
			}
		}
		return null;
	}

	public int sotpVerifyV2(int type, String sn, String seed, int time, int window, String pin, String challenge,
	        String verifycode) throws TException {
		SotpService.Client client = null;
		int index = -1;
		for (int invokeTime = 1; invokeTime < 3; invokeTime++) {
			try {
				log.debug(
				        "invoke thrift verify %d times .[type:%d][sn:%s][seed:%s][time:%d][window:%d][pin:%s][challenge:%s][verifycode:%s]",
				        invokeTime, type, sn == null ? "" : sn, seed == null ? "" : seed, time, window,
				        pin == null ? "" : pin, challenge == null ? "" : challenge,
				        verifycode == null ? "" : verifycode);

				clientResult clientResult = getAvailableClientVarable(businessClientConnList, true);
				if (null == clientResult) {
					return -2;
				}
				client = clientResult.getClient();
				index = clientResult.getIndex();
				int ret = client.merchant_sotpVerify(type, sn, seed, time, window, pin, challenge, verifycode);
				businessClientConnList.get(index).retClient(client);
				return ret;
			} catch (TException e) {
				log.error("invoke thrift verify %d times error.msg:%s", invokeTime, e.toString());

				client.getInputProtocol().getTransport().close();
				businessClientConnList.get(index).retClient(client);
			}
		}
		return -2;
	}

	public SotpPlugin genSotpV2(int type, String merchant_sn, String merchant_seed, String appSign, String hw)
	        throws TException {
		SotpService.Client client = null;
		int index = -1;

		Thread current = Thread.currentThread();
		for (int invokeTime = 1; invokeTime < 3; invokeTime++) {
			try {
				log.debug(
				        current.getId()
				                + "invoke thrift genSotp %d times .[type:%d][merchant_sn:%s][merchant_seed:%s][appSign:%s][hw:%s]",
				        invokeTime, type, merchant_sn == null ? "" : merchant_sn,
				        merchant_seed == null ? "" : merchant_seed, appSign == null ? "" : appSign,
				        hw == null ? "" : hw);

				clientResult clientResult = getAvailableClientVarable(downloadClientConnList, true);
				if (null == clientResult) {
					return null;
				}
				client = clientResult.getClient();
				index = clientResult.getIndex();
				SotpPlugin sp = client.merchant_sotpGen(type, merchant_sn, merchant_seed, appSign, hw);
				downloadClientConnList.get(index).retClient(client);
				return sp;
			} catch (TException e) {
				log.error(current.getId() + " %d times gen plugin error.[exception:%s] [try once more!]", invokeTime,
				        e.toString());

				log.debug("start --- retClient");
				client.getInputProtocol().getTransport().close();
				downloadClientConnList.get(index).retClient(client);
				log.debug("end --- retClient");

				// 释放该线程保存插件的缓存区
				PluginSaveHelper.release();
			}
		}
		return null;
	}

	public SotpRet shareKey(int type, String sharekey) throws TException {
		try {
			log.debug("invoke thrift encrypt.[type:]" + type + "[sharekey]" + sharekey);

			clientResult clientResult = getAvailableClientVarable(businessClientConnList, true);
			if (null == clientResult) {
				return null;
			}
			SotpService.Client client = clientResult.getClient();
			int index = clientResult.getIndex();
			SotpRet sr = client.shareKey(type, sharekey);
			businessClientConnList.get(index).retClient(client);
			return sr;
		} catch (TTransportException e) {

			log.error("invoke thrift encrypt error.msg:%s,  %s", e.toString(), e.getMessage());
			if (RESTARTABLE_CAUSES.contains(e.getType())) {
				clientResult clientResult = getAvailableClientVarable(businessClientConnList, false);
				if (null == clientResult) {
					return null;
				}
				SotpService.Client client = clientResult.getClient();
				int index = clientResult.getIndex();
				SotpRet sr = client.shareKey(type, sharekey);
				businessClientConnList.get(index).retClient(client);
				return sr;
			}
		} catch (TException e) {

			log.error("invoke thrift encrypt error.msg:%s", e.toString());
			return null;
		}
		return null;
	}

	public SotpRet transEncry(int type, String seed, String data) throws TException {
		try {
			log.debug("invoke thrift encrypt.[type:]" + type + "[seed:]" + seed + "[data:]" + data);

			clientResult clientResult = getAvailableClientVarable(businessClientConnList, true);
			if (null == clientResult) {
				return null;
			}
			SotpService.Client client = clientResult.getClient();
			int index = clientResult.getIndex();
			SotpRet sr = client.transEncry(type, seed, data);
			businessClientConnList.get(index).retClient(client);
			return sr;
		} catch (TTransportException e) {

			log.error("invoke thrift encrypt error.msg:%s,  %s", e.toString(), e.getMessage());
			if (RESTARTABLE_CAUSES.contains(e.getType())) {
				clientResult clientResult = getAvailableClientVarable(businessClientConnList, false);
				if (null == clientResult) {
					return null;
				}
				SotpService.Client client = clientResult.getClient();
				int index = clientResult.getIndex();
				SotpRet sr = client.transEncry(type, seed, data);
				businessClientConnList.get(index).retClient(client);
				return sr;
			}
		} catch (TException e) {

			log.error("invoke thrift encrypt error.msg:%s", e.toString());
			return null;
		}
		return null;
	}

	public static class Options {
		private int numRetries;
		private long timeBetweenRetries;

		/**
		 *
		 * @param numRetries
		 *            the maximum number of times to try reconnecting before
		 *            giving up and throwing an exception
		 * @param timeBetweenRetries
		 *            the number of milliseconds to wait in between reconnection
		 *            attempts.
		 */
		public Options(int numRetries, long timeBetweenRetries) {
			this.numRetries = numRetries;
			this.timeBetweenRetries = timeBetweenRetries;
		}

		public static Options defaults() {
			return new Options(3, 1000L);
		}

		private int getNumRetries() {
			return numRetries;
		}

		private long getTimeBetweenRetries() {
			return timeBetweenRetries;
		}

		public Options withNumRetries(int numRetries) {
			this.numRetries = numRetries;
			return this;
		}

		public Options withTimeBetweenRetries(long timeBetweenRetries) {
			this.timeBetweenRetries = timeBetweenRetries;
			return this;
		}
	}

	private class clientResult {
		private SotpService.Client client;
		private int index;

		public SotpService.Client getClient() {
			return client;
		}

		public void setClient(SotpService.Client client) {
			this.client = client;
		}

		public int getIndex() {
			return index;
		}

		public void setIndex(int index) {
			this.index = index;
		}
	}

}
