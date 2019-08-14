package cn.foxio.gate.tools;

import java.io.FileInputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;


/**
 * 
 * @author lucky
 *
 */
public class PropertiesConfig {

	private Map<String,String> map = new HashMap<>();

	private String url = null;
	
	public PropertiesConfig( String url ) {
		if ( url == null){
			throw new Error(" url error ! the url is null");
		}
		this.url = url;
		init();
	}
	
	/**
	 * 取值
	 * @param key
	 * @return
	 */
	public String getValString(String key){
		return map.get(key);
	}
	
	/**
	 * 取值
	 * @param key
	 * @return
	 */
	public int getValInt(String key){
		String v = map.get(key);
		if ( v == null ) {
			return -1;
		}
		return new Integer(v);
	}

	
	private void init() {

		Set<?> k = map.keySet();

		while (k.iterator().hasNext()) {
			Object obj = k.iterator().next();
			System.out.println(obj);
		}

		Properties pps = new Properties();
		try {
			pps.load(new FileInputStream(url));
		} catch (Exception e1) {
			e1.printStackTrace();
		} 
		// 得到配置文件的名字
		Enumeration<?> enum1 = pps.propertyNames();
		while (enum1.hasMoreElements()) {
			String strKey = (String) enum1.nextElement();
			String strValue = pps.getProperty(strKey);
			map.put(strKey, strValue);
		}

	}

}
