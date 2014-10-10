package com.rae.core.alarm;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;

@SuppressLint("SimpleDateFormat")
public final class AlarmUtils {
	public static Calendar calendar = Calendar.getInstance();
	public static String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

	/**
	 * 获取日期的长整型，获取失败返回系统当前时间。
	 * 
	 * @param date
	 *            时间
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	public static long getTimeInMillis(String date) {
		Date currentDate;
		try {
			String format = "HH:mm";
			if (date.contains("-")) {
				format = "yyyy-MM-dd HH:mm:ss";
			}
			SimpleDateFormat dateForamt = new SimpleDateFormat(format);
			currentDate = dateForamt.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();

			currentDate = new Date();
		}
		calendar.setTime(currentDate);
		return calendar.getTimeInMillis();
	}

	public static String getDateByTimeInMillis(long milliseconds) {
		calendar.setTimeInMillis(milliseconds);
		return dateToString(calendar.getTime());
	}

	/**
	 * 字符串转日期
	 * 
	 * @param date
	 *            日期，格式2014-12-12 16:00:00
	 * @return
	 */
	public static Date parseDate(String date) {
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
			return dateFormat.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return new Date();
	}

	/**
	 * 日期转字符串
	 * 
	 * @param date
	 *            日期
	 * @return
	 */
	public static String dateToString(Date date) {
		return dateToString(DEFAULT_DATE_FORMAT, date);
	}

	/**
	 * 日期转字符串
	 * 
	 * @param format
	 *            格式
	 * @param date日期
	 * @return
	 */
	public static String dateToString(String format, Date date) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		return dateFormat.format(date);
	}

	/**
	 * 获取这一天是周几。
	 * 
	 * @return
	 */
	public static int getDayOfWeek(long time) {
		calendar.clear();
		calendar.setTimeInMillis(time);
		int result = calendar.get(Calendar.DAY_OF_WEEK) - 1; // 这里是星期天为第一天的，则需减去1
		if (result <= 0) {
			result = 7;
		}
		return result;
	}

}
