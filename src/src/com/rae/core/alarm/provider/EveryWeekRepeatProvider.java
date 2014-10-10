package com.rae.core.alarm.provider;

import java.util.Arrays;
import java.util.Calendar;

import android.content.Context;
import android.util.Log;

import com.rae.core.alarm.AlarmEntity;
import com.rae.core.alarm.AlarmException;
import com.rae.core.alarm.AlarmUtils;

/**
 * 每周提醒，如周一、周三
 * 
 * @author ChenRui
 * 
 */
public class EveryWeekRepeatProvider extends AlarmProvider {
	private int[] mWeeks; // 重复周期

	public EveryWeekRepeatProvider(Context context, AlarmEntity entity) {
		super(context, entity);
		mWeeks = mAlarmEntity.getWeeks();
		Arrays.sort(mWeeks); // 重新排序
	}

	@Override
	public void create() {
		if (null == mWeeks || mWeeks.length <= 0) {
			onAlarmError(new AlarmException("必须至少设置一个周重复。"));
			return;
		}
		long triggerAtMillis = getNextAlarmTime(converTime(System.currentTimeMillis(), mAlarmEntity.getTime()));
		set(triggerAtMillis);
	}

	@Override
	public void update() {
		create(); // 重新计算
	}

	@Override
	public void skip() {
		cancle(); // 取消当前的
		update(); // 更新下一次周期的。
	}

	@Override
	public long getNextAlarmTime(long currentTimeMillis) {
		Calendar calendar = Calendar.getInstance();
		int week = AlarmUtils.getDayOfWeek(currentTimeMillis);

		boolean hasFind = false;
		long resultTimeMillis = currentTimeMillis;
		int index = 0;

		for (int i = 0; i < mWeeks.length; i++) {
			int item = mWeeks[i];
			if (item >= week) {
				index = i; // 当前索引
				calendar.setTimeInMillis(currentTimeMillis);
				calendar.add(Calendar.DATE, item - week);
				resultTimeMillis = calendar.getTimeInMillis();
				hasFind = true;
				break; // 已经找到
			}
		}

		if (resultTimeMillis <= System.currentTimeMillis()) { // 时间过期,加上下一个周期继续递归。
			calendar.clear();
			calendar.setTimeInMillis(resultTimeMillis);
			index = index + 1; // 找下一周期
			int span = 0;
			if (index >= mWeeks.length) { // 循环一圈。
				span = 7 - week + mWeeks[0];
			} else {
				span = mWeeks[index] - week;
				span = span < 0 ? 0 : span;
			}
			calendar.add(Calendar.DATE, span); // 这周剩余天数+设定周期的第一天。

			resultTimeMillis = calendar.getTimeInMillis();
			Log.e(TAG, "时间过期，递归。" + AlarmUtils.getDateByTimeInMillis(resultTimeMillis));
			return getNextAlarmTime(resultTimeMillis);
		} else if (!hasFind) { // 没有找到，找下周。
			calendar.clear();
			calendar.setTimeInMillis(currentTimeMillis);
			calendar.add(Calendar.DATE, 7 - week + mWeeks[0]); // 这周剩余天数+设定周期的第一天。
			resultTimeMillis = calendar.getTimeInMillis();
			Log.e(TAG, "本周没有，递归。" + AlarmUtils.getDateByTimeInMillis(resultTimeMillis));
			return getNextAlarmTime(calendar.getTimeInMillis());
		} else {
			Log.i(TAG, "闹钟启动时间：" + AlarmUtils.getDateByTimeInMillis(resultTimeMillis));
			return resultTimeMillis;
		}
	}
}
