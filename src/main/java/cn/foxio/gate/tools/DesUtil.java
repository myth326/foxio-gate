package cn.foxio.gate.tools;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;


/**
 * 
 * @author lucky
 *
 */
public class DesUtil {

	/**
	 * 算法名称
	 */
	public static final String KEY = "A2B1C7D8E5F60708";
	/**
	 * 算法名称/加密模式/填充方式
	 */
	public static final String CIPHER_ALGORITHM = "DES/ECB/PKCS5Padding";

	/**
	 * 
	 * 生成密钥key对象
	 * 
	 * @param KeyStr 密钥字符串
	 * @return 密钥对象
	 * @throws InvalidKeyException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 * @throws Exception
	 */
	private static SecretKey keyGenerator(String keyStr) throws Exception {
		byte input[] = hexString2Bytes(keyStr);
		DESKeySpec desKey = new DESKeySpec(input);
		// 创建一个密匙工厂，然后用它把DESKeySpec转换成
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		SecretKey securekey = keyFactory.generateSecret(desKey);
		return securekey;
	}

	private static int parse(char c) {
		if (c >= 'a') {
			return (c - 'a' + 10) & 0x0f;
		}
		if (c >= 'A') {
			return (c - 'A' + 10) & 0x0f;
		}
		return (c - '0') & 0x0f;
	}

	/**
	 * 从十六进制字符串到字节数组转换
	 * @param hexstr
	 * @return
	 */
	public static byte[] hexString2Bytes(String hexstr) {
		byte[] b = new byte[hexstr.length() / 2];
		int j = 0;
		for (int i = 0; i < b.length; i++) {
			char c0 = hexstr.charAt(j++);
			char c1 = hexstr.charAt(j++);
			b[i] = (byte) ((parse(c0) << 4) | parse(c1));
		}
		return b;
	}

	/**
	 * 加密数据
	 * 
	 * @param data 待加密数据
	 * @param key  密钥
	 * @return 加密后的数据
	 */
	public static String encrypt(String data, String key) throws Exception {
		try {
			Key deskey = keyGenerator(key);

			// 实例化Cipher对象，它用于完成实际的加密操作
			Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
			SecureRandom random = new SecureRandom();
			// 初始化Cipher对象，设置为加密模式
			cipher.init(Cipher.ENCRYPT_MODE, deskey, random);
			byte[] results = cipher.doFinal(data.getBytes());
			// 加密后结果用Base64编码进行传输
			
			byte[] bytes = Base64.getEncoder().encode(results);
			return new String(bytes);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	/**
	 * 解密数据
	 * 
	 * @param data 待解密数据
	 * @param key  密钥
	 * @return 解密后的数据
	 */
	public static String decrypt(String data, String key) {
		try {
			Key deskey = keyGenerator(key);
			Cipher cipher;
			cipher = Cipher.getInstance(CIPHER_ALGORITHM);
			// 初始化Cipher对象，设置为解密模式
			cipher.init(Cipher.DECRYPT_MODE, deskey);
			// 执行解密操作
			
			byte[] bytes = Base64.getDecoder().decode(data);
			return new String(cipher.doFinal(bytes));
			//return new String(cipher.doFinal(Base64.decodeBase64(data)));

		} catch (Exception e) {
			System.out.println( "decrypt :: data = "+ data  );
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) throws Exception {

		
		
		String k = "229c63b2-0615-4979-b130-8782b71c";
		
		String o = "s=2&orderId=20181230160644774ittest01&type=1&amount=1.0&moneyId=1&account=ittest01&timestamp=1546157206847";
		
		
		String value = DesUtil.encrypt(o, k) ;
		
		System.out.println( value );
		
		String de = DesUtil.decrypt(value, k);
		
		System.out.println( de );
		

	}
}