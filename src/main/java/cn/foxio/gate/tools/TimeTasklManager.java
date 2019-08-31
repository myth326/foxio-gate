//package cn.foxio.gate.tools;
//
//import java.text.ParseException;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.function.Consumer;
//
//import org.quartz.CronScheduleBuilder;
//import org.quartz.JobBuilder;
//import org.quartz.JobDataMap;
//import org.quartz.JobDetail;
//import org.quartz.Scheduler;
//import org.quartz.SimpleScheduleBuilder;
//import org.quartz.SimpleTrigger;
//import org.quartz.Trigger;
//import org.quartz.TriggerBuilder;
//import org.quartz.impl.StdSchedulerFactory;
//import org.quartz.impl.triggers.SimpleTriggerImpl;
//
//
//public class TimeTasklManager {
//	
//	
//	static private TimeTasklManager instance;
//	static public TimeTasklManager getInstance()
//	{
//		if ( instance == null ) instance = new TimeTasklManager();
//		return instance;
//	}
//	
//	
//
//	private Scheduler scheduler = null;
//	
//	
//	public TimeTasklManager(){
//		init();
//	}
//
//	private void init() {
//
//		try {
//			scheduler = StdSchedulerFactory.getDefaultScheduler();
//
//			// scheduler.scheduleJob(job, trigger);
//			
//			scheduler.start();
//
//			// job.getJobDataMap().put("default", new TimeJob())
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//	
//	public void addTask( Consumer<Object> callBack, Object param, long dealy) {
//		
//		String jobName = "default";
//		String groupName =  "testGroup";
//		
//		HashMap<String, Object> map = new HashMap<>();
//		map.put("callBack", callBack);
//		map.put("param", param);
//		
//		JobDetail job = JobBuilder.newJob(Task.class).withIdentity(jobName, groupName)
//				//.usingJobData("desc", "test desc value !")
//				.usingJobData(new JobDataMap(map))
//				.build();
//		try {
//
//			Trigger trigger = TriggerBuilder.newTrigger().withIdentity("default", "timeJob")
//					.withSchedule(SimpleScheduleBuilder
//							//仅执行1次
//							.repeatHourlyForTotalCount(1)
//							)
//					.forJob(jobName, groupName)
//					.startAt( new Date( System.currentTimeMillis() + dealy))
//					//.startNow()
//					.build();
//			
//
//			scheduler.scheduleJob(job, trigger);
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//	}
//
//	public static void main(String[] args) {
//
//		
//		TimeTasklManager.getInstance().addTask( o ->{
//					
//			
//				System.out.println( "时间到了! params " + o.toString() );
//			
//				}, 
//				new String("{ sjkdsfd9-2349 - 8999 }")
//				, 2000L 
//		);
//		
//		
//
//	}
//
//}
