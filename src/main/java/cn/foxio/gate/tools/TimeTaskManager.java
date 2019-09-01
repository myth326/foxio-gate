package cn.foxio.gate.tools;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;

public class TimeTaskManager {

	static private TimeTaskManager instance;

	static public TimeTaskManager getInstance() {
		if (instance == null)
			instance = new TimeTaskManager();
		return instance;
	}

	private Scheduler scheduler = null;

	public TimeTaskManager() {
		init();
	}

	private void init() {

		try {
			scheduler = StdSchedulerFactory.getDefaultScheduler();

			// scheduler.scheduleJob(job, trigger);

			scheduler.start();

			// job.getJobDataMap().put("default", new TimeJob())

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private AtomicInteger taskId = new AtomicInteger(1000);

	/**
	 * 定时回调
	 * 
	 * @param callBack
	 *            回调方法
	 * @param param
	 *            回调方法的参数
	 * @param dealy
	 *            延时[毫秒]
	 */
	public JobDataMap addTask(Consumer<Object> callBack, Object param, long dealy) {

		final int id = taskId.get() + 1;
		taskId.set(id);
		if (id > Integer.MAX_VALUE - 5000) {
			taskId.set(1000);
		}

		String jobName = "default-" + id;
		String groupName = "testGroup";

		HashMap<String, Object> map = new HashMap<>();
		map.put("callBack", callBack);
		map.put("param", param);
		map.put("id", id);
		map.put("jobName", jobName);
		map.put("groupName", groupName);

		JobDataMap jobDataMap = new JobDataMap(map);

		JobDetail job = JobBuilder.newJob(TimeTask.class).withIdentity(jobName, groupName).usingJobData(jobDataMap)
				.build();
		try {

			Trigger trigger = TriggerBuilder.newTrigger().withIdentity(jobName, groupName)
					.withSchedule(SimpleScheduleBuilder
							// 仅执行1次
							.repeatHourlyForTotalCount(1))
					.forJob(jobName, groupName).startAt(new Date(System.currentTimeMillis() + dealy))
					// .startNow()
					.build();

			scheduler.scheduleJob(job, trigger);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return jobDataMap;
	}

	/**
	 * 删除任务
	 * 
	 * @param jobDataMap
	 * @throws SchedulerException
	 */
	public void removeTask(JobDataMap jobDataMap) {
		String jobName = jobDataMap.getString("jobName");
		String groupName = jobDataMap.getString("groupName");
		TriggerKey triggerKey = TriggerKey.triggerKey(jobName, groupName);
		// 停止触发器
		try {
			scheduler.pauseTrigger(triggerKey);

			// 移除触发器
			scheduler.unscheduleJob(triggerKey);
			// 删除任务
			scheduler.deleteJob(JobKey.jobKey(jobName, groupName));

			scheduler.deleteJob(JobKey.jobKey(jobDataMap.getString("jobName"), jobDataMap.getString("groupName")));
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {

		List<JobDataMap> list = new ArrayList<>();

		for (int i = 0; i < 30; i++) {

			JobDataMap data = TimeTaskManager.getInstance().addTask(o -> {

				System.out.println("时间到了! params " + o.toString());

			}, String.valueOf(" id - " + i), i * 500L);
			list.add(data);

			if (i % 2 == 0) {
				TimeTaskManager.getInstance().removeTask(data);
			}
		}

	}

}
