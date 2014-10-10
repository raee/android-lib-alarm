package com.rae.core.alarm;

import java.util.List;

/**
 * 操作闹钟数据库接口
 * 
 * @author ChenRui
 * 
 */
public interface IDbAlarm {
	/**
	 * 存在则更新，不存在则添加。
	 * 
	 * @param entity
	 * @return
	 */
	int addOrUpdate(AlarmEntity entity);
	
	/**
	 * 删除一个闹钟
	 * 
	 * @param entity
	 * @return
	 */
	boolean delete(AlarmEntity entity);
	
	/**
	 * 更新一个闹钟
	 * 
	 * @param entity
	 * @return
	 */
	boolean update(AlarmEntity entity);
	
	/**
	 * 获取所有闹钟
	 * 
	 * @return
	 */
	List<AlarmEntity> getAlarms();
	
	/**
	 * 根据主键获取一个闹钟。
	 * 
	 * @param id
	 * @return
	 */
	AlarmEntity getAlarm(int id);
	
}
