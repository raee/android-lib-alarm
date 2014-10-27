package com.rae.core.alarm;

import java.util.Comparator;

/**
 * 闹钟时间比较
 * 
 * @author ChenRui
 * 
 */
class AlarmEntityComparator implements Comparator<AlarmEntity> {
	
	@Override
	public int compare(AlarmEntity lhs, AlarmEntity rhs) {
		// 比较时间。
		// 负数：l<r;0:l=r;正数：l>r
		
		long l = AlarmUtils.getTimeInMillis(lhs.getNextTime());
		
		long r = AlarmUtils.getTimeInMillis(rhs.getNextTime());
		
		if (l == r) {
			return 0;
		}
		else if (l < r) {
			return -1;
		}
		else {
			return 1;
		}
		
	}
}