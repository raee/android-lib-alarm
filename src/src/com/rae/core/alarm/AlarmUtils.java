package com.rae.core.alarm;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.text.TextUtils;

@SuppressLint("SimpleDateFormat")
public final class AlarmUtils
{
	public static Calendar	calendar			= Calendar.getInstance();
	public static String	DEFAULT_DATE_FORMAT	= "yyyy-MM-dd HH:mm:ss";
	
	/**
	 * ��ȡ���ڵĳ����ͣ���ȡʧ�ܷ���ϵͳ��ǰʱ�䡣
	 * 
	 * @param date
	 *            ʱ��
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	public static long getTimeInMillis(String date)
	{
		Date currentDate;
		try
		{
			String format = "HH:mm";
			if (date.contains("-"))
			{
				format = "yyyy-MM-dd HH:mm:ss";
			}
			SimpleDateFormat dateForamt = new SimpleDateFormat(format);
			currentDate = dateForamt.parse(date);
		}
		catch (ParseException e)
		{
			e.printStackTrace();
			
			currentDate = new Date();
		}
		calendar.setTime(currentDate);
		return calendar.getTimeInMillis();
	}
	
	public static String getDateByTimeInMillis(long milliseconds)
	{
		calendar.setTimeInMillis(milliseconds);
		return dateToString(calendar.getTime());
	}
	
	/**
	 * �ַ���ת����
	 * 
	 * @param date
	 *            ���ڣ���ʽ2014-12-12 16:00:00
	 * @return
	 */
	public static Date parseDate(String date)
	{
		try
		{
			SimpleDateFormat dateFormat = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
			return dateFormat.parse(date);
		}
		catch (ParseException e)
		{
			e.printStackTrace();
		}
		return new Date();
	}
	
	/**
	 * ����ת�ַ���
	 * 
	 * @param date
	 *            ����
	 * @return
	 */
	public static String dateToString(Date date)
	{
		return dateToString(DEFAULT_DATE_FORMAT, date);
	}
	
	public static String dateToString(String format, String date)
	{
		long time = getTimeInMillis(date);
		calendar.clear();
		calendar.setTimeInMillis(time);
		
		return dateToString(format, calendar.getTime());
	}
	
	/**
	 * ����ת�ַ���
	 * 
	 * @param format
	 *            ��ʽ
	 * @param date����
	 * @return
	 */
	public static String dateToString(String format, Date date)
	{
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		return dateFormat.format(date);
	}
	
	/**
	 * ��ȡ��һ�����ܼ���
	 * 
	 * @return
	 */
	public static int getDayOfWeek(long time)
	{
		calendar.clear();
		calendar.setTimeInMillis(time);
		int result = calendar.get(Calendar.DAY_OF_WEEK) - 1; // ������������Ϊ��һ��ģ������ȥ1
		if (result <= 0)
		{
			result = 7;
		}
		return result;
	}
	
	/**
	 * ��ȡ�´��������ھ������ڻ��ж�ã���λ���롣
	 */
	public static long getNextAlarmTimeSpan(String time)
	{
		calendar.clear();
		calendar.setTime(parseDate(time));
		return (calendar.getTimeInMillis() - System.currentTimeMillis()) / 1000; // �õ�ʱ���,��
	}
	
	public static String getNextTimeSpanString(String time)
	{
		long timespan = getNextAlarmTimeSpan(time);
		String result = "";
		
		calendar.setTimeInMillis(System.currentTimeMillis());
		int year = calendar.getActualMaximum(Calendar.DAY_OF_YEAR); // ��������һ��
		int month = calendar.getActualMaximum(Calendar.DAY_OF_MONTH); // ����µ����һ��
		long Y = timespan / year / 24 / 60 / 60; // ������
		long M = timespan / month / 24 / 60 / 60; // ������
		long d = timespan / 24 / 60 / 60; // ������
		long h = (timespan % (24 * 60 * 60)) / 3600; // ����Сʱ
		long m = (timespan % 3600) / 60; // ���ٷ�
		long s = (timespan % 3600) % 60;// ������
		
		DateForamt df = new DateForamt();
		df.year = Y;
		df.month = M;
		df.day = d;
		df.hour = h;
		df.minute = m;
		df.secound = s;
		df.span = timespan;
		
		if (df.span < 0)
		{
			result = "ʱ���ѹ�";
		}
		else if (df.minute < 1)
		{
			result = df.secound + "��";
		}
		else if (df.hour < 1)
		{
			result = df.minute + "��" + df.secound + "��";
		}
		else if (df.day < 1)
		{
			result = df.hour + "ʱ";
			if (df.minute > 0)
			{
				result += df.minute + "��";
			}
			result += df.secound + "��";
		}
		else if (df.day == 1)
		{
			result = "����";
		}
		else if (df.day == 2)
		{
			result = "����";
		}
		else if (df.day == 3)
		{
			result = "�����";
		}
		else if (df.month < 1)
		{
			result = df.day + "��";
		}
		else if (df.month == 6)
		{
			result = "����";
		}
		else if (df.year > 0)
		{
			result = df.year + "��";
		}
		else
		{
			result = df.month + "��";
		}
		
		return result;
		
	}
	
	static class DateForamt
	{
		/**
		 * ��
		 */
		public long	year;
		
		/**
		 * ��
		 */
		public long	month;
		
		/**
		 * ��
		 */
		public long	day;
		
		/**
		 * ʱ
		 */
		public long	hour;
		
		/**
		 * ��
		 */
		public long	minute;
		
		/**
		 * ��
		 */
		public long	secound;
		
		/**
		 * ʱ����
		 */
		public long	span;
	}
	
	public static String getWeekString(int week)
	{
		switch (week)
		{
			case 0:
				return "��";
			case 1:
				return "һ";
			case 2:
				return "��";
			case 3:
				return "��";
			case 4:
				return "��";
			case 5:
				return "��";
			case 6:
				return "��";
			case 7:
				return "��";
			default:
				return "Error!";
		}
	}
	
	public static AlarmEntity converEntity(String json)
	{
		AlarmEntity entity = null;
		try
		{
			JSONObject obj = new JSONObject(json);
			String cycle = obj.getString("cycle");
			String title = obj.getString("title");
			String time = obj.getString("time");
			
			entity = new AlarmEntity(cycle, title, time);
			entity.setContent(obj.getString("content"));
			entity.setOtherParam(obj.getString("otherParam"));
			String weeks = obj.getString("weeks");
			if (!TextUtils.isEmpty(weeks))
			{
				JSONArray arr = new JSONArray(weeks);
				//				String[] items = weeks.split(weeks);
				int[] values = new int[arr.length()];
				for (int i = 0; i < arr.length(); i++)
				{
					values[i] = Integer.valueOf(arr.getInt(i));
				}
				entity.setWeeks(values);
			}
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		
		return entity;
	}
}
