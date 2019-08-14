package cn.foxio.gate.tools;



import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 
 * @author lucky
 *
 */
public class FileUtils {

	public static boolean createFile(String filePath) {
		File f = new File(filePath);
		return createFile(f);
	}

	/**
	 * 创建文件
	 * 
	 * @param fileName
	 * @return
	 */
	public static boolean createFile(File fileName) {
		try {
			if (!fileName.exists()) {
				fileName.createNewFile();
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 读TXT文件内容
	 * 
	 * @param fileName
	 * @return
	 */
	public static String readTxtFile(String path) {

		long n = System.currentTimeMillis();
		String res = "";
		
		try {

			int bufSize = 1024 * 1000;
			byte[] bs = new byte[bufSize];
			ByteBuffer byteBuf = ByteBuffer.allocate(bufSize);

			FileChannel channel;

			channel = new RandomAccessFile(path, "r").getChannel();

			while (channel.read(byteBuf) != -1) {
				int size = byteBuf.position();
				byteBuf.rewind();
				byteBuf.get(bs);
				// 把文件当字符串处理，直接打印做为一个例子。
				// System.out.print(new String(bs, 0, size));
				res += new String(bs, 0, size);
				byteBuf.clear();
			}
			n = System.currentTimeMillis() - n;
			// System.err.println("读取完毕!, 耗时 "+n+" ms");
		} catch ( Exception e) {
			e.printStackTrace();
		}

		return res;

	}
	
	/**
	 * byte[] 读文件
	 * 
	 * @param fileName
	 * @return
	 */
	public static byte [] readFile(String path) {
		try {
		 InputStream in = new FileInputStream(path);
		    byte[] data = toByteArray(in);
		    in.close();
		 
		    return data;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	static private byte[] toByteArray(InputStream in) throws IOException {
		 
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    byte[] buffer = new byte[1024 * 4];
	    int n = 0;
	    while ((n = in.read(buffer)) != -1) {
	        out.write(buffer, 0, n);
	    }
	    return out.toByteArray();
	}
	
	public static boolean writeFile(byte[] content, File fileName) throws Exception {
		RandomAccessFile mm = null;
		boolean flag = false;
		FileOutputStream o = null;
		try {
			o = new FileOutputStream(fileName);
			o.write(content);
			o.close();
			flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (mm != null) {
				mm.close();
			}
		}
		return flag;
	}
	

	public static boolean writeTxtFile(String content, File fileName) throws Exception {
		RandomAccessFile mm = null;
		boolean flag = false;
		FileOutputStream o = null;
		try {
			o = new FileOutputStream(fileName);
			o.write(content.getBytes("utf-8"));
			o.close();
			// mm=new RandomAccessFile(fileName,"rw");
			// mm.writeBytes(content);
			flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (mm != null) {
				mm.close();
			}
		}
		return flag;
	}

	public static void contentToTxt(String filePath, String content) {
		// 原有txt内容
		String str = new String(); 
		// 内容更新
		String s1 = new String();
		try {
			File f = new File(filePath);
			if (f.exists()) {
				System.out.print("文件存在");
			} else {
				System.out.print("文件不存在");
				f.createNewFile();// 不存在则创建
			}
			BufferedReader input = new BufferedReader(new FileReader(f));

			while ((str = input.readLine()) != null) {
				s1 += str + "\n";
			}
			input.close();
			s1 += content;

			BufferedWriter output = new BufferedWriter(new FileWriter(f));
			output.write(s1);
			output.close();
		} catch (Exception e) {
			e.printStackTrace();

		}
	}

}