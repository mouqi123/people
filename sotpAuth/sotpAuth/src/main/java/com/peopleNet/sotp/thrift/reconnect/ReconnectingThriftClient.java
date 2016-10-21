package com.peopleNet.sotp.thrift.reconnect;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.Set;

import org.apache.thrift.TServiceClient;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import com.peopleNet.sotp.log.LogUtil;

public final class ReconnectingThriftClient {
	private static LogUtil LOG = LogUtil.getLogger(ReconnectingThriftClient.class);
	/**
	 * List of causes which suggest a restart might fix things (defined as
	 * constants in {@link org.apache.thrift.transport.TTransportException}).
	 */
	private static Set<Integer> RESTARTABLE_CAUSES = new HashSet<Integer>();

	ReconnectingThriftClient() {
		RESTARTABLE_CAUSES.add(TTransportException.NOT_OPEN);
		RESTARTABLE_CAUSES.add(TTransportException.END_OF_FILE);
		RESTARTABLE_CAUSES.add(TTransportException.TIMED_OUT);
		RESTARTABLE_CAUSES.add(TTransportException.UNKNOWN);
	}

	/**
	 * Reflectively wraps a thrift client so that when a call fails due to a
	 * networking error, a reconnect is attempted.
	 *
	 * @param baseClient
	 *            the client to wrap
	 * @param clientInterface
	 *            the interface that the client implements (can be inferred by
	 *            using
	 *            {@link #wrap(org.apache.thrift.TServiceClient, com.rapleaf.spruce_lib.singletons.ReconnectingThriftClient.Options)}
	 * @param options
	 *            options that control behavior of the reconnecting client
	 * @param <T>
	 * @param <C>
	 * @return
	 */
	public static <T extends TServiceClient, C> C wrap(T baseClient, Class<C> clientInterface, Options options) {
		Object proxyObject = Proxy.newProxyInstance(clientInterface.getClassLoader(),
		        new Class<?>[] { clientInterface },
		        new ReconnectingClientProxy<T>(baseClient, options.getNumRetries(), options.getTimeBetweenRetries()));

		return (C) proxyObject;
	}

	/**
	 * Reflectively wraps a thrift client so that when a call fails due to a
	 * networking error, a reconnect is attempted.
	 *
	 * @param baseClient
	 *            the client to wrap
	 * @param options
	 *            options that control behavior of the reconnecting client
	 * @param <T>
	 * @param <C>
	 * @return
	 */
	public static <T extends TServiceClient, C> C wrap(T baseClient, Options options) {
		Class<?>[] interfaces = baseClient.getClass().getInterfaces();
		for (Class<?> iface : interfaces) {

			LOG.info(iface.getName());
			LOG.info(iface.getSimpleName());

			if (iface.getSimpleName().equals("Iface")
			        && iface.getEnclosingClass().equals(baseClient.getClass().getEnclosingClass())) {
				return (C) wrap(baseClient, iface, options);
			}
		}

		throw new RuntimeException("Class needs to implement Iface directly. Use wrap(TServiceClient, Class) instead.");
	}

	/**
	 * Reflectively wraps a thrift client so that when a call fails due to a
	 * networking error, a reconnect is attempted.
	 *
	 * @param baseClient
	 *            the client to wrap
	 * @param clientInterface
	 *            the interface that the client implements (can be inferred by
	 *            using
	 *            {@link #wrap(org.apache.thrift.TServiceClient, com.rapleaf.spruce_lib.singletons.ReconnectingThriftClient.Options)}
	 * @param <T>
	 * @param <C>
	 * @return
	 */
	public static <T extends TServiceClient, C> C wrap(T baseClient, Class<C> clientInterface) {
		return wrap(baseClient, clientInterface, Options.defaults());
	}

	/**
	 * Reflectively wraps a thrift client so that when a call fails due to a
	 * networking error, a reconnect is attempted.
	 *
	 * @param baseClient
	 *            the client to wrap
	 * @param <T>
	 * @param <C>
	 * @return
	 */
	public static <T extends TServiceClient, C> C wrap(T baseClient) {

		LOG.info(baseClient.getClass().getName());
		LOG.info(baseClient.getClass().getDeclaredMethods().toString());
		return wrap(baseClient, Options.defaults());
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
			return new Options(5, 10000L);
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

	/**
	 * Helper proxy class. Attempts to call method on proxy object wrapped in
	 * try/catch. If it fails, it attempts a reconnect and tries the method
	 * again.
	 *
	 * @param <T>
	 */
	private static class ReconnectingClientProxy<T extends TServiceClient> implements InvocationHandler {
		private final T baseClient;
		private final int maxRetries;
		private final long timeBetweenRetries;

		public ReconnectingClientProxy(T baseClient, int maxRetries, long timeBetweenRetries) {
			this.baseClient = baseClient;
			this.maxRetries = maxRetries;
			this.timeBetweenRetries = timeBetweenRetries;
		}

		private static void reconnectOrThrowException(TTransport transport, int maxRetries, long timeBetweenRetries)
		        throws TTransportException {
			int errors = 0;
			transport.close();

			while (errors < maxRetries) {
				try {

					LOG.info("Attempting to reconnect...");
					transport.open();
					LOG.info("Reconnection successful");
					break;
				} catch (TTransportException e) {

					LOG.error("Error while reconnecting:", e);
					errors++;

					if (errors < maxRetries) {
						try {
							LOG.info("Sleeping for {} milliseconds before retrying", timeBetweenRetries);
							Thread.sleep(timeBetweenRetries);
						} catch (InterruptedException e2) {
							throw new RuntimeException(e);
						}
					}
				}
			}

			if (errors >= maxRetries) {
				throw new TTransportException("Failed to reconnect");
			}
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			try {
				return method.invoke(baseClient, args);
			} catch (InvocationTargetException e) {
				if (e.getTargetException() instanceof TTransportException) {
					TTransportException cause = (TTransportException) e.getTargetException();

					if (RESTARTABLE_CAUSES.contains(cause.getType())) {
						reconnectOrThrowException(baseClient.getInputProtocol().getTransport(), maxRetries,
						        timeBetweenRetries);
						return method.invoke(baseClient, args);
					}
				}

				throw e;
			}
		}
	}
}