package com.people.sotp.commons.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
	/**
	 * 获取当前日期和时间的中文显示
	 * 
	 * @return
	 */
	public static String nowCn() {
		return new SimpleDateFormat("yyyy年MM月dd日 HH时:mm分:ss秒").format(new Date());
	}

	/**
	 * 获取当前日期和时间的英文显示
	 * 
	 * @return
	 */
	public static String nowEn() {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
	}

	/**
	 * 获取当前日期和时间的英文显示
	 * 
	 * @return
	 */
	public static String nowEn(String formate) {
		return new SimpleDateFormat(formate).format(new Date());
	}

	/**
	 * 获取当前日期的中文显示
	 * 
	 * @return
	 */
	public static String nowDateCn() {
		return new SimpleDateFormat("yyyy年MM月dd日").format(new Date());
	}

	/**
	 * 获取当前日期的英文显示
	 * 
	 * @return
	 */
	public static String nowDateEn() {
		return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
	}

	/**
	 * 获取当前时间的中文显示
	 * 
	 * @return
	 */
	public static String nowTimeCn() {
		return new SimpleDateFormat("HH时:mm分:ss秒").format(new Date());
	}

	/**
	 * 获取当前时间的英文显示
	 * 
	 * @return
	 */
	public static String nowTimeEn() {
		return new SimpleDateFormat("HH:mm:ss").format(new Date());
	}

	/**
	 * 获取当前时间的英文显示
	 * 
	 * @return
	 */
	public static String getNowDate() {
		return new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
	}

	/**
	 * 获取当前日期的指定格式
	 * 
	 * @param format
	 * @return
	 * @author tianchk
	 */
	public static String nowDate(String format) {
		return new SimpleDateFormat(format).format(new Date());
	}

	/**
	 * 获取日期的指定格式
	 * 
	 * @param format
	 * @param date
	 * @return
	 * @author tianchk
	 */
	public static String formatDate(String format, Date date) {
		return new SimpleDateFormat(format).format(date);
	}

	/**
	 * 获取当前月第一天
	 * 
	 * @param format
	 * @return
	 * @author tianchk
	 */
	public static String getCurrentMonthFirstDay(String format) {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MONTH, 0);
		c.set(Calendar.DAY_OF_MONTH, 1);
		return new SimpleDateFormat(format).format(c.getTime());
	}

	/**
	 * 获取当前月第一天的中文显示
	 * 
	 * @return
	 * @author tianchk
	 */
	public static String getCurrentMonthFirstDay() {
		return getCurrentMonthFirstDay("yyyy年MM月dd日");
	}

	/**
	 * 获取当前月最后一天
	 * 
	 * @param format
	 * @return
	 * @author tianchk
	 */
	public static String getCurrentMonthLastDay(String format) {
		Calendar ca = Calendar.getInstance();
		ca.set(Calendar.DAY_OF_MONTH, ca.getActualMaximum(Calendar.DAY_OF_MONTH));
		return new SimpleDateFormat(format).format(ca.getTime());
	}

	/**
	 * 字符串转成日期
	 * 
	 * @param dateString
	 * @param format
	 * @return
	 * @throws ParseException
	 * @author tianchk
	 */
	public static Date getDateFromStringByFormat(String dateString, String format) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.parse(dateString);
	}

	/**
	 * 返回天数
	 * 
	 * @param date
	 * @return
	 * @author tianchk
	 */
	public static int getDay(java.util.Date date) {
		java.util.Calendar c = java.util.Calendar.getInstance();
		c.setTime(date);
		return c.get(java.util.Calendar.DAY_OF_MONTH);
	}

	/**
	 * 获取指定账期第一天
	 * 
	 * @param format
	 * @return
	 * @author tianchk
	 * @throws ParseException
	 */
	public static String getCycleMonthFirstDay(String format, String mongth) {
		Calendar c = Calendar.getInstance();
		try {
			c.setTime(DateUtil.getDateFromStringByFormat(mongth, "yyyyMM"));
		} catch (ParseException e) {
			e.printStackTrace();
			throw new RuntimeException("账期格式不正确");
		}
		c.add(Calendar.MONTH, 0);
		c.set(Calendar.DAY_OF_MONTH, 1);
		return new SimpleDateFormat(format).format(c.getTime());
	}

	/**
	 * 获取指定账期最后一天
	 * 
	 * @param format
	 * @return
	 * @author tianchk
	 */
	public static String getCycleMonthLastDay(String format, String mongth) {
		Calendar ca = Calendar.getInstance();
		try {
			ca.setTime(DateUtil.getDateFromStringByFormat(mongth, "yyyyMM"));
		} catch (ParseException e) {
			e.printStackTrace();
			throw new RuntimeException("账期格式不正确");
		}
		ca.set(Calendar.DAY_OF_MONTH, ca.getActualMaximum(Calendar.DAY_OF_MONTH));
		return new SimpleDateFormat(format).format(ca.getTime());
	}

	/**
	 * 获取通话时长的 xx时xx分xx秒 格式
	 * 
	 * @param conversationSecond
	 *            通话时长 单位:秒
	 * @return xx时xx分xx秒 字符串格式
	 * @author 李明顶
	 *
	 *         Date: 2014年4月9日 <br>
	 */
	public static String getConversationTime(final int conversationSecond) {
		if (conversationSecond / 60 > 0) {
			// 大于一分钟
			int secondOut = conversationSecond % 60;
			int minute = conversationSecond / 60;
			if (minute / 60 > 0) {
				// 大于一小时
				int minuteOut = minute % 60;
				int hours = minute / 60;
				return minuteOut != 0 && secondOut != 0 ? hours + "时" + minuteOut + "分" + secondOut + "秒"
						: minuteOut == 0 && secondOut != 0 ? hours + "时" + "零" + secondOut + "秒"
								: minuteOut != 0 && secondOut == 0 ? hours + "时" + minuteOut + "分" : hours + "时";
			} else {
				return secondOut == 0 ? minute + "分" : minute + "分" + secondOut + "秒";
			}
		} else {
			return conversationSecond + "秒";
		}
	}

	public static void main(String[] args) throws ParseException {
		// Date date=new Date();
		// System.out.println(DateUtil.formatDate("yyyyMMddHHmmss", date));
		// Calendar calendar=Calendar.getInstance();
		// calendar.setTime(date);
		// calendar.add(Calendar.MINUTE, 5);
		// System.out.println(DateUtil.formatDate("yyyyMMddHHmmss",
		// calendar.getTime()));
		// System.out.println(!calendar.before(date));
		// System.out.println(getDay(
		// DateUtil.getDateFromStringByFormat("2013-10-01 00:41:31",
		// "yyyy-MM-dd")));
		// System.out.println(DateUtil.getCycleMonthFirstDay("yyyy-MM-dd",
		// "201406"));

		System.out.println(new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒")
				.format(new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", java.util.Locale.ENGLISH)
						.parse("Wed Apr 16 15:04:11 CST 2014")));
	}
}
