package com.rae.core.alarm.provider;

import java.util.Calendar;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.rae.core.alarm.AlarmEntity;
import com.rae.core.alarm.AlarmUtils;

/**
 * 每N间隔1次。
 * 
 * @author ChenRui
 * 
 */
public class EveryOneTimeRepeatProvider extends AlarmProvider {

	public EveryOneTimeRepeatProvider(Context context, AlarmEntity entity) {
		super(context, entity);
	}

	@Override
	public AlarmEntity create() {
		long triggerAtMillis;
		if (TextUtils.isEmpty(mAlarmEntity.getNextTime())) { // 第一次创建
			triggerAtMillis = oneCreate();
		} else {
			triggerAtMillis = AlarmUtils.getTimeInMillis(mAlarmEntity.getNextTime());
			Log.i(TAG, "取下次响铃时间！");
		}

		if (triggerAtMillis <= System.currentTimeMillis()) {
			triggerAtMillis = getNextAlarmTime(triggerAtMillis); // 如果超过时间，则设置为下次启动时间。
		}

		setRepeat(triggerAtMillis, getTimeSpan());
		return this.mAlarmEntity;
	}


	private int getTimeSpan() {
		return mAlarmEntity.getTimeSpan();
	}
	
	/**
	 * 第一次创建时别调用
	 */
	protected long oneCreate() {
		Log.i(TAG, "闹钟为第一次创建！");
		return converTime(System.currentTimeMillis(), mAlarmEntity.getTime());
	}
	
	

	@Override
	public void update() {
		String time = mAlarmEntity.getNextTime(); // 下次响铃时间 = 当前时间 + 重复周期
		calendar.clear();
		calendar.setTime(AlarmUtils.parseDate(time));
		calendar.add(Calendar.MILLISECOND, getTimeSpan());
		mAlarmEntity.setNextTime(calendar.getTimeInMillis()); // 设置下次响铃时间。
		mAlarmEntity.setState(AlarmEntity.STATUS_WAITING); // 更新状态为等待响铃状态。
		Log.i(TAG, "下次响铃时间：" + mAlarmEntity.getNextTime());
		getDatabase().update(mAlarmEntity); // 更新实体
	}

	@Override
	public void skip() {
		cancle(); // 取消闹钟
		update(); // 更新下次闹钟
		create(); // 重新开始
	}

	/**
	 * 获取下次响铃时间时间
	 * 
	 * @param time
	 *            当前时间，格式：08:00
	 * @return
	 */
	public long getNextAlarmTime(long currentTimeAtMillis) {
		long current = System.currentTimeMillis();
		if (currentTimeAtMillis > current) {
			return currentTimeAtMillis;
		}

		// 1、获取两者的时间间隔 / 间隔周期
		long span = (current - currentTimeAtMillis) % getTimeSpan();

		// 2 、如果能整除，取当前时间 + 间隔周期
		if (span == 0) {
			calendar.setTimeInMillis(current);
			calendar.add(Calendar.MILLISECOND, getTimeSpan());
			currentTimeAtMillis = calendar.getTimeInMillis();
		} else {
			// 3、不能整除则：触发时间 + (取商+1)*间隔周期。
			int s = (int) ((current - currentTimeAtMillis) / getTimeSpan()) + 1;
			calendar.setTimeInMillis(currentTimeAtMillis);
			calendar.add(Calendar.MILLISECOND, s * getTimeSpan());
			currentTimeAtMillis = calendar.getTimeInMillis();
		}
		Log.i(TAG, "下次启动时间为：" + AlarmUtils.getDateByTimeInMillis(currentTimeAtMillis));
		calendar.clear();
		return currentTimeAtMillis;
	}

}
