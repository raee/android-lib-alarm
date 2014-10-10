package cn.rae.test.alarm.junit;

import java.util.List;

import com.rae.core.alarm.AlarmDataBase;
import com.rae.core.alarm.AlarmEntity;

public class TestDatabase extends TestBase {

	private AlarmDataBase db;
	private AlarmEntity entity = new AlarmEntity(AlarmEntity.TYPE_REPEAT_EVERY_ONE_TIME, "一个闹钟标题！", "2012-12-12 00:00:00");

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		db = new AlarmDataBase(mContext);
	}

	public void add() {
		db.addOrUpdate(entity);
		selectAll();
	}

	public void update() {
		entity.setTitle("更新后的一个闹钟");
		db.update(entity);
		selectAll();
	}

	public void delete() {
		for (int i = 1; i <= 4; i++) {
			entity.setId(i);
			db.delete(entity);
		}
		selectAll();
	}

	public void deleteAll() {
		List<AlarmEntity> lists = db.getAlarms();
		for (AlarmEntity entity : lists) {
			db.delete(entity);
		}
	}

	public void selectAll() {
		List<AlarmEntity> datas = db.getAlarms();
		for (AlarmEntity entity : datas) {
			show(entity);
		}
	}

	public void select() {
		AlarmEntity entity = db.getAlarm(1);
		show(entity);
	}
}
