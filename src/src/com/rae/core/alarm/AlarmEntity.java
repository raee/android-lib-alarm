package com.rae.core.alarm;

import android.media.RingtoneManager;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

/**
 * 闹钟实体
 * 
 * @author ChenRui
 * 
 */
public class AlarmEntity implements Parcelable {
	/**
	 * 一天
	 */
	public final static int TIME_OF_DAY = 86400000;
	/**
	 * 一周
	 */
	public final static int TIME_OF_WEEK = 604800000;

	/**
	 * 闹钟正在运行状态
	 */
	public static final int STATUS_RUNING = 0;
	/**
	 * 闹钟处于错过状态，一般地由于程序被关闭或者服务未启动导致。
	 */
	public static final int STATUS_MISS = -1;

	/**
	 * 闹钟处于关闭状态，一般是由于服务关闭或者用户自动停止
	 */
	public static final int STATUS_CLOSE = -2;

	/**
	 * 闹钟处于等待下一次响铃状态，一般用于闹钟更新下一次响铃时间时。
	 */
	public static final int STATUS_WAITING = 1;

	public static final String TYPE_ONCE = "TYPE_ONCE"; // 一次闹钟
	public static final String TYPE_REPEAT_EVERY_ONE_TIME = "TYPE_REPEAT_EVERY_ONE_TIME"; // 每N间隔重复1次
	public static final String TYPE_REPEAT_EVERY_DAY = "TYPE_REPEAT_EVERY_DAY"; // 每天重复
	public static final String TYPE_REPEAT_EVERY_WEEK = "TYPE_REPEAT_EVERY_WEEK"; // 每周几重复

	public static Creator<AlarmEntity> CREATOR = new Creator<AlarmEntity>() {

		@Override
		public AlarmEntity[] newArray(int size) {
			return new AlarmEntity[size];
		}

		@Override
		public AlarmEntity createFromParcel(Parcel source) {
			return new AlarmEntity(source);
		}
	};

	private int id; // 闹钟Id
	private String title; // 提醒标题
	private String content; // 提醒内容
	private String time; // 闹铃时间：时分秒
	private String nextTime; // 下次闹铃时间。
	private String cycle; // 周期：一次、重复；
	private String ring; // 铃声
	private String images; // 图片组
	private String sound; // 语音
	private int state; // 闹钟状态：正常、关闭、删除
	private String otherParam; // 其他参数，key,value 的形式。
	private int timeSpan; // 间隔，如：1天1次、3天1次。单位为：毫秒。
	private int[] weeks; // 重复周期。设置周期设置为：周几重复才生效。

	public AlarmEntity(String cycle, String title, String time) {
		setCycle(cycle);
		setTitle(title);
		setTime(time);
	}

	public AlarmEntity(Parcel source) {
		id = source.readInt();
		title = source.readString();
		content = source.readString();
		time = source.readString();
		nextTime = source.readString();
		cycle = source.readString();
		ring = source.readString();
		images = source.readString();
		sound = source.readString();
		state = source.readInt();
		timeSpan = source.readInt();
		otherParam = source.readString();
		source.readIntArray(weeks);
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(id);
		dest.writeString(title);
		dest.writeString(content);
		dest.writeString(time);
		dest.writeString(nextTime);
		dest.writeString(cycle);
		dest.writeString(ring);
		dest.writeString(images);
		dest.writeString(sound);
		dest.writeInt(state);
		dest.writeInt(timeSpan);
		dest.writeString(otherParam);
		dest.writeIntArray(weeks);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getNextTime() {
		return nextTime;
	}

	public void setNextTime(String nextTime) {
		this.nextTime = nextTime;
	}

	public void setNextTime(long time) {
		this.nextTime = AlarmUtils.getDateByTimeInMillis(time);
	}

	public String getCycle() {
		return cycle;
	}

	/**
	 * 设置周期类型，参考静态变量中的TYPE_??
	 * 
	 * @param cycle
	 */
	public void setCycle(String cycle) {
		this.cycle = cycle;
	}

	public String getRing() {
		if (TextUtils.isEmpty(ring)) {
			// 获取系统自带的铃声
			ring = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM).toString();
		}
		return ring;
	}

	public void setRing(String ring) {
		this.ring = ring;
	}

	public String getImages() {
		return images;
	}

	public void setImages(String images) {
		this.images = images;
	}

	public String getSound() {
		return sound;
	}

	public void setSound(String sound) {
		this.sound = sound;
	}

	public String getOtherParam() {
		return otherParam;
	}

	public void setOtherParam(String otherParam) {
		this.otherParam = otherParam;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public int getTimeSpan() {
		if (timeSpan == 0) {
			timeSpan = TIME_OF_DAY; // 默认为1天
		}
		return timeSpan;
	}

	public void setTimeSpan(int timeSpan) {
		this.timeSpan = timeSpan;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(id);
		sb.append("|");
		sb.append(title);
		sb.append("|");
		sb.append(content);
		sb.append("|");
		sb.append(time);
		sb.append("|");
		sb.append(nextTime);
		sb.append("|");
		sb.append(cycle);
		sb.append("|");
		sb.append(ring);
		sb.append("|");
		sb.append(images);
		sb.append("|");
		sb.append(sound);
		sb.append("|");
		sb.append(state);
		sb.append("|");
		sb.append(otherParam);
		sb.append("|");
		return sb.toString();
	}

	public int[] getWeeks() {
		return weeks;
	}

	public void setWeeks(int[] weeks) {
		this.weeks = weeks;
	}

}
