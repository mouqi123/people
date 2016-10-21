package com.peopleNet.sotp.thrift.service;

import com.peopleNet.sotp.util.CommonConfig;

public class PluginSaveHelper {

	private static int STATIC_SAVE_GENSOTP_RESULT = Integer
	        .parseInt(CommonConfig.get("STATIC_SAVE_GENSOTP_RESULT").trim());
	private static boolean availableResult[] = new boolean[STATIC_SAVE_GENSOTP_RESULT];

	static {
		for (int i = 0; i < STATIC_SAVE_GENSOTP_RESULT; i++) {
			availableResult[i] = true;
		}
	}

	public static boolean getAvailable() {
		long threadId = Thread.currentThread().getId();
		int resultIndex = (int) (threadId % STATIC_SAVE_GENSOTP_RESULT);
		boolean isAvail = false;
		synchronized (availableResult) {
			if (true == availableResult[resultIndex]) {
				isAvail = true;
				availableResult[resultIndex] = false;
			}
		}
		return isAvail;
	}

	public static void release() {
		long threadId = Thread.currentThread().getId();
		int resultIndex = (int) (threadId % STATIC_SAVE_GENSOTP_RESULT);
//		synchronized (availableResult) {
			availableResult[resultIndex] = true;
//		}
	}
}
