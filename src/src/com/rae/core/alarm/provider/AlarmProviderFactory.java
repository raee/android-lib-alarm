package com.rae.core.alarm.provider;

import java.util.List;

import android.content.Context;
import android.util.Log;

import com.rae.core.alarm.AlarmDataBase;
import com.rae.core.alarm.AlarmEntity;
import com.rae.core.alarm.IDbAlarm;

/**
 * 获取闹钟操作实例
 * 
 * @author ChenRui
 * 
 */
public final class AlarmProviderFactory {
	private static final String	TAG	= "AlarmProviderFactory";
	
	/**
	 * 获取闹钟实例对象
	 * 
	 * @param type
	 *            类型，为{@link AlarmEntity} getCycle() 的周期类型。
	 * @return
	 */
	public static AlarmProvider getProvider(Context context, AlarmEntity entity) {
		String type = entity.getCycle();
		AlarmProvider provider = null;
		
		if (AlarmEntity.TYPE_REPEAT_EVERY_ONE_TIME.equals(type)) { // 重复闹钟
			provider = new EveryOneTimeRepeatProvider(context, entity);
		}
		else if (AlarmEntity.TYPE_REPEAT_EVERY_DAY.equals(type)) { // 每天重复闹钟
			provider = new EveryDayRepeatAlarmProvider(context, entity);
		}
		else if (AlarmEntity.TYPE_REPEAT_EVERY_WEEK.equals(type)) {// 每周几重复闹钟
			provider = new EveryWeekRepeatProvider(context, entity);
		}
		else {// 单次闹钟
			provider = new OnceAlarmProvider(context, entity);
		}
		return provider;
	}
	
	/**
	 * 刷新数据库中 闹钟
	 * 
	 * @param context
	 */
	public static List<AlarmEntity> refreshAlarmList(Context context) {
		AlarmDataBase db = new AlarmDataBase(context);
		List<AlarmEntity> lists = db.getAlarms();
		for (AlarmEntity entity : lists) {
			AlarmProvider provider = getProvider(context, entity);
			provider.cancle();// 关掉之前的闹钟
			provider.create(); // 重新开启闹钟
			Log.i(TAG, "重新更新了闹钟状态！\n" + entity.toString());
			provider = null;
		}
		db.close();
		System.gc();
		return lists;
	}
	
	/**
	 * 关闭所有闹钟。
	 * 
	 * @param context
	 */
	public static void closeAlarmList(Context context) {
		// 更新所有的闹钟为关闭状态
		AlarmDataBase db = new AlarmDataBase(context);
		List<AlarmEntity> lists = db.getAlarms();
		for (AlarmEntity entity : lists) {
			
			AlarmProvider provider = AlarmProviderFactory.getProvider(context, entity);
			provider.cancle(); // 取消所有闹钟
			provider = null;
			
			// 更新状态
			entity.setState(AlarmEntity.STATUS_CLOSE);
			db.update(entity);
		}
		db.close();
		System.gc();
	}
	
//	public static AlarmEntity getAlarmEntityById(Context context, int id) {
//		if (id == 0) { return null; }
//		AlarmDataBase db = new AlarmDataBase(context);
//		AlarmEntity result = db.getAlarm(id);
//		db.close();
//		return result;
//	}
	
	public static IDbAlarm getDbAlarm(Context context){
		return new AlarmDataBase(context);
	}
}
