package cn.foxio.gate.tools;


import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

import cn.foxio.simple.GlobalConfig;


/**
 * 计时任务管理器
 * @author lucky
 *
 */
public class CronJobManager {

	/**
	 * 倒计时任务（该类计时是服务器停服后消失）
	 * 
	 * @author
	 */
	public static class CountDownTask {

		static final String TASK_KEY_NAME = "COUNTDOWNTASK";

		/** 任务id 从10000开始计数 **/
		private static AtomicLong  IDSource = new AtomicLong(10000L);

		/** 任务id **/
		private long id;
		/** 开始时间 **/
		private long startTime;

		private long endTime;

		private long delay;

		private JobDetail jobDetail;

		private Trigger trigger;

		private CronJobManager mgr;

		private Object packName;

		private Consumer<Object> remoteCallBack;
		
		private boolean isEnd = false;
		
		public boolean getIsEnd() {
			return isEnd;
		}

		

		public CountDownTask(long delay, Object event, Consumer<Object> callBack) {
			
			
			id = IDSource.get()+1;
			if ( id > Long.MAX_VALUE - 100 ){
				id = 10001;
			}
			IDSource.set(id);

			this.startTime = System.currentTimeMillis();

			this.endTime = startTime + delay;

			this.packName = event;

			this.remoteCallBack = callBack;
		}

		

		public void end() {
			if (remoteCallBack != null) {
				remoteCallBack.accept(packName);
			}
			isEnd = true;

//			if (_callBackActor != null) {
//				ObjectWrapper.wrap(packName).sendTo(_callBackActor);
//			}
		}

		public CountDownTask reset() {

			this.startTime = System.currentTimeMillis();

			this.endTime = startTime + delay;

			return this;
		}

		public long getStartTime() {
			return startTime;
		}

		public long getEndTime() {
			return endTime;
		}

		public long getLastTime() {
			return endTime - System.currentTimeMillis();
		}

		public long getId() {
			return id;
		}

		public String getName() {
			return id + "";
		}

		public void extendTime(long time) {
			this.endTime += time;
		}

		public JobDetail getJobDetail() {
			return jobDetail;
		}

		public Trigger getTrigger() {
			return trigger;
		}

		public void cancel() {
			mgr.removeJob(this);
			isEnd = true;
		}

		public void setDetail(CronJobManager mgr, JobDetail jobDetail, Trigger trigger) {
			this.jobDetail = jobDetail;
			this.trigger = trigger;
			this.mgr = mgr;
		}
	}

	public static class CountDownJob implements Job {

		public CountDownJob() {

		}
		@Override
		public void execute(JobExecutionContext context) throws JobExecutionException {
			try {
				CountDownTask task = (CountDownTask) context.getJobDetail().getJobDataMap().get(CountDownTask.TASK_KEY_NAME);
				task.end();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	private static final String TRIGGER_KEY_PROFIX = "T_";

	private static final String DEFAULT_GROUP_NAME = "default_group";

	private static SchedulerFactory gSchedulerFactory;

	private static CronJobManager cronTaskManager;
	
	private ConcurrentHashMap<Long,	CountDownTask>  taskPool;	

	public CronJobManager() {
		init();
		startJobs();
	}

	public static CronJobManager getInstance() {
		if (cronTaskManager == null) {
			cronTaskManager = new CronJobManager();
		}
		return cronTaskManager;
	}
	
	
	public void init() {
		try {
			gSchedulerFactory = new StdSchedulerFactory( GlobalConfig.getConfigPath() +"quartz.properties");
			taskPool = new ConcurrentHashMap<Long,CountDownTask>(16);
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 启动所有定时任务
	 */
	public void startJobs() {
		if (gSchedulerFactory != null) {
			try {
				Scheduler sched = gSchedulerFactory.getScheduler();
				sched.start();
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * 关闭所有定时任务
	 */
	public void shutdownJobs() {
		if (gSchedulerFactory != null) {
			try {
				Scheduler sched = gSchedulerFactory.getScheduler();
				if (!sched.isShutdown()) {
					sched.shutdown();
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
	}

	/***
	 * 添加一个周期任务
	 * 
	 * 格式: [秒] [分] [小时] [日] [月] [周] [年]
	 * 
	 * 序号 说明 是否必填 允许填写的值 允许的通配符 1 秒 是 0-59 , - * / 2 分 是 0-59 , - * / 3 小时 是
	 * 0-23 ,* * - * / 4 日 是 1-31 , - * ? / L W 5 月 是 1-12 , - * / 6 周 是 1-7 , -
	 * * ? / L # 7 年 否 empty 或 1970-2099 , - * /
	 * 
	 * 常用示例:
	 * 
	 * 0 0 12 * * ? 每天12点触发 0 15 10 ? * * 每天10点15分触发 0 15 10 * * ? 每天10点15分触发 0
	 * 15 * 10 * * ? * 每天10点15分触发 0 15 10 * * ? 2005 2005年每天10点15分触发 0 * 14 * *
	 * ? 每天下午的 * 2点到2点59分每分触发 0 0/5 14 * * ? 每天下午的 2点到2点59分(整点开始，每隔5分触发) 0 0/5
	 * 14,18 * * ?每天下午的 18点到18点59分(整点开始，每隔5分触发) 0 0-5 14 * * ? * 每天下午的
	 * 2点到2点05分每分触发 0 10,44 14 ? 3 WED 3月分每周三下午的 2点10分和2点44分触发 0 15 10 ? * *
	 * MON-FRI 从周一到周五每天上午的10点15分触发 0 15 10 15 * ? 每月15号上午10点15分触发 0 15 10 L * ?
	 * * 每月最后一天的10点15分触发 0 15 10 ? * 6L 每月最后一周的星期五的10点15分触发 0 15 10 ? * 6L
	 * 2002-2005 * 从2002年到2005年每月最后一周的星期五的10点15分触发 0 15 10 ? * 6#3
	 * 每月的第三周的星期五开始触发 0 0 12 1/5 * ? * 每月的第一个中午开始每隔5天触发一次 0 11 11 11 11 ?
	 * 每年的11月11号 11点11分触发(光棍节)
	 * 
	 * 
	 * 
	 * @param jobName
	 *            任务名
	 * @param jobClass
	 *            任务执行类
	 * @param time
	 */
	public boolean addCountDownJob(String jobName, String groupName, CountDownTask task, String reg) {
		try {
			Scheduler scheduler0 = gSchedulerFactory.getScheduler();
			JobDetail jd = scheduler0.getJobDetail(jobName, groupName);

			if (jd != null) {
				/** 停止触发器 */
				scheduler0.pauseTrigger(TRIGGER_KEY_PROFIX + jobName, TRIGGER_KEY_PROFIX + groupName);
				/** 移除触发器 **/
				scheduler0.unscheduleJob(jd.getKey().getName(), jd.getKey().getGroup());
				/** 删除任务 **/
				scheduler0.deleteJob(jd.getKey().getName(), jd.getKey().getGroup());
			}
		} catch (SchedulerException e1) {
			e1.printStackTrace();
			return false;
		}

		try {
			Scheduler scheduler = gSchedulerFactory.getScheduler();

			JobDetail jobDetail = new JobDetail(jobName, groupName, CountDownJob.class);

			jobDetail.getJobDataMap().put(CountDownTask.TASK_KEY_NAME, task);

			CronTrigger trigger = null;
			try {

				trigger = new CronTrigger(TRIGGER_KEY_PROFIX + jobName, TRIGGER_KEY_PROFIX + groupName, reg);

			} catch (ParseException e) {
				e.printStackTrace();
			}

			trigger.setStartTime(new Date(System.currentTimeMillis()));

			scheduler.scheduleJob(jobDetail, trigger);

			task.setDetail(this, jobDetail, trigger);

		} catch (SchedulerException e) {
			e.printStackTrace();
			return false;
		}
		
		taskPool.put(task.id, task);
		
		clearTask();
		
		return true;
	}
	
	
	private void clearTask()
	{
		//需要清理的任务
		ArrayList<Long> taskLst = new ArrayList<>();
		Iterator<Entry<Long, CountDownTask>> iterator = taskPool.entrySet().iterator();
		while( iterator.hasNext() )
		{
			Entry<Long, CountDownTask> next = iterator.next();
			if ( next.getValue().getIsEnd() ) {
				//超时 10秒
				if ( next.getValue().getEndTime() > System.currentTimeMillis() + 10000 ) {
					taskLst.add(next.getKey());
				}
			}
		}
		for ( Long k : taskLst) {
			taskPool.remove(k);
		}	
	}
	

	/***
	 * 添加一个定时任务
	 * 
	 * @param jobName
	 *            任务名
	 * @param jobClass
	 *            任务执行类
	 * @param time
	 */
	public boolean addCountDownJob(String jobName, String groupName, CountDownTask task) {
		String gName = groupName;
		if (groupName == null) {
			gName = DEFAULT_GROUP_NAME;
		}

		try {

			Scheduler scheduler0 = gSchedulerFactory.getScheduler();
			JobDetail jd = scheduler0.getJobDetail(jobName, gName);

			if (jd != null) {
				/** 停止触发器 */
				scheduler0.pauseTrigger(TRIGGER_KEY_PROFIX + jobName, TRIGGER_KEY_PROFIX + gName);
				/** 移除触发器 **/
				scheduler0.unscheduleJob(jd.getKey().getName(), jd.getKey().getGroup());
				/** 删除任务 **/
				scheduler0.deleteJob(jd.getKey().getName(), jd.getKey().getGroup());
			}
		} catch (SchedulerException e1) {
			e1.printStackTrace();
			return false;
		}

		try {
			Scheduler scheduler = gSchedulerFactory.getScheduler();

			JobDetail jobDetail = new JobDetail(jobName, gName, CountDownJob.class);

			jobDetail.getJobDataMap().put(CountDownTask.TASK_KEY_NAME, task);

			SimpleTrigger trigger = new SimpleTrigger(TRIGGER_KEY_PROFIX + jobName, TRIGGER_KEY_PROFIX + gName);

			trigger.setStartTime(new Date(task.getEndTime()));

			scheduler.scheduleJob(jobDetail, trigger);

			task.setDetail(this, jobDetail, trigger);

		} catch (SchedulerException e) {
			e.printStackTrace();
			return false;
		}

		taskPool.put(task.id, task);
		clearTask();
		
		return true;
	}

	/***
	 * 移除一个任务(使用默认的任务组名，触发器名，触发器组名)
	 * 
	 * @param keys 
	 */
	void removeJob(CountDownTask task) {
		try {
			Scheduler sched = gSchedulerFactory.getScheduler();
			/** 停止触发器 */
			sched.pauseTrigger(task.getTrigger().getKey().getName(), task.getTrigger().getKey().getGroup());
			/** 移除触发器 **/
			sched.unscheduleJob(task.getTrigger().getKey().getName(), task.getTrigger().getKey().getGroup());
			/** 删除任务 **/
			sched.deleteJob(task.getJobDetail().getKey().getName(), task.getJobDetail().getKey().getGroup());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void removeJob(long taskId){
		CountDownTask  task = taskPool.remove(taskId);
		if(task!=null){
			task.cancel();
		}else{
			//System.out.println( "removeJob  null !");
		}
	}
}

