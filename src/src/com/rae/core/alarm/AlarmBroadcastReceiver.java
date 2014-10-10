package com.rae.core.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.rae.core.alarm.provider.AlarmProvider;
import com.rae.core.alarm.provider.AlarmProviderFactory;

public class AlarmBroadcastReceiver extends BroadcastReceiver {
	private static String	TAG	= "AlarmBroadcastReceiver";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if (AlarmProvider.ACTION_ALARM_WAKEUP.equals(intent.getAction())) {
			int alarmId = intent.getIntExtra(AlarmProvider.EXTRA_ALARM_ID, 0);
			AlarmEntity entity = AlarmProviderFactory.getDbAlarm(context).getAlarm(alarmId);
			if (entity != null) {
				onAlarm(entity); // 通知闹钟
				AlarmProvider provider = AlarmProviderFactory.getProvider(context, entity);
				provider.update(); // 更新闹钟
				provider = null;
				System.gc();
			}
			else {
				Log.e(TAG, "闹钟响铃，但获取闹钟实体为空！");
			}
		}
	}
	
	protected void onAlarm(AlarmEntity entity) {
		Log.i(TAG, "--------- 闹钟响铃 ：" + entity.getTitle() + " ----------- ");
	}
	
}
