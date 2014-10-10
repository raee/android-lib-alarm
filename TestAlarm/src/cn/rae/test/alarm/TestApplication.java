package cn.rae.test.alarm;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import com.rae.core.alarm.AlarmService;

public class TestApplication extends Application {
	@Override
	public void onCreate() {
		super.onCreate();
		Log.i("Application", "程序运行！");
		startService(new Intent(this, AlarmService.class)); // 开启服务
	}
}
