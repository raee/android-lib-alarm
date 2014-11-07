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
	private static final String	TAG	= "AlarmService";
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
//	@SuppressWarnings("deprecation")
	@Override
	public void onCreate() {
		super.onCreate();
		Log.i(TAG, "闹钟服务开启！");
		AlarmProviderFactory.refreshAlarmList(getApplicationContext());
//		
//		// 通知。
//		Notification notification = new Notification();
//		notification.flags = Notification.FLAG_AUTO_CANCEL;
//		notification.tickerText = "闹钟提醒服务已开启！";
//		notification.when = System.currentTimeMillis();
//		notification.icon = getApplicationContext().getApplicationInfo().icon;
//		notification.setLatestEventInfo(getApplicationContext(), "闹钟提醒", "闹钟服务已经开启！", null);
//		
//		((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).notify(10021, notification);
		
	}
	
	@Override
	public void onDestroy() {
		Log.e(TAG, "闹钟服务被关闭，取消所有闹钟！");
		AlarmProviderFactory.closeAlarmList(this);
		super.onDestroy();
	}
	
	// TODO 定期检测闹钟执行情况
	
}
