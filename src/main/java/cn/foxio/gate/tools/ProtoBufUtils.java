package cn.foxio.gate.tools;


import java.io.IOException;
import java.util.Set;

import com.baidu.bjf.remoting.protobuf.Codec;
import com.baidu.bjf.remoting.protobuf.ProtobufIDLGenerator;
import com.baidu.bjf.remoting.protobuf.ProtobufProxy;
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;


/**
 * protobuf 编解码工具
 * @author lucky
 *
 */
public class ProtoBufUtils {

	/**
	 * 编码
	 * 
	 * @param obj
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public static <T> byte[] encode(T obj)  {
		if (obj == null ) {
			return null;
		}
		try {
			Codec<T> codec = (Codec<T>) ProtobufProxy.create(obj.getClass());
			return codec.encode(obj);
		} catch (IOException e) {
			
			System.out.println( obj );
			System.out.println( obj != null ? obj.getClass().getName() : "obj == null !"  );
			
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 解码
	 * 
	 * @param cls
	 * @param bytes
	 * @return
	 * @throws IOException
	 */

	public static <T> T decode(Class<T> cls, byte[] bytes) {
		Codec<T> codec = ProtobufProxy.create(cls);
		try {
			return codec.decode(bytes);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	public static char byteToChar(byte[] b) {
        char c = (char) (((b[0] & 0xFF) << 8) | (b[1] & 0xFF));
        return c;
    }
	
	
	
	public static String entityToProtobuf( Class<?>  cls )
	{
		String idl = ProtobufIDLGenerator.getIDL(cls);
		return idl;
	}
	
	public static String entityToProtobuf(Class<?>  cls, Set<Class<?>> cachedTypes, Set<Class<?>> cachedEnumTypes) {
		String idl = ProtobufIDLGenerator.getIDL(cls, cachedTypes, cachedEnumTypes);
		return idl;
	}

	public static void main(String[] args)  {

		InnerCls d = new InnerCls();

		d.setId(101);
		d.setName("123abc");
		byte [] tableLst = {(byte)101,(byte)102,(byte)103,(byte)104};
		
		
		
//		new Character
//		
//		tableLst.add( new Character((byte)101));
//		tableLst.add((short)101);
//		tableLst.add((short)102);

		d.setTableLst(tableLst);

		System.out.println(d);

		byte[] ba = ProtoBufUtils.encode(d);

		for (byte b : ba) {
			System.out.println(b);
		}

		System.out.println("ba.size = " + ba.length);

//		InnerCls d2 = ProtoBufUtils.decode(InnerCls.class, ba);
//
//		System.out.println(d2);


	}

	static public class InnerCls {

		@Protobuf(order = 1 )
		private byte[] tableLst;

		@Protobuf(order = 2 )
		private int id;
		@Protobuf(order = 3 )
		private String name;

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public byte[] getTableLst() {
			return tableLst;
		}

		public void setTableLst(byte[] tableLst) {
			this.tableLst = tableLst;
		}


		public InnerCls() {
		}

		@Override
		public String toString() {
			return "InnerCls [id=" + id + ", name=" + name + ", tableLst=" + tableLst + "]";
		}

	}
}
