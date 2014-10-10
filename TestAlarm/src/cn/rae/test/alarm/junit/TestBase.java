package cn.rae.test.alarm.junit;

import android.test.AndroidTestCase;
import android.util.Log;

public class TestBase extends AndroidTestCase {
	public void show(Object msg) {
		Log.i("AlarmTest", msg == null ? "--" : msg.toString());
	}

	public void error(Object msg) {
		Log.e("AlarmTest", msg == null ? "--" : msg.toString());
	}

	public void error(Exception e) {
		e.printStackTrace();
		String msg = e.getMessage();
		Log.e("AlarmTest", msg == null ? "--" : msg.toString());
	}
}
