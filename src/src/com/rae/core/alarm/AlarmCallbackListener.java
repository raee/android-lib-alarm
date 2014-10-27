package com.rae.core.alarm;

import com.rae.core.alarm.provider.AlarmProvider;

/**
 * ÄÖÖÓ»Øµ÷¼àÌý
 * 
 * @author ChenRui
 * 
 */
public interface AlarmCallbackListener {
	void onAlarmCreated(AlarmProvider provider, AlarmEntity entity, String nextTimeTips);
	
	void onAlarmCancle(AlarmProvider provider, AlarmEntity entity);
	
	void onAlarmDelete(AlarmProvider provider, AlarmEntity entity);
	
	void onAlarmUpdate(AlarmProvider provider, AlarmEntity entity, String lastTime, String nextTime);
	
	void onAlarmError(AlarmException e);
}
