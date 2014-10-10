package com.rae.core.alarm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

public class AlarmDataBase extends SQLiteOpenHelper {

	private static final String TAG = "AlarmDataBase";
	private String table = "alarms";

	public AlarmDataBase(Context context) {
		super(context, "alarm.db", null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table alarms(id INTEGER NOT NULL,title nvarchar(255)not null default'闹钟',content text,time nvarchar(255)not null,nexttime nvarchar(255),cycle nvarchar(255)not null,ring nvarchar(255)not null,images nvarchar(255),sound nvarchar(255),state int not null default 0,timespan int default 0,otherparam nvarchar(255),weeks nvarchar(255),primary key(id));");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

	private ContentValues getAlarmContentValues(AlarmEntity entity) {
		ContentValues values = new ContentValues();
		values.put("title", entity.getTitle());
		values.put("content", entity.getContent());
		values.put("time", entity.getTime());
		values.put("nexttime", entity.getNextTime());
		values.put("cycle", entity.getCycle());
		values.put("ring", entity.getRing());
		values.put("images", entity.getImages());
		values.put("sound", entity.getSound());
		values.put("state", entity.getState());
		values.put("timespan", entity.getTimeSpan());
		values.put("otherparam", entity.getOtherParam());

		int[] weeks = entity.getWeeks();
		if (null != weeks && weeks.length > 0) {
			values.put("weeks", Arrays.toString(weeks));
		}
		return values;
	}

	/**
	 * @param entity
	 * @return 增加后的主键
	 */
	public int addOrUpdate(AlarmEntity entity) {
		// 主键形式。
		if (exits(entity)) {
			update(entity);
			return entity.getId();
		} else {

			SQLiteDatabase db = getWritableDatabase();
			int result = (int) db.insert(table, null, getAlarmContentValues(entity));
			if (result < 0) {
				Log.e(TAG, "插入失败：\n" + entity.toString());
			}
			db.close();
			return result;
		}
	}

	public boolean exits(AlarmEntity entity) {
		if (entity == null) {
			return false;
		}
		return getAlarm(entity.getId()) != null;
	}

	/**
	 * 删除
	 * 
	 * @param entity
	 * @return
	 */
	public boolean delete(AlarmEntity entity) {
		SQLiteDatabase db = getWritableDatabase();
		int result = db.delete(table, "id=?", new String[] { String.valueOf(entity.getId()) });
		if (result < 0) {
			Log.e(TAG, "删除失败：\n" + entity.toString());
		}
		db.close();
		return result > 0;
	}

	/**
	 * 更新
	 * 
	 * @param entity
	 * @return
	 */
	public boolean update(AlarmEntity entity) {
		SQLiteDatabase db = getWritableDatabase();
		int result = db.update(table, getAlarmContentValues(entity), "id=?", new String[] { String.valueOf(entity.getId()) });
		if (result < 0) {
			Log.e(TAG, "更新失败：\n" + entity.toString());
		}
		db.close();
		return result > 0;
	}

	public List<AlarmEntity> getAlarms() {
		List<AlarmEntity> result = new ArrayList<AlarmEntity>();
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from alarms", null);
		while (cursor.moveToNext()) {
			AlarmEntity entity = read(cursor);
			result.add(entity);
		}
		cursor.close();
		db.close();
		return result;
	}

	public AlarmEntity getAlarm(int id) {
		AlarmEntity entity = null;
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from alarms where id=?", new String[] { String.valueOf(id) });
		if (cursor.moveToNext()) {
			entity = read(cursor);
		}

		cursor.close();
		db.close();
		return entity;
	}

	private AlarmEntity read(Cursor cursor) {
		int id = cursor.getInt(cursor.getColumnIndex("id"));
		String title = cursor.getString(cursor.getColumnIndex("title"));
		String content = cursor.getString(cursor.getColumnIndex("content"));
		String time = cursor.getString(cursor.getColumnIndex("time"));
		String nextTime = cursor.getString(cursor.getColumnIndex("nexttime"));
		String cycle = cursor.getString(cursor.getColumnIndex("cycle"));
		String ring = cursor.getString(cursor.getColumnIndex("ring"));
		String images = cursor.getString(cursor.getColumnIndex("images"));
		String sound = cursor.getString(cursor.getColumnIndex("sound"));
		int state = cursor.getInt(cursor.getColumnIndex("state"));
		int timespan = cursor.getInt(cursor.getColumnIndex("timespan"));
		String otherParam = cursor.getString(cursor.getColumnIndex("otherparam"));
		String weeks = cursor.getString(cursor.getColumnIndex("weeks"));

		AlarmEntity entity = new AlarmEntity(cycle, title, time);
		entity.setId(id);
		entity.setContent(content);
		entity.setNextTime(nextTime);
		entity.setRing(ring);
		entity.setImages(images);
		entity.setSound(sound);
		entity.setState(state);
		entity.setTimeSpan(timespan);
		entity.setOtherParam(otherParam);

		// 数组转换
		if (!TextUtils.isEmpty(weeks)) {
			try {
				JSONArray arr = new JSONArray(weeks);
				int[] arrWeeks = new int[arr.length()];
				for (int i = 0; i < arr.length(); i++) {
					arrWeeks[i] = arr.getInt(i);
				}
				entity.setWeeks(arrWeeks);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		return entity;
	}
}
