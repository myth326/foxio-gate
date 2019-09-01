package cn.foxio.gate.tools;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;

import cn.foxio.gate.tools.TimeUtil;
import cn.foxio.simple.config.GlobalConfig;



/**
 * 日志文件封装类
 * @author lucky
 *
 */
public class LoggerUtil {
	
	
	
	
	public static final String NETTY_IO = "netty_io";
	
	public static final String MSG_IO_ERROR = "io_error";
	
	public static final String MACTH = "match";

	
	//public static final String PREFIX_RED = GlobalConfig.getConfigPath() +".."+File.separator+"game_logs"+File.separator+"table"+File.separator+"game";
	
	
	//static private  ConcurrentHashMap<String, LoggerTmp> tmpLst = new ConcurrentHashMap<>(); 
	
	
	
	public static String getStackTrace(Throwable throwable)
    {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        try
        {
            throwable.printStackTrace(pw);
            return sw.toString();
        } finally
        {
            pw.close();
        }
    }
	
	/**
	 * 牌桌log
	 * @param name
	 * @return
	 */
	public static Logger getLoggerByName(String name) {
		String prefix = GlobalConfig.getConfigPath() + File.separator+".."+File.separator+"game_logs"+File.separator;
		return getLoggerByName(name, prefix);
	}
	
	/**
	 * 机器人log
	 * @param name
	 * @return
	 */
	public static Logger getRobotLogger(String name,String gameName) {
		String prefix = GlobalConfig.getConfigPath() + File.separator+".."+File.separator+"game_logs"+File.separator;
		prefix = prefix +"robot_"+gameName+File.separator;
		
		return getLoggerByName(name, prefix);
	}
	
	
	
	/**
	 * 网站log
	 * @param name
	 * @return
	 */
	public static Logger getWebLogger(String name ) {
		String prefix = GlobalConfig.getConfigPath() + File.separator+".."+File.separator+"web_logs"+File.separator;
		return getLoggerByName(name, prefix);
	}

	public static Logger getLoggerByName(String logName , String prefix) {
		
		String name = TimeUtil.getNowDayHours() + File.separator+logName;
		
		if ( name != null && ! name.contains(".log") ) {
			name = name + ".log";
		}
		
		// 生成新的Logger
		// 如果已经有了一个Logger实例返回现有的
		Logger logger = Logger.getLogger(name);
		// 清空Appender。特别是不想使用现存实例时一定要初期化
		logger.removeAllAppenders();
		// 设定Logger级别。
		logger.setLevel(Level.DEBUG);
		
		// 设定是否继承父Logger。
		// 默认为true。继承root输出。
		// 设定false后将不输出root。
		logger.setAdditivity(false);
		// 生成新的Appender
		RollingFileAppender appender = new RollingFileAppender();
		appender.setMaxFileSize("10MB");
		appender.setMaxBackupIndex(10000);
		
		
		PatternLayout layout = new PatternLayout();
		// log的输出形式
		String conversionPattern = "[%d{MM-dd HH:mm:ss,SSS}] %p - %m%n";
		layout.setConversionPattern(conversionPattern);
		appender.setLayout(layout);
		// log输出路径
		// 这里使用了环境变量[catalina.home]，只有在tomcat环境下才可以取到
		//String tomcatPath = java.lang.System.getProperty("catalina.home");
		
		appender.setFile( prefix + name );
		// log的文字码
		appender.setEncoding("UTF-8");
		// true:在已存在log文件后面追加 false:新log覆盖以前的log
		appender.setAppend(true);
		// 适用当前配置
		appender.activateOptions();
		// 将新的Appender加到Logger中
		logger.addAppender(appender);
		
		
		return logger;
	}
}