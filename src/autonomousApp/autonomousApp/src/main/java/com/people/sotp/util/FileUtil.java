package com.people.sotp.util;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FileUtil {
	private static Log log = LogFactory.getLog(FileUtil.class);

	public static boolean createFile(String absolutePath) {
		int lastSlash = absolutePath.lastIndexOf("/");
		String parent = absolutePath.substring(0, lastSlash);
		String child = absolutePath.substring(lastSlash);

		File parentDir = new File(parent);
		if (!parentDir.exists() && !parentDir.mkdir()) {
			log.error("目录：" + parent + "不存在，并且无法创建目录");
			return false;
		}
		File file = new File(parent, child);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (Exception e) {
				log.error("文件:" + child + "不存在，并且无法创建此文件");
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}
}
