package com.rae.core.alarm.provider;

import java.util.Arrays;
import java.util.Calendar;

import android.content.Context;
import android.util.Log;

import com.rae.core.alarm.AlarmEntity;
import com.rae.core.alarm.AlarmException;
import com.rae.core.alarm.AlarmUtils;

/**
 * ÿ�����ѣ�����һ������
 * 
 * @author ChenRui
 * 
 */
public class EveryWeekRepeatProvider extends AlarmProvider
{
	private int[]	mWeeks; // �ظ�����
							
	public EveryWeekRepeatProvider(Context context, AlarmEntity entity)
	{
		super(context, entity);
		mWeeks = mAlarmEntity.getWeeks();
		Arrays.sort(mWeeks); // ��������
	}
	
	@Override
	public AlarmEntity create()
	{
		if (null == mWeeks || mWeeks.length <= 0)
		{
			onAlarmError(new AlarmException("������������һ�����ظ���"));
			return this.mAlarmEntity;
		}
		long triggerAtMillis = getNextAlarmTime(converTime(System.currentTimeMillis(), mAlarmEntity.getTime()));
		set(triggerAtMillis);
		return this.mAlarmEntity;
	}
	
	@Override
	public void update()
	{
		create(); // ���¼���
	}
	
	@Override
	public void skip()
	{
		cancle(); // ȡ����ǰ��
		update(); // ������һ�����ڵġ�
	}
	
	@Override
	public long getNextAlarmTime(long currentTimeMillis)
	{
		Calendar calendar = Calendar.getInstance();
		int week = AlarmUtils.getDayOfWeek(currentTimeMillis); // ��ǰ���ܼ�
		
		boolean hasFind = false;
		long resultTimeMillis = currentTimeMillis;
		int index = 0; // �ܼ�
		
		for (int i = 0; i < mWeeks.length; i++)
		{
			int item = mWeeks[i];
			if (item >= week)
			{
				index = i; // ��ǰ����
				calendar.setTimeInMillis(currentTimeMillis);
				calendar.add(Calendar.DATE, item - week);
				resultTimeMillis = calendar.getTimeInMillis();
				hasFind = true;
				break; // �Ѿ��ҵ�
			}
		}
		
		if (!hasFind)
		{ // û���ҵ��������ܡ�
			calendar.clear();
			calendar.setTimeInMillis(currentTimeMillis);
			calendar.add(Calendar.DATE, 7 - week + mWeeks[0]); // ����ʣ������+�趨���ڵĵ�һ�졣
			resultTimeMillis = calendar.getTimeInMillis();
			Log.e(TAG, "����û�У��ݹ顣" + AlarmUtils.getDateByTimeInMillis(resultTimeMillis));
			return getNextAlarmTime(calendar.getTimeInMillis());
		}
		else if (resultTimeMillis <= System.currentTimeMillis())
		{ // ʱ�����,������һ�����ڼ����ݹ顣
			calendar.clear();
			calendar.setTimeInMillis(resultTimeMillis);
			index = index + 1; // ����һ����
			int span = 0;
			if (index >= mWeeks.length)
			{ // ѭ��һȦ��
				span = 7 - week + mWeeks[0];
			}
			else
			{
				span = mWeeks[index] - week;
				span = span < 0 ? 0 : span;
			}
			calendar.add(Calendar.DATE, span); // ����ʣ������+�趨���ڵĵ�һ�졣
			
			resultTimeMillis = calendar.getTimeInMillis();
			Log.e(TAG, "ʱ����ڣ��ݹ顣" + AlarmUtils.getDateByTimeInMillis(resultTimeMillis));
			return getNextAlarmTime(resultTimeMillis);
		}
		else
		{
			Log.i(TAG, "��������ʱ�䣺" + AlarmUtils.getDateByTimeInMillis(resultTimeMillis));
			return resultTimeMillis;
		}
	}
}
