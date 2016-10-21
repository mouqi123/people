package com.peopleNet.sotp.util;

import com.peopleNet.sotp.log.LogUtil;
import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;

/**
 * DOCUMENT ME!
 * 
 * @author $author$
 * @version $Revision$
 */
public class CommonConfig {
	private static LogUtil log = LogUtil.getLogger(CommonConfig.class);
    // 系统配置
	private static CompositeConfiguration commonConfiguration;
    // 风控中白名单配置
	private static CompositeConfiguration whiteListConfiguration;
    // 风控中黑名单配置
	private static CompositeConfiguration blackListConfiguration;

	static {
		try {
            // 加载系统配置文件,不自动重载[程序启动后不会改变]
			String path = URLDecoder.decode(
			        Thread.currentThread().getContextClassLoader().getResource("").getPath() + "/properties", "UTF-8");
            commonConfiguration = reload(path, false);

            // 加载系统配置文件,不自动重载[程序启动后不会改变]
            String whiteListPath = URLDecoder.decode(
                    Thread.currentThread().getContextClassLoader().getResource("").getPath() + "/whitelist", "UTF-8");
            whiteListConfiguration = reload( whiteListPath, true);

            // 加载系统配置文件,不自动重载[程序启动后不会改变]
            String blackListPath = URLDecoder.decode(
                    Thread.currentThread().getContextClassLoader().getResource("").getPath() + "/blacklist", "UTF-8");
            blackListConfiguration = reload(blackListPath, true);

		} catch (UnsupportedEncodingException e) {
			log.error("get property files path error!");
			e.printStackTrace();
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
	 * @param path
	 *            配置文件路径
	 * @param checkReload
	 *            是否自动重载
	 * @throws ConfigurationException
	 */
	public static CompositeConfiguration reload( String path, boolean checkReload) throws ConfigurationException {
        CompositeConfiguration configuration = new CompositeConfiguration();

		log.info("The root path is %s, checkReload:%s", path, String.valueOf(checkReload));

		File file = new File(path);
		File[] properites = file.listFiles();

        if (null == properites)
            return configuration;

		//Assert.notNull(properites);

		for (File propertyFile : properites) {
			if (propertyFile.getName().endsWith(".properties")) {
				log.info("Add the file %s", propertyFile.getAbsolutePath());

				PropertiesConfiguration propertiesConfiguration = new PropertiesConfiguration(propertyFile);
				propertiesConfiguration.setEncoding("UTF-8");

                if (checkReload) {
                    // 允许自动重载,默认重载时间间隔为5s
                    propertiesConfiguration.setReloadingStrategy(new FileChangedReloadingStrategy());
                }
				configuration.addConfiguration(propertiesConfiguration);
			}
		}
        return configuration;

	}

	/**
	 * 得到所有的配置文件
	 * 
	 * @return
	 */
	public static Map<String, String> getAllProperties() {
		Map<String, String> ret = new LinkedHashMap<String, String>();

		@SuppressWarnings("rawtypes")
		Iterator iter = commonConfiguration.getKeys();

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

			ret.put(key, commonConfiguration.getString(key));
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
		return commonConfiguration.getString(key);
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
		String value = commonConfiguration.getString(key);

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
		String value = commonConfiguration.getString(key);

		if (value == null) {
			return defaultValue;
		} else {
			return Long.parseLong(value.trim());
		}
	}

	public static boolean isAdmin(String username) {
		String admins = commonConfiguration.getString("admin");
		String array[] = admins.split(";");
		return Arrays.asList(array).contains(username);
	}

	public static boolean isDebugMode() {
		String debug = commonConfiguration.getString("debug");
		if (debug.equals("true") || debug.equals("on")) {
			return true;
		} else {
			return false;
		}
	}

	public static String getVelocityTemplateDir() {
		return getCurrentClassPath() + "/vm/";
	}

    /**
     * 取得白名单配置项的列表
     *
     * @param key
     *            白名单配置项的键
     *
     * @return 配置项的列表
     */
    public static List<String> getWhiteList(String key) {
        List<Object> whiteObjList = whiteListConfiguration.getList(key);
        List<String> whiteList = new ArrayList<>(whiteObjList.size());
        for (Object obj: whiteObjList) {
            whiteList.add(Objects.toString(obj));
        }
        return whiteList;
    }

    /**
     * 取得黑名单配置项的列表
     *
     * @param key
     *            黑名单配置项的键
     *
     * @return 配置项的列表
     */
    public static List<String> getBlackList(String key) {
        List<Object> blackObjList = blackListConfiguration.getList(key);
        List<String> blackList = new ArrayList<>(blackObjList.size());
        for (Object obj: blackObjList) {
            blackList.add(Objects.toString(obj));
        }
        return blackList;
    }
}
