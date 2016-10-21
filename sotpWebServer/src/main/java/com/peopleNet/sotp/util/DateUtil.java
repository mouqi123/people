package com.peopleNet.sotp.util;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;

public class DateUtil {

	public static String DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss";
	/** 是否合法日期字符串 */
	private static boolean isValidDate;
	/** 日期检查错误信息 */
	private static String errorMsg;

	/**
	 * 说明: 日期转化为字符串(默认格式 yyyy-mm-dd hh24:mi:ss)
	 *
	 * @param dt
	 *            日期
	 * @return dateString 转化后的字符串
	 */
	public static String formatDate(Date dt) {

		return formatDate(dt, "yyyy-MM-dd HH:mm:ss");
	}

	/**
	 * 说明: 日期转化为字符串
	 *
	 * @param dt
	 *            日期
	 * @param sf
	 *            日期格式化定义
	 * @return dateString 转化后的字符串
	 */
	public static String formatDate(Date dt, String sf) {
		// Format the current time.
		SimpleDateFormat sdf = new SimpleDateFormat(sf);
		return sdf.format(dt);
	}

	/**
	 * 说明: 日期转化为SQL字符串(默认格式 yyyy-mm-dd hh24:mi:ss)
	 *
	 * @param dt
	 *            日期
	 * @return dateString 转化后的字符串
	 */
	public static String formatDateSQL(Date dt) {
		String sqlString = formatDate(dt);
		sqlString = " to_date('" + sqlString + "', 'yyyy-mm-dd hh24:mi:ss') ";
		return sqlString;
	}

	/**
	 * 说明: 字符串转化为SQL字符串(默认格式 yyyy-mm-dd hh24:mi:ss)
	 *
	 * @param dtString
	 *            日期字符串
	 * @return dateString 转化后的SQL字符串
	 */
	public static String formatDateSQL(String dtString) {
		String sqlString;
		// 防止 2-29等日期溢出错误。
		Date dt;
		// 将提交的日期转化为JAVA日期
		dt = DateUtil.parseString(dtString, "yyyy-MM-dd");
		// 将JAVA日期转化为提交的日期
		dtString = DateUtil.formatDate(dt, "yyyy-MM-dd");
		sqlString = " to_date('" + dtString + "', 'yyyy-mm-dd') ";
		return sqlString;
	}

	/**
	 * 说明: 字符串转化为SQL字符串
	 *
	 * @param dtString
	 *            日期字符串
	 * @param sf
	 *            数据库日期格式
	 * @return dateString 转化后的SQL字符串
	 */
	public static String formatDateSQL(String dtString, String sf) {
		String sqlString;
		// 防止 2-29等日期溢出错误。
		Date dt;
		// 将提交的日期转化为JAVA日期
		dt = DateUtil.parseString(dtString, "yyyy-MM-dd");
		// 将JAVA日期转化为提交的日期
		dtString = DateUtil.formatDate(dt, "yyyy-MM-dd");
		sqlString = " to_date('" + dtString + "', '" + sf + "') ";
		return sqlString;
	}

	/**
	 * 说明: 字符串转换为日期 (默认格式 yyyy-MM-dd HH:mm:ss)
	 *
	 * @param dateString
	 *            日期格式字符串
	 * @return 转换后的日期
	 */
	public static Date getDateTimeByString(String dateString) {

		String sf = "yyyy-MM-dd HH:mm:ss";
		Date dt = parseString(dateString, sf);
		return dt;

	}

	/**
	 * 说明: 字符串转换为日期 (默认格式 yyyy-MM-dd hh:)
	 *
	 * @param dateString
	 *            日期格式字符串
	 * @return 转换后的日期
	 */
	public static Date parseString(String dateString) {
		String sf = "yyyy-MM-dd";
		Date dt = parseString(dateString, sf);
		return dt;
	}

	/**
	 * 说明: 字符串转换为日期
	 *
	 * @param dateString
	 *            日期格式字符串
	 * @param sf
	 *            日期格式化定义
	 * @return 转换后的日期
	 */
	public static Date parseString(String dateString, String sf) {
		// Parse the previous string back into a Date.
		ParsePosition pos = new ParsePosition(0);
		// Format the current time.
		SimpleDateFormat sdf = new SimpleDateFormat(sf);
		Date dt = sdf.parse(dateString, pos);
		return dt;
	}

	/**
	 * 说明: 字符串(带时间)转换为日期
	 *
	 * @param dateString
	 *            日期格式字符串
	 * @param sf
	 *            日期格式化定义
	 * @return 转换后的日期
	 */
	public static Date timeString(String dateString, String sf) {
		// Parse the previous string back into a Date.
		// Format the current time.
		SimpleDateFormat sdf = new SimpleDateFormat(sf);
		Date dt = null;
		try {
			dt = sdf.parse(dateString);
		} catch (Throwable e) {

		}
		return dt;
	}

	/**
	 * 说明: 是否合法日期
	 *
	 * @return 是否合法日期
	 */
	public static boolean isValidDate() {
		return isValidDate;
	}

	/**
	 * 说明: 获取错误信息提示
	 * 
	 * @return errorMsg 错误信息
	 */
	public static String getErrorMsg() {
		return errorMsg;
	}

	public static String getYear(Date dt) {
		return formatDate(dt, "yyyy");
	}

	/**
	 * 获取该月的最后一天的时间
	 * 
	 * @param year
	 *            年
	 * @param month
	 *            月
	 * @return 日期
	 */
	public static Date getLastDayByYear(int year, int month) {
		java.util.Calendar cal = java.util.Calendar.getInstance();
		cal.set(year, month - 1, getLastDayByMonth(year, month), 23, 59, 59);
		return cal.getTime();
	}

	/**
	 * 取得该月的最后一天
	 * 
	 * @param year
	 * @param month
	 * @return
	 * @note: 备注 1,3,5,7,8,10,12 ----31天 2 闰年----29天,平年---28天 2,4,6,9,11 ---30天
	 *        能被4整除,但不能被400整除的就是闰年 其余的是平年
	 */
	private static int getLastDayByMonth(int year, int month) {
		switch (month) {
		case 1:
			return 31;
		case 2:
			if (year / 4 == 0 && year / 400 != 0)
				return 29;
			else
				return 28;
		case 3:
			return 31;
		case 4:
			return 30;
		case 5:
			return 31;
		case 6:
			return 30;
		case 7:
			return 31;
		case 8:
			return 31;
		case 9:
			return 30;
		case 10:
			return 31;
		case 11:
			return 30;
		case 12:
			return 31;
		}
		return 0;
	}

	/**
	 * 功能:比较所需日期与当前日期的差值 如果所需日期比当前日期大,返回ture 否则返回fasle
	 * 
	 * @param comparedate
	 * @param longtime
	 * @return
	 */
	public static boolean compareDate(Date comparedate, long longtime) {
		boolean tag = true;
		Date currentDate = new Date();

		long begintime = currentDate.getTime();
		long endtime = comparedate.getTime();
		if ((begintime - endtime) > longtime) {
			tag = true;
		} else {
			tag = false;
		}
		return tag;

	}

	public static boolean compareMonth(Date comparedate) {
		boolean comparemonth_tag = false;
		String formate_comparedate = formatDate(comparedate, "yyyy-MM");
		String formate_systemdate = formatDate(new Date(), "yyyy-MM");
		// System.out.println("formate_comparedate==="+formate_comparedate);
		// System.out.println("formate_systemdate===="+formate_systemdate);
		if (formate_comparedate.equals(formate_systemdate)) {
			comparemonth_tag = true;
		}
		return comparemonth_tag;
	}

	// public static String formatDateToString(Date formatDate){
	// String datetostring="";
	// datetostring=formatDate(formatDate,"yyyy-mm-dd");
	//
	// return datetostring;
	//
	// }

	public static Integer getAgeByBirthday(Date birthday) {
		if (null == birthday) {
			return null;
		}
		long curTime = new Date().getTime();
		if (birthday.getTime() >= curTime) {
			return 0;
		}
		long dayNum = (curTime - birthday.getTime()) / (1000 * 60 * 60 * 24);
		return (new Long(dayNum / 365).intValue());
	}

	/*
	 * 取得时间戳（精确到秒）
	 * 
	 * @return
	 */
	public static String timeStamp(long militime) {
		long time = 0;
		if (militime == 0) {
			time = System.currentTimeMillis();
		} else {
			time = militime;
		}

		String t = String.valueOf(time / 1000);
		return t;
	}

    public boolean compare(String time1,String time2) throws ParseException {
        //如果想比较日期则写成"yyyy-MM-dd"就可以了
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        //将字符串形式的时间转化为Date类型的时间
        Date a=sdf.parse(time1);
        Date b=sdf.parse(time2);
        //Date类的一个方法，如果a早于b返回true，否则返回false
        if(a.before(b))
            return true;
        else
            return false;
    }

    public static void main(String[] args) {
		String mm = "2009-09-09 12:23:45";
		Date curDate = DateUtil.getDateTimeByString(mm);

		System.out.println(curDate);
	}

}
