package com.rae.core.alarm;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;

@SuppressLint("SimpleDateFormat")
public final class AlarmUtils {
	public static Calendar	calendar			= Calendar.getInstance();
	public static String	DEFAULT_DATE_FORMAT	= "yyyy-MM-dd HH:mm:ss";
	
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
		}
		catch (ParseException e) {
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
		}
		catch (ParseException e) {
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
	
	public static String dateToString(String format, String date) {
		long time =  getTimeInMillis(date);
		calendar.clear();
		calendar.setTimeInMillis(time);		
		
		return dateToString(format, calendar.getTime());
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
	
	/**
	 * 获取下次响铃日期距离现在还有多久，单位：秒。
	 */
	public static long getNextAlarmTimeSpan(String time) {
		calendar.clear();
		calendar.setTime(parseDate(time));
		return (calendar.getTimeInMillis() - System.currentTimeMillis()) / 1000; // 得到时间差,秒
	}
	
	public static String getNextTimeSpanString(String time) {
		long timespan = getNextAlarmTimeSpan(time);
		String result = "";
		
		calendar.setTimeInMillis(System.currentTimeMillis());
		int year = calendar.getActualMaximum(Calendar.DAY_OF_YEAR); // 这年的最后一天
		int month = calendar.getActualMaximum(Calendar.DAY_OF_MONTH); // 这个月的最后一天
		long Y = timespan / year / 24 / 60 / 60; // 多少年
		long M = timespan / month / 24 / 60 / 60; // 多少月
		long d = timespan / 24 / 60 / 60; // 多少天
		long h = (timespan % (24 * 60 * 60)) / 3600; // 多少小时
		long m = (timespan % 3600) / 60; // 多少分
		long s = (timespan % 3600) % 60;// 多少秒
		
		DateForamt df = new DateForamt();
		df.year = Y;
		df.month = M;
		df.day = d;
		df.hour = h;
		df.minute = m;
		df.secound = s;
		df.span = timespan;
		
		if (df.span < 0) {
			result = "时间已过";
		}
		else if (df.minute < 1) {
			result = df.secound + "秒";
		}
		else if (df.hour < 1) {
			result = df.minute + "分" + df.secound + "秒";
		}
		else if (df.day < 1) {
			result = df.hour + "时";
			if (df.minute > 0) {
				result += df.minute + "分";
			}
			result += df.secound + "秒";
		}
		else if (df.day == 1) {
			result = "明天";
		}
		else if (df.day == 2) {
			result = "后天";
		}
		else if (df.day == 3) {
			result = "大后天";
		}
		else if (df.month < 1) {
			result = df.day + "天";
		}
		else if (df.month == 6) {
			result = "半年";
		}
		else if (df.year > 0) {
			result = df.year + "年";
		}
		else {
			result = df.month + "月";
		}
		
		return result;
		
	}
	
	static class DateForamt {
		/**
		 * 年
		 */
		public long	year;
		
		/**
		 * 月
		 */
		public long	month;
		
		/**
		 * 天
		 */
		public long	day;
		
		/**
		 * 时
		 */
		public long	hour;
		
		/**
		 * 分
		 */
		public long	minute;
		
		/**
		 * 秒
		 */
		public long	secound;
		
		/**
		 * 时间间隔
		 */
		public long	span;
	}
	
	public static String getWeekString(int week) {
		switch (week) {
			case 0:
				return "日";
			case 1:
				return "一";
			case 2:
				return "二";
			case 3:
				return "三";
			case 4:
				return "四";
			case 5:
				return "五";
			case 6:
				return "六";
			case 7:
				return "日";
			default:
				return "Error!";
		}
	}
}
