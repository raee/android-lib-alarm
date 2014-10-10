package cn.rae.test.alarm;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.rae.core.alarm.AlarmDataBase;
import com.rae.core.alarm.AlarmEntity;
import com.rae.core.alarm.AlarmUtils;
import com.rae.core.alarm.provider.AlarmProvider;
import com.rae.core.alarm.provider.AlarmProviderFactory;

@SuppressLint("SimpleDateFormat")
public class MainActivity extends Activity implements OnClickListener {

	private EditText mTimeText;
	private ListView mListView;
	AlarmDataBase db;
	private List<AlarmEntity> mDataList;
	private SimpleAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		db = new AlarmDataBase(this);
		mListView = (ListView) findViewById(R.id.listView1);
		Button btn1 = (Button) findViewById(R.id.btnOnce);
		btn1.setOnClickListener(this);
		findViewById(R.id.btnRepeat).setOnClickListener(this);
		Button btnWeek = (Button) findViewById(R.id.btnWeek);
		btnWeek.setOnClickListener(this);

		this.mTimeText = (EditText) findViewById(R.id.editText1);
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.SECOND, 3);
		CharSequence text = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(cal.getTime());
		this.mTimeText.setText(text);
		update();

		btnWeek.performClick();
	}

	public void update() {
		mDataList = db.getAlarms();
		adapter = new SimpleAdapter(this, getData(), R.layout.items, new String[] { "tv" }, new int[] { R.id.textView1 });
		mListView.setAdapter(adapter);
	}

	private List<? extends Map<String, ?>> getData() {
		List<HashMap<String, String>> reslult = new ArrayList<HashMap<String, String>>();
		for (AlarmEntity entity : mDataList) {
			HashMap<String, String> object = new HashMap<String, String>();
			object.put("tv", entity.toString());
			reslult.add(object);
		}
		return reslult;
	}

	@Override
	public void onClick(View v) {
		String time = mTimeText.getText().toString().trim();
		AlarmEntity entity = null;
		Calendar cal = Calendar.getInstance();
		cal.setTime(AlarmUtils.parseDate(time));
		cal.set(Calendar.SECOND, 0); // 00秒
		switch (v.getId()) {

		// 重复
		case R.id.btnRepeat:
			time = AlarmUtils.dateToString("HH:mm", cal.getTime());
			entity = new AlarmEntity(AlarmEntity.TYPE_REPEAT_EVERY_DAY, "每天重复闹钟", time);
			break;

		// 周几
		case R.id.btnWeek:
			cal.set(Calendar.HOUR_OF_DAY, 8);
			cal.set(Calendar.MINUTE, 0);
			time = AlarmUtils.dateToString("HH:mm", cal.getTime());
			entity = new AlarmEntity(AlarmEntity.TYPE_REPEAT_EVERY_WEEK, "缝周1、3、5重复闹钟", time);
			entity.setWeeks(new int[] { 1, 3, 5 });
			break;

		// 单次
		case R.id.btnOnce:
		default:
			entity = new AlarmEntity(AlarmEntity.TYPE_ONCE, "单次闹钟", time);
			break;
		}

		AlarmProvider provider = AlarmProviderFactory.getProvider(this, entity);
		provider.create();
		update();
	}
}
