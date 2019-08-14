package cn.foxio.gate.tools;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * 时间工具
 * @author lucky
 *
 */
public class TimeUtil {

	
	
	/**
	 * 获得当前时间字符串
	 * @return
	 */
	public static String getNowString(){
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
	}
	
	/**
	 * 获得当前日期
	 * @return
	 */
	public static String getNowDayHours(){
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String v = sdf.format(date);
		return v;
	}
	
	/**
	 * 获得时间字符串
	 * @return
	 */
	public static String getDateString( Date d){
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(d);
	}
	
	/**
	 * 获得当前时间字符串
	 * @return
	 */
	public static String getOrderNumber(long id){
		return (new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())) + id;
	}
	
	/**
	 * 返回两个时间毫秒差
	 * @param startTime
	 * @param endTime
	 * @return
	 * @throws Exception
	 */
	public static long milisecondApart(String startTime, String endTime) throws Exception{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date d1 = sdf.parse(startTime);
		Date d2 = sdf.parse(endTime);
		return d2.getTime()-d1.getTime();
	}
	
	/**
	 * 检测是否为一个时间
	 * @param dateString
	 * @return
	 */
	public static boolean checkAsDate(String dateString)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		
		Date date = null;
		try {
			date = sdf.parse(dateString);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date != null;
	}
	
	
    
    /**
     * String -> Date
     * @param text
     */
	public static Date parseDate(String text) throws Exception{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.parse(text);
	}
	
    /**
     * String -> Date
     * @param text
     */
	public static Date parseWebDate(String text) throws Exception{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		return sdf.parse(text);
	}
	
	
	/**
	 * Date -> String
	 * @param date
	 * @return
	 * @throws Exception
	 */
	public static String formatDate(Date date) throws Exception{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(date);
	}
	
	/**
	 * Date -> String
	 * @param date
	 * @return
	 * @throws Exception
	 */
	public static String formatMDate(Date date) throws Exception{
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm:ss");
		return sdf.format(date);
	}
	
	/**
	 * 计算任务延迟执行的具体时间
	 * 
	 */
	public static long calDelayTime(long startTime) {
	    long second = currentDaySecond(new Date());
	    long delay =  startTime >= second ?  startTime - second 
	            : 3600*24 -(second - startTime);
	    
	    return delay;
	}
	
	/**
     * 获取当天已过秒数
     * @param date
     * @return long
     */
    public static long currentDaySecond(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        long second = calendar.get(Calendar.SECOND);
        return hour * 60 * 60 + minute * 60 + second;
    }
    
    
    /**
     *当天零点
     * @param date
     * @return long
     */
    public static Date currentDayZoneHour() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }
    
    
    
    /**
     * 执行指定次数任务定时器
     * @param dateTime
     * @param period
     * @param timerTask
     * @throws Exception
     
    public static void submitLoopsTask(String dateTime, int period, FixedLoopsTask timerTask) throws Exception{
    	if(timerTask.getLoops()<=0){
    		throw new Exception("submitLoopsTask loops need bigger than zero!");
    	}
    	Timer timer = new Timer();
    	long delay = 0;
    	if(!dateTime.equals("0")){
    		
    		long startTime = parseDate(dateTime).getTime();
    		long now = System.currentTimeMillis();
    		delay = startTime - now;
    		timerTask.setStartTime(startTime);
    		timerTask.setPeriod(period);
		}
    	
    	timerTask.setTimer(timer);
    	timer.schedule(timerTask, delay < 0 ? 0 : delay, period < 1 ? 1 : period);
    }*/
    
   
    public static boolean isSameHour(long time1, long time2){
    	Calendar c1 = Calendar.getInstance();
    	c1.setTimeInMillis(time1);
    	Calendar c2 = Calendar.getInstance();
    	c1.setTimeInMillis(time2);
    	int h1 = c1.get(Calendar.HOUR_OF_DAY);
    	int y1 = c1.get(Calendar.YEAR);
    	int m1 = c1.get(Calendar.MONTH);
    	int d1 = c1.get(Calendar.DATE);
    	
    	int h2 = c2.get(Calendar.HOUR_OF_DAY);
    	int y2 = c2.get(Calendar.YEAR);
    	int m2 = c2.get(Calendar.MONTH);
    	int d2 = c2.get(Calendar.DATE);
    	
    	if(h1==h2&&y1==y2&&m1==m2&&d1==d2){
    		return true;
    	}
    	return false;
    }
    

	/**
	 * 格式化日期
	 * 
	 * @param date
	 * @return
	 */
	public static String getDateFormat(java.util.Date date) {
		SimpleDateFormat formatter;
		formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String ctime = formatter.format(date);
		return ctime;
	}
	/**
	 * 格式化日期
	 * 
	 * @param date
	 * @return
	 */
	public static String getDateFormat() {
		return getDateFormat(new Date());
	}
	
	
}
