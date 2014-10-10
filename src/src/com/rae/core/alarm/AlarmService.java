package com.rae.core.alarm;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.rae.core.alarm.provider.AlarmProviderFactory;

/**
 * 闹钟服务
 * 
 * @author ChenRui
 * 
 */
public class AlarmService extends Service {
	private static final String TAG = "AlarmService";

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.i(TAG, "闹钟服务开启！");
		AlarmProviderFactory.refreshAlarmList(getApplicationContext());

	}

	@Override
	public void onDestroy() {
		Log.e(TAG, "闹钟服务被关闭，取消所有闹钟！");
		AlarmProviderFactory.closeAlarmList(this);
		super.onDestroy();
	}

	// TODO 定期检测闹钟执行情况

}
