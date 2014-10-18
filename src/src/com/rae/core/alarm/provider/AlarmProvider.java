package com.rae.core.alarm.provider;

import java.util.Calendar;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.rae.core.alarm.AlarmDataBase;
import com.rae.core.alarm.AlarmEntity;
import com.rae.core.alarm.AlarmException;
import com.rae.core.alarm.AlarmUtils;
import com.rae.core.alarm.IDbAlarm;

/**
 * 闹钟提供者
 * 
 * @author ChenRui
 * 
 */
public abstract class AlarmProvider {
	/**
	 * 闹钟响铃广播
	 */
	public static final String		ACTION_ALARM_WAKEUP	= "com.rae.core.alarm.action.wakeup";
	
	/**
	 * 广播数据：闹钟唯一主键标志
	 */
	public static final String		EXTRA_ALARM_ID		= "com.rae.core.alarm.extra.alarm.id";
	
	protected static final String	TAG					= "AlarmProvider";
	
	protected Context				mContext;
	private String					mName;														// 闹钟提供者名称，如一次闹钟，重复闹钟，用于标志。
	protected AlarmEntity			mAlarmEntity;												// 闹钟实体
	private AlarmManager			mAlarmManager;												// 系统闹钟管理对象
	private IDbAlarm				mDb;
	protected Calendar				calendar			= Calendar.getInstance();
	
	public AlarmProvider(Context context, AlarmEntity entity) {
		mContext = context;
		setAlarmEntity(entity);
		mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
	}
	
	protected IDbAlarm getDatabase() {
		if (mDb == null) {
			mDb = new AlarmDataBase(mContext);
		}
		return mDb;
	}
	
	/**
	 * 创建闹钟，由于不同类型的闹钟创建都各部相同，所以抽象。
	 */
	public abstract AlarmEntity create();
	
	/**
	 * 在数据库中创建一个闹钟，如果存在则更新，不存在则创建。
	 */
	protected void createInDatabase() {
		// 插入到数据库中
		if (mAlarmEntity.getId() == 0) {
			int id = getDatabase().addOrUpdate(mAlarmEntity);
			mAlarmEntity.setId(id);
		}
		else {
			getDatabase().update(mAlarmEntity);
		}
	}
	
	/**
	 * 当闹钟被执行时，更新当前闹钟，闹钟需重新响铃计算时间，或者删除闹钟。
	 * 
	 * @param entity
	 */
	public abstract void update();
	
	/**
	 * 跳过当前闹钟，取消当前闹钟，并更新下次响铃时间。
	 */
	public abstract void skip();
	
	/**
	 * 获取下次响铃时间。
	 * 
	 * @return
	 */
	public abstract long getNextAlarmTime(long currentTimeAtMillis);
	
	/**
	 * 取消（或关闭）当前闹钟。闹钟将不会再执行，也不会计算下次闹铃时间。
	 */
	public void cancle() {
		mAlarmManager.cancel(getOperation(mAlarmEntity.getId()));
		Log.w(TAG, "---> 闹钟被取消  <-----\n" + mAlarmEntity.toString());
	}
	
	/**
	 * 设置闹钟实体
	 * 
	 * @param entity
	 */
	public void setAlarmEntity(AlarmEntity entity) {
		mAlarmEntity = entity;
	}
	
	/**
	 * 获取闹钟实体
	 * 
	 * @return
	 */
	public AlarmEntity getAlarmEntity() {
		return mAlarmEntity;
	}
	
	/**
	 * 获取闹钟名称
	 * 
	 * @return
	 */
	public String getName() {
		return mName;
	}
	
	/**
	 * 添加闹钟参数
	 * 
	 * @param name
	 * @param value
	 */
	public void putValue(String name, Object value) {
		try {
			String json = mAlarmEntity.getOtherParam();
			JSONObject jsonObj;
			if (TextUtils.isEmpty(json)) {
				jsonObj = new JSONObject();
			}
			else {
				jsonObj = new JSONObject(json);
			}
			jsonObj.put(name, value.toString());
			mAlarmEntity.setOtherParam(jsonObj.toString());
		}
		catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取闹钟参数
	 * 
	 * @param name
	 * @return
	 */
	public String getValue(String name) {
		String json = mAlarmEntity.getOtherParam();
		if (!TextUtils.isEmpty(json)) {
			try {
				JSONObject obj = new JSONObject(json);
				return obj.getString(name);
			}
			catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return "";
	}
	
	/**
	 * 设置单次闹钟，设置前会更新数据库中 的实体，以获取主键。
	 * 
	 * @param date
	 *            响铃时间
	 */
	protected boolean set(String date) {
		if (TextUtils.isEmpty(date)) {
			onAlarmError(new AlarmException("一次性闹钟开始时间不能为空！"));
			return false;
		}
		long time = AlarmUtils.getTimeInMillis(date);
		return set(time);
	}
	
	/**
	 * 检查时间是否已经过期，如果过期返回下次响铃时间。
	 * 
	 * @param triggerAtMillis
	 *            需要检查的时间
	 * @return
	 */
	protected long checkOutOfTime(long triggerAtMillis) {
		
		if (triggerAtMillis < System.currentTimeMillis()) {
			Log.e(TAG, "设定的响铃时间已经过期！" + AlarmUtils.getDateByTimeInMillis(triggerAtMillis));
			triggerAtMillis = getNextAlarmTime(triggerAtMillis); // 获取下次闹铃日期
			Log.i(TAG, "过期时间更改为：" + AlarmUtils.getDateByTimeInMillis(triggerAtMillis));
		}
		
		return triggerAtMillis;
	}
	
	/**
	 * 设置单次闹钟，设置前会更新数据库中 的实体，以获取主键。
	 * 
	 * @param triggerAtMillis
	 * @return
	 */
	protected boolean set(long triggerAtMillis) {
		String date = AlarmUtils.getDateByTimeInMillis(triggerAtMillis);
		triggerAtMillis = checkOutOfTime(triggerAtMillis); // 检查时间是否过期
		if (triggerAtMillis <= 0 || triggerAtMillis == AlarmUtils.getTimeInMillis(mAlarmEntity.getTime())) {
			onAlarmError(new AlarmException("下次响铃时间不能为0或者是上次时间！错误时间：" + triggerAtMillis));
			return false;
		}
		createInDatabase();
		mAlarmManager.set(AlarmManager.RTC_WAKEUP, triggerAtMillis, getOperation(mAlarmEntity.getId()));
		mAlarmEntity.setNextTime(triggerAtMillis); // 设置下次启动时间为自己。
		mAlarmEntity.setState(AlarmEntity.STATUS_RUNING);
		getDatabase().update(mAlarmEntity); // 更新数据库
		onAlarmSet(mAlarmEntity, date, false); // 通知
		return true;
	}
	
	/**
	 * 设置重复闹钟，设置前会更新数据库中 的实体，以获取主键。
	 * 
	 * @param triggerAtMillis
	 *            开始时间
	 * @param secound
	 *            重复间隔，如：每天重复24*60*60*1000
	 */
	protected void setRepeat(long triggerAtMillis, long intervalMillis) {
		createInDatabase();
		mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, triggerAtMillis, intervalMillis, getOperation(mAlarmEntity.getId()));
		String date = AlarmUtils.getDateByTimeInMillis(triggerAtMillis); // 开始时间
		mAlarmEntity.setNextTime(triggerAtMillis); // 设置下次启动日期为自己
		mAlarmEntity.setState(AlarmEntity.STATUS_RUNING); // 更新闹钟状态
		getDatabase().update(mAlarmEntity);
		onAlarmSet(mAlarmEntity, date, true);
	}
	
	/**
	 * 获取响铃时的意图
	 * 
	 * @return
	 */
	public PendingIntent getOperation(int id) {
		if (id == 0) { throw new AlarmException("闹钟唯一主键不能为0，请设置主键：entity.setId();"); }
		Intent intent = new Intent(ACTION_ALARM_WAKEUP);
		intent.putExtra(EXTRA_ALARM_ID, id); // 主键
		return PendingIntent.getBroadcast(mContext, id, intent, PendingIntent.FLAG_CANCEL_CURRENT);
	}
	
	/**
	 * 将时间格式为：08:00 的转换位当前日期的时间如：2014-10-05 08:00:00。
	 * 
	 * @param time
	 *            时间格式为：HH:mm
	 * @return
	 */
	protected long converTime(long startTimeMillis, String time) {
		int hour = Integer.valueOf(time.split(":")[0]);
		int minute = Integer.valueOf(time.split(":")[1]);
		
		// 转换为当前时间
		startTimeMillis = startTimeMillis < 1 ? System.currentTimeMillis() : startTimeMillis;
		calendar.setTimeInMillis(startTimeMillis);
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, 0);
		long result = calendar.getTimeInMillis();
		calendar.clear();
		return result;
	}
	
	/**
	 * 当闹钟发生错误是调用
	 * 
	 * @param e
	 *            异常信息
	 */
	protected void onAlarmError(AlarmException e) {
		Log.e(TAG, e.getMessage());
	}
	
	/**
	 * 当闹钟被启动时调用
	 * 
	 * @param entity
	 *            闹钟实体
	 * @param date
	 *            响铃日期
	 * @param isRepeat
	 *            是否重复
	 */
	protected void onAlarmSet(AlarmEntity entity, String date, boolean isRepeat) {
		Toast.makeText(mContext, "闹钟距离现在还有："+getNextAlarmTimeSpan(), Toast.LENGTH_LONG).show();
		Log.i(TAG, "---- 设置闹钟：" + entity.getTitle() + date + "----------");
	}
	
	/**
	 * 获取下次响铃日期距离现在还有多久，单位：秒。
	 */
	public String getNextAlarmTimeSpan(){
		return AlarmUtils.getNextTimeSpanString(mAlarmEntity.getNextTime());
	}
}
