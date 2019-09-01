//package cn.foxio.gate.tools;
//
//import java.text.ParseException;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.concurrent.atomic.AtomicInteger;
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
//	private AtomicInteger taskId = new AtomicInteger(1000);
//	
//	
//	public void addTask( Consumer<Object> callBack, Object param, long dealy) {
//		
//		
//		final int id = taskId.get() + 1;
//		taskId.set(id);
//		if ( id > Integer.MAX_VALUE - 5000){
//			taskId.set(1000);
//		}
//		
//		String jobName = "default-"+ id;
//		String groupName =  "testGroup";
//		
//		HashMap<String, Object> map = new HashMap<>();
//		map.put("callBack", callBack);
//		map.put("param", param);
//		map.put("id", id);
//		
//		JobDetail job = JobBuilder.newJob(Task.class).withIdentity(jobName, groupName)
//				//.usingJobData("desc", "test desc value !")
//				.usingJobData(new JobDataMap(map))
//				.build();
//		try {
//
//			Trigger trigger = TriggerBuilder.newTrigger().withIdentity(jobName, groupName)
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
//		
//		for ( int i = 0 ; i < 5 ; i ++ ){
//		
//			TimeTasklManager.getInstance().addTask( o ->{
//						
//				
//					System.out.println( "时间到了! params " + o.toString() );
//				
//					}, 
//					String.valueOf(" id - " + i )
//					, i * 1000L 
//			);
//		
//		}
//		
//		
//
//	}
//
//}
