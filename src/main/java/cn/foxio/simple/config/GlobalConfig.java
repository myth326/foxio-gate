package cn.foxio.simple.config;

import java.util.Scanner;

/**
 * 
 * @author lucky
 *
 */
public class GlobalConfig {

	
	private static boolean isFirst = false;
	
	static public String getConfigPath()
	{
		String v = "";
		if ( isWindows() ){
			v = GlobalConfig.class.getResource("/").toString()+"config_inner/";
			v = v.replace("file:/", "");
		}else{
			v= "//game//server//config//";
		}
		
		//生产配置
		String product = "config_product";
		
		if ( !isFirst && v.contains(product)) {
			
			isFirst = true;
			 Scanner xx = new Scanner( System.in );
		        System.out.print("当前配置为   【生产】 环境！！！， 继续请输入 [y] ");
		        String value = xx.next();
		        if ( "y".equals(value)) {
		        	return v;
		        }else {
		        	return null;
		        }
		}
		
		return v;
	}
	
	
	
	
	static public boolean isWindows(){
		String os = System.getProperty("os.name");  
        if (os != null && os.startsWith("Windows")) {
        	return true;
        }
        return false;
	}
	
	public static void main(String[] args) {
		String os = System.getProperty("os.name");  
		System.out.println( os + isWindows() );
	}
	
}
