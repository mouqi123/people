/**
 * Copyright (C) 2015 Baifendian Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.peopleNet.sotp.thrift.heartbeat;

import com.peopleNet.sotp.log.LogUtil;

/**
 * <p>
 * 
 */
public class HeartBeatTest {
	private static LogUtil logger = LogUtil.getLogger(HeartBeatTest.class);

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void main(String[] args) {

		DynamicHostSet dynamicHostSet = new DynamicHostSet();
		dynamicHostSet.addServerInstance(new ServerNode("192.168.1.202", 9091));
		dynamicHostSet.addServerInstance(new ServerNode("192.168.1.202", 9092));
		dynamicHostSet.addServerInstance(new ServerNode("192.168.1.100", 8081));
		HeartBeatManager manager = new HeartBeatManager(dynamicHostSet, 1000, 2000, 3, 1000, null);
		manager.startHeatbeatTimer();

		while (true) {
			System.out.println("lives:" + dynamicHostSet.getLives());
			System.out.println("deads:" + dynamicHostSet.getDeads());
			// System.out.println(manager.getDeads());
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		/*
		 * while(true) { String result =
		 * encryptTest("23142531","asdfa","123456"); logger.info(result); try {
		 * Thread.sleep(1000); } catch (InterruptedException e) {
		 * e.printStackTrace(); } }
		 */
	}
}
