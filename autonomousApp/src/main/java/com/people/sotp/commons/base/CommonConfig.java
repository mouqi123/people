package com.people.sotp.commons.base;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;

/**
 * DOCUMENT ME!
 * 
 * @author $author$
 * @version $Revision$
 */
public class CommonConfig {
	// private static Logger log = LoggerFactory.getLogger(CommonConfig.class);
	private static Log log = LogFactory.getLog(CommonConfig.class);

	private static CompositeConfiguration configuration;

	static {
		try {
			reload();
		} catch (ConfigurationException e) {
			log.warn(e.toString());
		}
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public static String getCurrentClassPath() {
		String currentClassPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();

		try {
			return URLDecoder.decode(currentClassPath, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return currentClassPath;
		}
	}

	public static String getWebRootPath() {
		String currentClassPath = Thread.currentThread().getContextClassLoader().getResource("").getPath() + "/../../";

		try {
			return URLDecoder.decode(currentClassPath, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return currentClassPath;
		}
	}

	/**
	 * 载入配置 在配置文件夹下的所有配置文件都要重新加载
	 * 
	 * @throws ConfigurationException
	 */
	public static void reload() throws ConfigurationException {
		configuration = new CompositeConfiguration();

		try {
			String path = URLDecoder.decode(CommonConfig.class.getClassLoader().getResource("/properties").getPath(),
					"UTF-8");
			log.info("The root path is" + path);

			File file = new File(path);
			File[] properites = file.listFiles();

			Assert.notNull(properites);

			for (File propertyFile : properites) {
				if (propertyFile.getName().endsWith(".properties")) {
					log.info("Add the file {" + propertyFile.getAbsolutePath() + "}");

					PropertiesConfiguration propertiesConfiguration = new PropertiesConfiguration(propertyFile);
					propertiesConfiguration.setEncoding("UTF-8");
					configuration.addConfiguration(propertiesConfiguration);
				}
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 得到所有的配置文件
	 * 
	 * @return
	 */
	public static Map<String, String> getAllProperties() {
		Map<String, String> ret = new LinkedHashMap<String, String>();

		@SuppressWarnings("rawtypes")
		Iterator iter = configuration.getKeys();

		while (iter.hasNext()) {
			String key = (String) iter.next();

			if (key.startsWith("datasource.")) {
				continue;
			}

			if (key.startsWith("svn.")) {
				continue;
			}

			if (key.startsWith("hibernate.")) {
				continue;
			}

			if (key.startsWith("SQL.")) {
				continue;
			}

			if (key.contains("username")) {
				continue;
			}

			if (key.contains("password")) {
				continue;
			}

			ret.put(key, configuration.getString(key));
		}

		return ret;
	}

	/**
	 * 取得配置项的值
	 * 
	 * @param key
	 *            配置项的键
	 * 
	 * @return 配置项的值
	 */
	public static String get(String key) {
		return configuration.getString(key);
	}

	/**
	 * 取得配置项的值
	 * 
	 * @param key
	 *            配置项的键
	 * @param defaultValue
	 *            DOCUMENT ME!
	 * 
	 * @return 配置项的值
	 */
	public static int getInt(String key, int defaultValue) {
		String value = configuration.getString(key);

		if (value == null) {
			return defaultValue;
		} else {
			return Integer.parseInt(value.trim());
		}
	}

	/**
	 * 取得配置项的值
	 * 
	 * @param key
	 *            配置项的键
	 * @param defaultValue
	 *            DOCUMENT ME!
	 * 
	 * @return 配置项的值
	 */
	public static long getLong(String key, long defaultValue) {
		String value = configuration.getString(key);

		if (value == null) {
			return defaultValue;
		} else {
			return Long.parseLong(value.trim());
		}
	}

	public static boolean isAdmin(String username) {
		String admins = configuration.getString("admin");
		String array[] = admins.split(";");
		return Arrays.asList(array).contains(username);
	}

	public static boolean isDebugMode() {
		String debug = configuration.getString("debug");
		if (debug.equals("true") || debug.equals("on")) {
			return true;
		} else {
			return false;
		}
	}

	public static String getVelocityTemplateDir() {
		return getCurrentClassPath() + "/vm/";
	}

}
