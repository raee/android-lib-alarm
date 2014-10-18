package com.rae.core.alarm.provider;

import android.content.Context;

import com.rae.core.alarm.AlarmEntity;

/**
 * 一次性闹钟
 * 
 * @author ChenRui
 * 
 */
public class OnceAlarmProvider extends AlarmProvider {

	public OnceAlarmProvider(Context context, AlarmEntity entity) {
		super(context, entity);
	}

	@Override
	public AlarmEntity create() {
		set(mAlarmEntity.getTime());
		return this.mAlarmEntity;
	}

	@Override
	public void update() {
		getDatabase().delete(mAlarmEntity); // 从数据库中删除闹钟
	}

	@Override
	public void skip() {
		cancle(); // 取消当前闹钟
		getDatabase().delete(mAlarmEntity); // 　删除闹钟
	}

	@Override
	public long getNextAlarmTime(long currentTimeAtMillis) {
		return 0; // 一次性闹钟没有下次时间。
	}
}
