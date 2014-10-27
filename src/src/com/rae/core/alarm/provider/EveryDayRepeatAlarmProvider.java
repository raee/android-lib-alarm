package com.rae.core.alarm.provider;

import android.content.Context;

import com.rae.core.alarm.AlarmEntity;

/**
 * √øÃÏ÷ÿ∏¥ƒ÷÷”
 * 
 * @author ChenRui
 * 
 */
public class EveryDayRepeatAlarmProvider extends EveryOneTimeRepeatProvider {

	public EveryDayRepeatAlarmProvider(Context context, AlarmEntity entity) {
		super(context, entity);
		entity.setTimeSpan(AlarmEntity.TIME_OF_ONE_DAY);
	}
}
