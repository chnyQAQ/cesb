package com.dah.desb.infrastructure.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {

	/**
	 * @param date
	 *            自定义时间
	 * @param format
	 *            自定义格式
	 * @return 返回格式化的日期串
	 */
	public static String getDate(Date date, String format) {
		return new SimpleDateFormat(format).format(date);
	}

	/**
	 * @param date
	 *            自定义时间
	 * @return 返回格式化的日期串 yyyy-MM-dd
	 */
	public static String getDate(Date date) {
		return getDate(date, "yyyy-MM-dd");
	}

	/**
	 * @param date
	 *            自定义时间
	 * @return 返回格式化的日期串 yyyy-MM-dd HH:mm:ss SSS
	 */
	public static String getDateTime(Date date) {
		return getDate(date, "yyyy-MM-dd HH:mm:ss SSS");
	}

	/**
	 * @param source
	 *            日期串
	 * @param sdf
	 *            格式化工具
	 * @return 日期
	 */
	public static Date parse(String source, SimpleDateFormat sdf) {
		try {
			return sdf.parse(source);
		} catch (ParseException e) {
			return null;
		}
	}

	/**
	 * @param date
	 *            （yyyy-MM-dd）
	 * @return 日期
	 */
	public static Date parseDate(String date) {
		try {
			return new SimpleDateFormat("yyyy-MM-dd").parse(date);
		} catch (ParseException e) {
			return null;
		}
	}

	/**
	 * @param dateTime
	 *            （yyyy-MM-dd HH:mm:ss SSS）
	 * @return 日期
	 */
	public static Date parseDateTime(String dateTime) {
		try {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS").parse(dateTime);
		} catch (ParseException e) {
			return null;
		}
	}
	
	
	public static Date addDays(Date date, int days) {
		Calendar cNow = Calendar.getInstance();
		cNow.setTime(date);
		cNow.add(Calendar.DATE, days);
		return new Date(cNow.getTimeInMillis());
	}
}
