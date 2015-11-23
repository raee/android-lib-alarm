package com.rae.core.alarm.provider;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.rae.core.alarm.AlarmCallbackListener;
import com.rae.core.alarm.AlarmDataBase;
import com.rae.core.alarm.AlarmEntity;
import com.rae.core.alarm.AlarmException;
import com.rae.core.alarm.AlarmUtils;
import com.rae.core.alarm.IDbAlarm;

/**
 * �����ṩ��
 * 
 * @author ChenRui
 * 
 */
public abstract class AlarmProvider
{
	/**
	 * ��������㲥
	 */
	public static final String		ACTION_ALARM_WAKEUP	= "com.rae.core.alarm.action.wakeup";
	
	/**
	 * �㲥���ݣ�����Ψһ������־
	 */
	public static final String		EXTRA_ALARM_ID		= "com.rae.core.alarm.extra.alarm.id";
	
	protected static final String	TAG					= "AlarmProvider";
	
	protected Context				mContext;
	private String					mName;														// �����ṩ�����ƣ���һ�����ӣ��ظ����ӣ����ڱ�־��
	protected AlarmEntity			mAlarmEntity;												// ����ʵ��
	private AlarmManager			mAlarmManager;												// ϵͳ���ӹ������
	private IDbAlarm				mDb;
	protected Calendar				calendar			= Calendar.getInstance();
	private AlarmCallbackListener	mListener;
	
	public AlarmProvider(Context context, AlarmEntity entity)
	{
		mContext = context;
		setAlarmEntity(entity);
		mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
	}
	
	protected IDbAlarm getDatabase()
	{
		if (mDb == null)
		{
			mDb = new AlarmDataBase(mContext);
		}
		return mDb;
	}
	
	/**
	 * �������Ӽ���
	 * 
	 * @param listener
	 */
	public void setAlarmListener(AlarmCallbackListener listener)
	{
		this.mListener = listener;
	}
	
	/**
	 * �������ӣ����ڲ�ͬ���͵����Ӵ�����������ͬ�����Գ���
	 */
	public abstract AlarmEntity create();
	
	/**
	 * �����ݿ��д���һ�����ӣ������������£��������򴴽���
	 */
	protected void createInDatabase()
	{
		// ���뵽���ݿ���
		if (mAlarmEntity.getId() == 0)
		{
			int id = getDatabase().addOrUpdate(mAlarmEntity);
			mAlarmEntity.setId(id);
		}
		else
		{
			getDatabase().update(mAlarmEntity);
		}
	}
	
	/**
	 * �����ӱ�ִ��ʱ�����µ�ǰ���ӣ������������������ʱ�䣬����ɾ�����ӡ�
	 * 
	 * @param entity
	 */
	public abstract void update();
	
	/**
	 * ������ǰ���ӣ�ȡ����ǰ���ӣ��������´�����ʱ�䡣
	 */
	public abstract void skip();
	
	/**
	 * ��ȡ�´�����ʱ�䡣
	 * 
	 * @return
	 */
	public abstract long getNextAlarmTime(long currentTimeAtMillis);
	
	/**
	 * ȡ������رգ���ǰ���ӡ����ӽ�������ִ�У�Ҳ��������´�����ʱ�䡣
	 */
	public void cancle()
	{
		mAlarmManager.cancel(getOperation(mAlarmEntity.getId()));
		Log.w(TAG, "---> ���ӱ�ȡ��  <-----\n" + mAlarmEntity.toString());
	}
	
	public void delete()
	{
		cancle();
		getDatabase().delete(mAlarmEntity);
	}
	
	/**
	 * ��������ʵ��
	 * 
	 * @param entity
	 */
	public void setAlarmEntity(AlarmEntity entity)
	{
		mAlarmEntity = entity;
	}
	
	/**
	 * ��ȡ����ʵ��
	 * 
	 * @return
	 */
	public AlarmEntity getAlarmEntity()
	{
		return mAlarmEntity;
	}
	
	/**
	 * ��ȡ��������
	 * 
	 * @return
	 */
	public String getName()
	{
		return mName;
	}
	
	/**
	 * ���õ������ӣ�����ǰ��������ݿ��� ��ʵ�壬�Ի�ȡ������
	 * 
	 * @param date
	 *            ����ʱ��
	 */
	protected boolean set(String date)
	{
		if (TextUtils.isEmpty(date))
		{
			onAlarmError(new AlarmException("һ�������ӿ�ʼʱ�䲻��Ϊ�գ�"));
			return false;
		}
		long time = AlarmUtils.getTimeInMillis(date);
		return set(time);
	}
	
	/**
	 * ���ʱ���Ƿ��Ѿ����ڣ�������ڷ����´�����ʱ�䡣
	 * 
	 * @param triggerAtMillis
	 *            ��Ҫ����ʱ��
	 * @return
	 */
	protected long checkOutOfTime(long triggerAtMillis)
	{
		
		if (triggerAtMillis < System.currentTimeMillis())
		{
			String errorTime = AlarmUtils.getDateByTimeInMillis(triggerAtMillis);
			triggerAtMillis = getNextAlarmTime(triggerAtMillis); // ��ȡ�´���������
			String newTime = AlarmUtils.getDateByTimeInMillis(triggerAtMillis);
			onAlarmError(new AlarmException("�趨������ʱ�䣨" + errorTime + "���Ѿ����ڣ�ʱ�����Ϊ��" + newTime));
		}
		
		return triggerAtMillis;
	}
	
	/**
	 * ���õ������ӣ�����ǰ��������ݿ��� ��ʵ�壬�Ի�ȡ������
	 * 
	 * @param triggerAtMillis
	 * @return
	 */
	protected boolean set(long triggerAtMillis)
	{
		String date = AlarmUtils.getDateByTimeInMillis(triggerAtMillis);
		triggerAtMillis = checkOutOfTime(triggerAtMillis); // ���ʱ���Ƿ����
		if (triggerAtMillis <= 0 || triggerAtMillis < AlarmUtils.getTimeInMillis(mAlarmEntity.getTime()))
		{
			onAlarmError(new AlarmException("�´�����ʱ�䲻��Ϊ0����С���ϴ�ʱ�䣡����ʱ�䣺" + date));
			return false;
		}
		createInDatabase();
		mAlarmManager.set(AlarmManager.RTC_WAKEUP, triggerAtMillis, getOperation(mAlarmEntity.getId()));
		mAlarmEntity.setNextTime(triggerAtMillis); // �����´�����ʱ��Ϊ�Լ���
		mAlarmEntity.setState(AlarmEntity.STATUS_RUNING);
		getDatabase().update(mAlarmEntity); // �������ݿ�
		onAlarmSet(mAlarmEntity, date, false); // ֪ͨ
		return true;
	}
	
	/**
	 * �����ظ����ӣ�����ǰ��������ݿ��� ��ʵ�壬�Ի�ȡ������
	 * 
	 * @param triggerAtMillis
	 *            ��ʼʱ��
	 * @param secound
	 *            �ظ�������磺ÿ���ظ�24*60*60*1000
	 */
	protected void setRepeat(long triggerAtMillis, long intervalMillis)
	{
		createInDatabase();
		mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, triggerAtMillis, intervalMillis, getOperation(mAlarmEntity.getId()));
		String date = AlarmUtils.getDateByTimeInMillis(triggerAtMillis); // ��ʼʱ��
		mAlarmEntity.setNextTime(triggerAtMillis); // �����´���������Ϊ�Լ�
		mAlarmEntity.setState(AlarmEntity.STATUS_RUNING); // ��������״̬
		getDatabase().update(mAlarmEntity);
		onAlarmSet(mAlarmEntity, date, true);
	}
	
	/**
	 * ��ȡ����ʱ����ͼ
	 * 
	 * @return
	 */
	public PendingIntent getOperation(int id)
	{
		if (id == 0) { throw new AlarmException("����Ψһ��������Ϊ0��������������entity.setId();"); }
		Intent intent = new Intent(ACTION_ALARM_WAKEUP);
		intent.putExtra(EXTRA_ALARM_ID, id); // ����
		return PendingIntent.getBroadcast(mContext, id, intent, PendingIntent.FLAG_CANCEL_CURRENT);
	}
	
	/**
	 * ��ʱ���ʽΪ��08:00 ��ת��λ��ǰ���ڵ�ʱ���磺2014-10-05 08:00:00��
	 * 
	 * @param time
	 *            ʱ���ʽΪ��HH:mm
	 * @return
	 */
	protected long converTime(long startTimeMillis, String time)
	{
		int hour = Integer.valueOf(time.split(":")[0]);
		int minute = Integer.valueOf(time.split(":")[1]);
		
		// ת��Ϊ��ǰʱ��
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
	 * �����ӷ��������ǵ���
	 * 
	 * @param e
	 *            �쳣��Ϣ
	 */
	protected void onAlarmError(AlarmException e)
	{
		if (mListener != null)
		{
			mListener.onAlarmError(e);
		}
	}
	
	protected void onAlarmCancle()
	{
		if (mListener != null)
		{
			mListener.onAlarmCancle(this, mAlarmEntity);
		}
	}
	
	protected void onAlarmUpdate(String lastTime, String nextTime)
	{
		if (mListener != null)
		{
			mListener.onAlarmUpdate(this, mAlarmEntity, lastTime, nextTime);
		}
	}
	
	protected void onAlarmDelete()
	{
		if (mListener != null)
		{
			mListener.onAlarmDelete(this, mAlarmEntity);
		}
	}
	
	/**
	 * �����ӱ�����ʱ����
	 * 
	 * @param entity
	 *            ����ʵ��
	 * @param date
	 *            ��������
	 * @param isRepeat
	 *            �Ƿ��ظ�
	 */
	@SuppressWarnings("deprecation")
	protected void onAlarmSet(AlarmEntity entity, String date, boolean isRepeat)
	{
		String timeTips = getNextAlarmTimeSpan();
		Log.i(TAG, "---- �������ӣ�" + entity.getTitle() + date + "----------");
		if (mListener != null)
		{
			mListener.onAlarmCreated(this, mAlarmEntity, timeTips);
		}
		
		// ֪ͨ��
		//		Intent intent = new Intent();
		//		intent.setPackage(mContext.getPackageName());
		//		intent.setClassName(mContext, "");
		//	
		
		//		Notification notification = new Notification();
		//		notification.defaults = Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE;
		//		notification.flags = Notification.FLAG_AUTO_CANCEL;
		//		notification.tickerText = entity.getTitle() + "������ʱ�䣺" + timeTips;
		//		notification.when = System.currentTimeMillis();
		//		notification.icon = mContext.getApplicationInfo().icon;
		//		notification.setLatestEventInfo(mContext, entity.getTitle(), "�������ڻ��У�" + timeTips, PendingIntent.getActivity(mContext, 0, null, 0));
		//		
		//		((NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE)).notify(10021, notification);
		
		Toast.makeText(mContext, entity.getTitle() + "���������ڻ��У�" + timeTips, Toast.LENGTH_LONG).show();
	}
	
	/**
	 * ��ȡ�´��������ھ������ڻ��ж�ã���λ���롣
	 */
	public String getNextAlarmTimeSpan()
	{
		return AlarmUtils.getNextTimeSpanString(mAlarmEntity.getNextTime());
	}
}
