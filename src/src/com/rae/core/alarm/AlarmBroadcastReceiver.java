package com.rae.core.alarm;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.text.TextUtils;
import android.util.Log;

import com.rae.core.alarm.provider.AlarmProvider;
import com.rae.core.alarm.provider.AlarmProviderFactory;

public class AlarmBroadcastReceiver extends BroadcastReceiver {
	private static String	TAG	= "AlarmBroadcastReceiver";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if (AlarmProvider.ACTION_ALARM_WAKEUP.equals(intent.getAction())) {
			int alarmId = intent.getIntExtra(AlarmProvider.EXTRA_ALARM_ID, 0);
			
			String intentName = "";
			
			ComponentName component = new ComponentName(context, this.getClass());
			try {
				ActivityInfo info = context.getPackageManager().getReceiverInfo(component, PackageManager.GET_META_DATA);
				intentName = info.metaData.getString("intent");
			}
			catch (NameNotFoundException e) {
				e.printStackTrace();
			}
			Log.i(TAG, intentName);
			
			AlarmEntity entity = AlarmProviderFactory.getDbAlarm(context).getAlarm(alarmId);
			if (entity != null) {
				onAlarm(entity); // 通知闹钟
				AlarmProvider provider = AlarmProviderFactory.getProvider(context, entity);
				provider.update(); // 更新闹钟
				provider = null;
				System.gc();
				
				if (!TextUtils.isEmpty(intentName)) {
					Intent ringIntent = new Intent();
					ringIntent.setClassName(context.getPackageName(), intentName);
					ringIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
					ringIntent.putExtra("data", entity);
					context.startActivity(ringIntent);
				}
				
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
