package com.people.sotp.commons.util;

import java.io.IOException;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.beanutils.BeanUtils;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 类型转换类
 * 
 * @author Administrator
 * 
 */
public class Convert {
	/**
	 * 实体类转换为Map
	 * 
	 * @param entites
	 * @return
	 */
	public static Map<String, Object> entitiesToMap(Object entites) {
		Map<String, Object> map = new HashMap<String, Object>();
		if (entites != null) {
			Field[] f = entites.getClass().getDeclaredFields();
			for (int i = 0; i < f.length; i++) {
				f[i].setAccessible(true);// 设置允许访问
				try {
					Object _obj = f[i].get(entites);
					if (_obj != null)
						map.put(f[i].getName(), _obj);
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
		return map;
	}

	/**
	 * Map转换为实体类
	 * 
	 * @param entites
	 * @return
	 */
	public static void transMap2Bean2(Map<String, Object> map, Object obj) {
		if (map == null || obj == null) {
			return;
		}
		try {
			BeanUtils.populate(obj, map);
		} catch (Exception e) {
			System.out.println("transMap2Bean2 Error " + e);
		}
	}

	/**
	 * 将List数据转换成json格式(支持泛型为Map，pojo类)
	 * 
	 * @param list
	 * @return json格式字符串
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonGenerationException
	 */
	public static String toJson(List<?> list) {
		ObjectMapper mapper = new ObjectMapper();
		String result = "";
		try {
			result = mapper.writeValueAsString(list);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 将Map数据转换成json格式
	 * 
	 * @param map
	 * @return json格式字符串
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonGenerationException
	 */
	public static String toJson(Map<String, Object> map) {
		ObjectMapper mapper = new ObjectMapper();
		String result = "";
		try {
			result = mapper.writeValueAsString(map).replaceAll("null", "\"\"");
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> parseJSON2Map(String jsonStr) {
		Map<String, Object> map = new HashMap<String, Object>();
		// 最外层解析
		JSONObject json = JSONObject.fromObject(jsonStr);
		for (Object k : json.keySet()) {
			Object v = json.get(k);
			// 如果内层还是数组的话，继续解析
			if (v instanceof JSONArray) {
				List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
				Iterator<JSONObject> it = ((JSONArray) v).iterator();
				while (it.hasNext()) {
					JSONObject json2 = it.next();
					list.add(parseJSON2Map(json2.toString()));
				}
				map.put(k.toString(), list);
			} else {
				map.put(k.toString(), v);
			}
		}
		return map;
	}

	/**
	 * 将pojo转换成json格式
	 * 
	 * @param pojo对象
	 * @return json格式字符串
	 */
	public static String toJson(Object obj) {
		return JSONObject.fromObject(obj).toString();
	}

	/**
	 * list转换为xml
	 * 
	 * @param list泛型支持map和pojo
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static String toXML(List<?> list) {
		StringBuffer sb = new StringBuffer();
		if (list != null && list.size() > 0) {
			sb.append("<root>");
			for (int i = 0; i < list.size(); i++) {
				Object row = list.get(i);
				if (row instanceof Map)
					sb.append(toXML((Map<Object, Object>) row));
				else
					sb.append(toXML(row));
			}
			sb.append("</root>");
		}
		return sb.toString();

	}

	/**
	 * map转换为xml
	 * 
	 * @param map
	 * @return
	 */
	public static String toXML(Map<Object, Object> map) {
		StringBuffer sb = new StringBuffer();
		if (map != null && !map.isEmpty()) {
			sb.append("<node>");
			Object[] keyArr = map.keySet().toArray();
			for (int i = 0; i < keyArr.length; i++) {
				Object _key = keyArr[i];
				Object _value = map.get(_key);
				sb.append("<" + _key + ">");
				sb.append(_value == null ? "" : _value);
				sb.append("</" + _key + ">");
			}
			sb.append("</node>");
		}
		return sb.toString();
	}

	/**
	 * map转换为xml
	 * 
	 * @param map
	 * @return
	 */
	public static String toXML(Map<Object, Object> map, String type) throws Exception {
		StringBuffer sb = new StringBuffer();
		if (map != null && !map.isEmpty()) {
			sb.append("<Content><Item>");

			/*
			 * sb.append("<TypeID>"); Field f =
			 * Constants.class.getField("TYPE_"+type.toUpperCase()); Integer
			 * iTemp = (Integer)f.get(Constants.class);
			 * sb.append(Integer.toString(iTemp)); sb.append("</TypeID>");
			 */

			sb.append("<").append(type).append(">");
			Object[] keyArr = map.keySet().toArray();
			for (int i = 0; i < keyArr.length; i++) {
				Object _key = keyArr[i];
				Object _value = map.get(_key);
				sb.append("<" + _key + ">");
				sb.append(_value == null ? "" : _value);
				sb.append("</" + _key + ">");
			}
			sb.append("</").append(type).append(">");
			sb.append("</Item></Content>");
		}
		return sb.toString();
	}

	/**
	 * pojo转换为xml
	 * 
	 * @param obj
	 * @return
	 */
	public static String toXML(Object obj) {
		StringBuffer sb = new StringBuffer();
		if (obj != null) {
			Field[] f = obj.getClass().getDeclaredFields();
			sb.append("<node>");
			for (int i = 0; i < f.length; i++) {
				f[i].setAccessible(true);// 设置允许访问
				try {
					Object _obj = f[i].get(obj);
					if (!(_obj instanceof Logger)) {
						sb.append("<" + f[i].getName() + ">");
						sb.append((_obj == null) ? "" : _obj);
						sb.append("</" + f[i].getName() + ">");
					}
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}

			}
			sb.append("</node>");
		}
		return sb.toString();
	}

	public static Map<Object, Object> analysisMap(Map<Object, Object> map) {
		Map<Object, Object> mResult = new HashMap<Object, Object>();
		Object[] keys = map.keySet().toArray();
		Map<Object, Object> mPMap = new HashMap<Object, Object>();
		for (Object key : keys) {
			String sKey = (String) key;
			Object oValue = map.get(key);
			if (sKey.indexOf(".") >= 0) {
				mPMap.put(sKey.substring(sKey.indexOf(".") + 1), oValue);
				mResult.put((sKey.split("\\."))[0], analysisMap(mPMap));
			} else
				mResult.put(key, oValue);
		}
		return mResult;
	}

	/**
	 * String数字数组转Integer数字数组
	 * 
	 * @param numStr
	 * @return
	 */
	public static Integer[] convertToIntegerArr(String[] numStr) {
		Integer[] numInt = new Integer[numStr.length];
		for (int i = 0; i < numStr.length; i++) {
			numInt[i] = Integer.parseInt(numStr[i]);
		}
		return numInt;
	}

	public static String clearString(String str) {
		return str.replace('\r', ' ').replace('\n', ' ').replace('\\', '/');
	}

	public static String strategyCondition(String condition) {
		/**
		 * 解析公共条件时间模式外的部分 格式为： elementid;elementValue#elementid;elementValue
		 * 
		 */

		StringBuffer sb = new StringBuffer();
		if ("".equals(condition.trim()) || condition == null) {
			sb.append("");
		} else {
			condition = condition.trim();
			String[] splitRoot = condition.split("@");

			if (!condition.substring(0, 1).equals("@")) {
				String[] splitPublic = splitRoot[0].split("#");
				for (int i = 0; i < splitPublic.length; i++) {
					String[] k = splitPublic[i].split(";");
					if (!"".equals(splitPublic[i].substring(splitPublic[i].length() - 1, splitPublic[i].length()))) {
						// sb.append("<" + k[0]+">" + k[1] + "</" + k[0] + ">");
						/**
						 * @author majie 修改后代码
						 */
						if (!k[0].equals("CLT_WORKMODENAME")) {
							sb.append("<" + k[0] + ">" + k[1] + "</" + k[0] + ">");
						} else {
							if (k.length != 1) {
								sb.append("<" + k[0] + ">" + k[1] + "</" + k[0] + ">");
							}
						}
					}
				}
			}

			/**
			 * 解析公共条件时间格式 格式为：
			 * startTimeValue;endTimeValue#startTimeValue;endTimeValue
			 * 
			 */
			if (condition.substring(condition.length() - 1, condition.length()) != "@") {
				String[] operTime = splitRoot[1].split("#");
				sb.append("<CLT_OPERATETIME>");
				for (int i = 0; i < operTime.length; i++) {
					if (!"".equals(operTime[i].substring(operTime[i].length() - 1, operTime[i].length()))) {
						String[] k1 = operTime[i].split(";");
						sb.append("<STR_START_TIME>" + k1[0] + "</STR_START_TIME>" + "<STR_END_TIME>" + k1[1]
								+ "</STR_END_TIME>");

					}
				}
				sb.append("</CLT_OPERATETIME>");
			}
		}
		return sb.toString();
	}

	public static String targetcondition(String xmlString) {
		StringBuffer stringBuffer = new StringBuffer();
		String[] xml = null;
		String[] xmlTemp = null;
		if ("".equals(xmlString.trim()) || xmlString == null) {
			stringBuffer.append("");
		} else {
			xml = xmlString.split("@@");
			int len = xml.length;
			for (int i = 0; i < len; i++) {
				xmlTemp = xml[i].split("#");
				if ("".equals(xmlTemp[0].trim()) || xmlTemp[0] == null) {
				} else {

					if (xml[i].substring(xml[i].indexOf("#"), xml[i].length()).equals("#")
							|| "".equals(xml[i].substring(xml[i].indexOf("#"), xml[i].length()).trim())) {

					} else {
						// ---
						if ("".equals(xmlTemp[1].trim()) || xmlTemp[1] == null) {
						} else {
							String[] splitRex = xmlTemp[1].split(";");
							if (splitRex.length == 1) {
								stringBuffer.append("<" + xmlTemp[0] + ">" + xmlTemp[1] + "</" + xmlTemp[0] + ">");
							} else {
								for (int j = 0; j < splitRex.length; j++) {
									stringBuffer.append("<" + xmlTemp[0] + ">" + splitRex[j] + "</" + xmlTemp[0] + ">");
								}

							}

						}
					}
				}
			}

		}
		return stringBuffer.toString();
	}

	public static String convertDateToString() {
		DateFormat format = new SimpleDateFormat("yyyy_MM_dd");
		String date = format.format(new Date());
		return date;
	}

	/**
	 * list的join方法
	 * 
	 * @param list
	 * @param str
	 * @return
	 */
	public static String join(List<?> list, String str) {
		StringBuffer result = new StringBuffer();
		if (list != null) {
			int i = 0;
			for (Object object : list) {
				result.append(object);
				result.append((list.size() - 1 == i) ? "" : str);
				i++;
			}
		}

		return result.toString();
	}

	/**
	 * 字符串解析
	 * 
	 * @param str
	 * @param strsplitfirst
	 * @param strsplitsec
	 */
	public static List<Object> strconvert(String str, String strsplitfirst, String strsplitsec) {
		List<Object> list = new ArrayList<Object>();
		String[] str1;
		String[] str2;
		if (!"".equals(str) && str != null) {
			if (!"".equals(strsplitsec) && strsplitsec != null && !"".equals(strsplitfirst) && strsplitfirst != null) {// 先安第一个拆分
																														// 再安第二个拆分
				str1 = str.split(strsplitfirst);
				if (str1 != null && str1.length > 0) {
					for (int i = 0; i < str1.length; i++) {
						str2 = str1[i].split(strsplitsec);
						if (!"".equals(str2) && str2.length > 0) {
							HashMap<Object, Object> map = new HashMap<Object, Object>();
							map.put("type", str2[0]);
							map.put("value", str2[1]);
							list.add(map);
						}
					}
				}
			}
		}
		return list;
	}

	/**
	 * 数组去空 包括null
	 * 
	 * @param num
	 * @return
	 */
	public static Object[] removeArrayNull(Object num[]) {
		Object[] obj = null;
		List<Object> list = new ArrayList<Object>();
		if (!"".equals(num) && num != null) {
			for (int i = 0; i < num.length; i++) {
				if (num[i] == null || "".equals(num[i].toString()) || num[i].equals("null")) {
					continue;
				} else {
					list.add(num[i]);
				}
			}
			if (!"".equals(list) && list != null) {
				obj = new Object[list.size()];
				for (int j = 0; j < list.size(); j++) {
					obj[j] = list.get(j);
				}
			}
		}
		return obj;
	}

	/**
	 * 数组去重
	 * 
	 * @param num
	 * @return
	 */
	public static Object[] getDistinct(Object num[]) {
		Object[] obj = null;
		List<Object> list = new ArrayList<Object>();
		if (!"".equals(num) && num != null) {
			for (int i = 0; i < num.length; i++) {
				if (!list.contains(num[i])) {// 如果list数组不包括num[i]中的值的话，就返回true。
					list.add(num[i]); // 在list数组中加入num[i]的值。已经过滤过。
				}
			}
		}
		obj = list.toArray(new Object[1]);
		// toArray（数组）方法返回数组。并要指定Integer类型。new
		// integer[o]的空间大小不用考虑。因为如果list中的长度大于0（你integer的长度），toArray方法会分配一个具有指定数组的运行时类型和此列表大小的新数组。
		return obj;
	}

	// 求两个数组的交集
	public static Object[] intersect(Object[] arr1, Object[] arr2) {
		Map<Object, Boolean> map = new HashMap<Object, Boolean>();
		LinkedList<Object> list = new LinkedList<Object>();
		for (Object str : arr1) {
			if (!map.containsKey(str)) {
				map.put(str, Boolean.FALSE);
			}
		}
		for (Object str : arr2) {
			if (map.containsKey(str)) {
				map.put(str, Boolean.TRUE);
			}
		}

		for (Entry<Object, Boolean> e : map.entrySet()) {
			if (e.getValue().equals(Boolean.TRUE)) {
				list.add(e.getKey());
			}
		}
		Object[] result = {};
		return list.toArray(result);
	}

	// 求两个字符串数组的并集，利用set的元素唯一性
	public static Object[] union(Object[] arr1, Object[] arr2) {
		Set<Object> set = new HashSet<Object>();
		for (Object str : arr1) {
			set.add(str);
		}
		for (Object str : arr2) {
			set.add(str);
		}
		Object[] result = {};
		return set.toArray(result);
	}

	// 求两个数组的差集
	public static Object[] minus(Object[] arr1, Object[] arr2) {
		LinkedList<Object> list = new LinkedList<Object>();
		LinkedList<Object> history = new LinkedList<Object>();
		Object[] longerArr = arr1;
		Object[] shorterArr = arr2;
		// 找出较长的数组来减较短的数组
		if (arr1.length > arr2.length) {
			longerArr = arr2;
			shorterArr = arr1;
		}
		for (Object str : longerArr) {
			if (!list.contains(str)) {
				list.add(str);
			}
		}
		for (Object str : shorterArr) {
			if (list.contains(str)) {
				history.add(str);
				list.remove(str);
			} else {
				if (!history.contains(str)) {
					list.add(str);
				}
			}
		}
		Object[] result = {};
		return list.toArray(result);
	}
}