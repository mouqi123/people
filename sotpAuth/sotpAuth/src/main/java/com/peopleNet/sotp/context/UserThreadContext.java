package com.peopleNet.sotp.context;

public class UserThreadContext {
	private static ThreadLocal<Contextable> context = new ThreadLocal<Contextable>();

	public static Contextable getContext() {
		return context.get();
	}

	public static void setContext(Contextable serviceContext) {
		context.set(serviceContext);
	}
}
