package cn.foxio.gate.tools;

import java.util.ArrayList;
import java.util.List;

/**
 * @author
 */
public final class BytesUtil {

	/**
	 * 在源byte[]增加byte[]
	 *
	 * @param srcValue
	 *            源byte[]
	 * @param addValue
	 *            增加的byte[]
	 * @return 新byte[]
	 */
	public static byte[] append(byte[] srcValue, byte[] addValue) {

		if (srcValue == null) {
			return addValue;
		}
		if (addValue == null) {
			return srcValue;
		}
		int lenOri = srcValue.length;
		int lenInc = addValue.length;
		byte dest[] = new byte[lenOri + lenInc];
		System.arraycopy(srcValue, 0, dest, 0, lenOri);
		System.arraycopy(addValue, 0, dest, lenOri, lenInc);
		return dest;
	}

	/**
	 * 在源String[]增加String[]
	 *
	 * @param srcValue
	 *            源String[]
	 * @param addValue
	 *            增加的String[]
	 * @return 新String[]
	 */
	public static String[] appendString(String[] srcValue, String addValue) {

		if (srcValue == null) {
			return srcValue;
		}
		if (addValue == null) {
			return srcValue;
		}
		int lenOri = srcValue.length;
		int lenInc = 1;
		String[] dest = new String[lenOri + lenInc];
		System.arraycopy(srcValue, 0, dest, 0, lenOri);
		System.arraycopy(addValue, 0, dest, lenOri, lenInc);
		return dest;
	}

	/**
	 * 在源byte[]增加byte[]
	 *
	 * @param srcValue
	 *            源byte[]
	 * @param addValue
	 *            增加的byte[]
	 * @return 新byte[]
	 */
	public static byte[] insert(byte[] srcValue, byte addValue, int idx) {

		int lenOri = srcValue.length;

		byte dest[] = new byte[lenOri + 1];
		dest[idx] = addValue;
		System.arraycopy(srcValue, 0, dest, 0, idx);
		System.arraycopy(srcValue, idx, dest, idx + 1, lenOri - idx);
		return dest;
	}

	/**
	 * 在源byte[]增加byte[]
	 *
	 * @param srcValue
	 *            源byte[]
	 * @param addValue
	 *            增加的byte[]
	 * @return 新byte[]
	 */
	public static byte[] appendNotRepeat(byte[] srcValue, byte[] addValue) {

		if (srcValue == null) {
			return addValue;
		}
		if (addValue == null) {
			return srcValue;
		}
		byte[] newValue = arraycopy(srcValue);
		for (byte avalue : addValue) {
			if (findIndex(avalue, srcValue) == -1) {
				newValue = append(newValue, avalue);
			}
		}
		return newValue;
	}

	public static byte[] arraycopy(byte[] srcValue) {
		if ((srcValue == null) || (srcValue.length == 0)) {
			return null;
		}
		byte[] newValue = new byte[srcValue.length];
		System.arraycopy(srcValue, 0, newValue, 0, newValue.length);
		return newValue;
	}

	/**
	 * 在源byte[][]增加byte[]
	 *
	 * @param srcValue
	 *            源byte[]
	 * @param addValue
	 *            增加的byte[]
	 * @return 新byte[][]
	 */
	public static byte[][] append(byte[][] srcValue, byte[] addValue) {
		if (addValue == null) {
			return srcValue;
		}
		int len = 0;
		if (srcValue != null) {
			len = srcValue.length;
		}
		byte dest[][] = new byte[len + 1][];
		if (len > 0) {
			System.arraycopy(srcValue, 0, dest, 0, len);
		}
		dest[len] = addValue;
		return dest;
	}

	/**
	 * 在源byte[][]增加byte[]
	 *
	 * @param srcValue
	 *            源byte[]
	 * @param addValue
	 *            增加的byte[]
	 * @return 新byte[][]
	 */
	public static int[][] append(int[][] srcValue, int[] addValue) {
		if (addValue == null) {
			return srcValue;
		}
		int len = 0;
		if (srcValue != null) {
			len = srcValue.length;
		}
		int dest[][] = new int[len + 1][];
		if (len > 0) {
			System.arraycopy(srcValue, 0, dest, 0, len);
		}
		dest[len] = addValue;
		return dest;
	}

	/**
	 * 在源byte[]增加byte
	 *
	 * @param srcValue
	 *            源byte[]
	 * @param addValue
	 *            增加的byte
	 * @return 新byte[]
	 */
	public static byte[] append(byte[] srcValue, byte addValue) {
		int len = 0;
		if (srcValue != null) {
			len = srcValue.length;
		}
		byte[] dest = new byte[len + 1];
		if (len > 0) {
			System.arraycopy(srcValue, 0, dest, 0, len);
		}
		dest[len] = addValue;
		return dest;
	}

	/**
	 * 在源long[]增加long
	 *
	 * @param srcValue
	 *            源long[]
	 * @param addValue
	 *            增加的long
	 * @return 新long[]
	 */
	public static int[] append(int[] srcValue, int addValue) {
		int len = 0;
		if (srcValue != null) {
			len = srcValue.length;
		}
		int[] dest = new int[len + 1];
		if (len > 0) {
			System.arraycopy(srcValue, 0, dest, 0, len);
		}
		dest[len] = addValue;
		return dest;
	}

	/**
	 * 在源long[]增加long
	 *
	 * @param srcValue
	 *            源long[]
	 * @param addValue
	 *            增加的long
	 * @return 新long[]
	 */
	public static int[] append(int[] srcValue, int[] addValue) {

		int len = 0;
		if (srcValue != null) {
			len = srcValue.length;
		}
		int[] dest = new int[len + addValue.length];
		if (len > 0) {
			System.arraycopy(srcValue, 0, dest, 0, len);
		}
		for (int i = 0; i < addValue.length; i++) {
			dest[len + i] = addValue[i];
		}
		return dest;
	}

	/**
	 * 在源long[]增加long
	 *
	 * @param srcValue
	 *            源long[]
	 * @param addValue
	 *            增加的long
	 * @return 新long[]
	 */
	public static long[] append(long[] srcValue, long addValue) {
		int len = 0;
		if (srcValue != null) {
			len = srcValue.length;
		}
		long[] dest = new long[len + 1];
		if (len > 0) {
			System.arraycopy(srcValue, 0, dest, 0, len);
		}
		dest[len] = addValue;
		return dest;
	}

	/**
	 * 在源int[]增加byte
	 *
	 * @param srcValue
	 *            源int[]
	 * @param addValue
	 *            增加的int
	 * @return 新int[]
	 */
	public static int[] appendInt(int[] srcValue, int addValue) {
		int len = 0;
		if (srcValue != null) {
			len = srcValue.length;
		}
		int[] dest = new int[len + 1];
		if (len > 0) {
			System.arraycopy(srcValue, 0, dest, 0, len);
		}
		dest[len] = addValue;
		return dest;
	}

	public static boolean arrayBelong(byte min[], byte max[]) {
		if ((min == null) || (max == null)) {
			return false;
		}

		for (byte element : min) {
			if (!existInArray(element, max)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * 合计byte[]数组中指定某值数量
	 *
	 * @param findValue
	 * @param srcValue
	 * @return 数量
	 */
	public static int countInArray(byte findValue, byte[] srcValue) {
		if (srcValue == null) {
			return 0;
		}
		int count = 0;
		for (byte element : srcValue) {
			if (element == findValue) {
				count++;
			}
		}

		return count;
	}

	/**
	 * 按字符分隔字符串,主要用于多家食牌
	 *
	 * @param str
	 *            String
	 * @param dividflag
	 *            char
	 * @return String[]
	 */
	public static String[] divideString(String str, char dividflag) {
		if ((str == null) || (str.length() <= 0)) {
			throw new java.lang.IllegalArgumentException("string error!");
		}
		int rslen = 0;
		for (int i = 0; i < str.length(); i++) {
			if (str.charAt(i) == dividflag) {
				rslen++;
			}
		}
		String[] rs = new String[rslen + 1];
		int j = 0;
		while (str.indexOf(dividflag) > 0) {
			rs[j] = str.substring(0, str.indexOf(dividflag));
			str = str.substring(str.indexOf(dividflag) + 1, str.length());
			j++;
		}
		rs[j] = str;
		return rs;
	}

	public static boolean existInArray(byte[] findValue, byte[] srcValue) {
		if (srcValue == null || findValue == null) {
			return false;
		}
		for (byte find : findValue) {
			if (!existInArray(find, srcValue)) {
				return false;
			}
		}

		return true;
	}

	public static boolean existInArray(byte findValue, byte[] srcValue) {
		if ((srcValue == null) || (srcValue.length == 0)) {
			return false;
		}
		for (byte element : srcValue) {
			if (element == findValue) {
				return true;
			}
		}

		return false;
	}

	public static boolean existInArray(int findValue, int[] srcValue) {
		if ((srcValue == null) || (srcValue.length == 0)) {
			return false;
		}
		for (int element : srcValue) {
			if (element == findValue) {
				return true;
			}
		}

		return false;
	}

	public static int findIndex(byte findValue, byte[] srcValue) {
		if ((srcValue == null) || (srcValue.length == 0)) {
			return -1;
		}
		for (int i = 0; i < srcValue.length; i++) {
			if (srcValue[i] == findValue) {
				return i;
			}
		}
		return -1;
	}
	
	public static long findIndex(long findValue, long[] srcValue) {
		if ((srcValue == null) || (srcValue.length == 0)) {
			return -1;
		}
		for (int i = 0; i < srcValue.length; i++) {
			if (srcValue[i] == findValue) {
				return i;
			}
		}
		return -1;
	}

	public static int findIndex(int findValue, int[] srcValue) {
		if ((srcValue == null) || (srcValue.length == 0)) {
			return -1;
		}
		for (int i = 0; i < srcValue.length; i++) {
			if (srcValue[i] == findValue) {
				return i;
			}
		}
		return -1;
	}

	public static int findIndex(int[] findValue, int[][] srcValue) {
		if ((srcValue == null) || (srcValue.length == 0)) {
			return -1;
		}
		for (int i = 0; i < srcValue.length; i++) {
			if (equals(srcValue[i], findValue)) {
				return i;
			}
		}
		return -1;
	}

	public static boolean equals(int[] src, int[] value) {
		if (src == null && value == null) {
			return true;
		}
		if (src == null || value == null) {
			return false;
		}

		if (src.length != value.length) {
			return false;
		}

		for (int i = 0; i < src.length; i++) {
			if (src[i] != value[i]) {
				return false;
			}
		}
		return true;
	}

	public static int findIndexDesc(byte findValue, byte[] srcValue) {
		if ((srcValue == null) || (srcValue.length == 0)) {
			return -1;
		}
		int len = srcValue.length - 1;
		for (int i = len; i >= 0; i--) {
			if (srcValue[i] == findValue) {
				return i;
			}
		}
		return -1;
	}

	public static int findIndexFromIndex(byte findValue, byte[] srcValue, int findIndex) {
		if ((srcValue == null) || (srcValue.length == 0)) {
			return -1;
		}
		for (int i = findIndex; i < srcValue.length; i++) {
			if (srcValue[i] == findValue) {
				return i;
			}
		}
		return -1;
	}

	public static String printBytes(byte[] data) {
		if (data == null || data.length == 0) {
			return "[]";
		}
		int len = data.length;
		StringBuffer buf = new StringBuffer(2 * len + 1);
		buf.append('[');
		for (int i = 0; i < len; i++) {
			buf.append(data[i]);
			if (i < len - 1) {
				buf.append(',');
			}
		}
		buf.append(']');
		return buf.toString();
	} // printBytes

	public static String printBoolean(boolean[] data) {
		if (data == null || data.length == 0) {
			return "[]";
		}
		int len = data.length;
		StringBuffer buf = new StringBuffer(2 * len + 1);
		buf.append('[');
		for (int i = 0; i < len; i++) {
			buf.append(data[i]);
			if (i < len - 1) {
				buf.append(',');
			}
		}
		buf.append(']');
		return buf.toString();
	} // printBoolean

	public static String printString(String[] data) {
		if (data == null || data.length == 0) {
			return "[]";
		}
		int len = data.length;
		StringBuffer buf = new StringBuffer(2 * len + 1);
		buf.append('[');
		for (int i = 0; i < len; i++) {
			buf.append(data[i]);
			if (i < len - 1) {
				buf.append(',');
			}
		}
		buf.append(']');
		return buf.toString();
	} // printBoolean

	public static String printBytes(int[] data) {
		if (data == null || data.length == 0) {
			return "[]";
		}
		int len = data.length;
		StringBuffer buf = new StringBuffer(2 * len + 1);
		buf.append('[');
		for (int i = 0; i < len; i++) {
			buf.append(data[i]);
			if (i < len - 1) {
				buf.append(',');
			}
		}
		buf.append(']');
		return buf.toString();
	} // printBytes

	public static String printBytes(int[][] data) {
		if (data == null || data.length == 0) {
			return "[]";
		}
		int len = data.length;
		StringBuffer buf = new StringBuffer(128);
		buf.append('[');
		for (int i = 0; i < len; i++) {
			buf.append(printBytes(data[i]));
			if (i < len - 1) {
				buf.append(',');
			}
		}
		buf.append(']');
		return buf.toString();
	} // printBytes

	public static String printBytes(long[] data) {
		if (data == null || data.length == 0) {
			return "[]";
		}
		int len = data.length;
		StringBuffer buf = new StringBuffer(2 * len + 1);
		buf.append('[');
		for (int i = 0; i < len; i++) {
			buf.append(data[i]);
			if (i < len - 1) {
				buf.append(',');
			}
		}
		buf.append(']');
		return buf.toString();
	} // printBytes

	/**
	 * 打牌，移除元素
	 *
	 * @param removeValue
	 *            byte[]
	 * @param srcValue
	 *            byte 打出的牌
	 * @return byte[]
	 */
	public static byte[] removeByValue(byte[] removeValue, byte[] srcValue) {
		if (removeValue == null || removeValue.length == 0) {
			return srcValue;
		}
		if (srcValue == null || srcValue.length == 0) {
			return null;
		}
		byte[] newValue = srcValue;
		for (int i = 0; i < removeValue.length; i++) {
			newValue = removeByValue(removeValue[i], newValue);
		}
		return newValue;
	}


	public static byte[][] removeFirst(byte[][] srcValue) {
		if ((srcValue == null) || (srcValue.length <= 1)) {
			return null;
		}
		int len = srcValue.length - 1;
		byte[][] dest = new byte[len][];
		System.arraycopy(srcValue, 1, dest, 0, len);
		return dest;
	}

	public static byte[] removeFirst(byte[] srcValue) {
		if ((srcValue == null) || (srcValue.length <= 1)) {
			return null;
		}
		int len = srcValue.length - 1;
		byte[] dest = new byte[len];
		System.arraycopy(srcValue, 1, dest, 0, len);
		return dest;
	}

	public static byte[] removeLast(byte[] srcValue) {
		if ((srcValue == null) || (srcValue.length <= 1)) {
			return null;
		}
		int len = srcValue.length - 1;
		byte[] dest = new byte[len];
		System.arraycopy(srcValue, 0, dest, 0, len);
		return dest;
	}

	public static byte[][] removeLast(byte[][] srcValue) {
		if ((srcValue == null) || (srcValue.length <= 1)) {
			return null;
		}
		int len = srcValue.length - 1;
		byte[][] dest = new byte[len][];
		System.arraycopy(srcValue, 0, dest, 0, len);
		return dest;
	}

	public static byte[] removeByValue(byte removeValue, byte[] srcValue) {
		if ((srcValue == null) || (srcValue.length == 0)) {
			return null;
		}
		int index = findIndex(removeValue, srcValue);
		if (index == -1) {
			return srcValue;
		}
		if (index == srcValue.length - 1) {
			return removeLast(srcValue);
		}
		byte[] result = new byte[srcValue.length - 1];
		System.arraycopy(srcValue, 0, result, 0, index);
		System.arraycopy(srcValue, index + 1, result, index, srcValue.length - 1 - index);
		return result;
	}

	public static int[] removeByValue(int removeValue, int[] srcValue) {
		if ((srcValue == null) || (srcValue.length == 0)) {
			return null;
		}
		int index = findIndex(removeValue, srcValue);
		if (index == -1) {
			return srcValue;
		}
		int[] result = new int[srcValue.length - 1];
		System.arraycopy(srcValue, 0, result, 0, index);
		System.arraycopy(srcValue, index + 1, result, index, srcValue.length - 1 - index);
		return result;
	}

	public static int[][] removeByValue(int[] removeValue, int[][] srcValue) {
		if ((srcValue == null) || (srcValue.length == 0)) {
			return null;
		}
		int index = findIndex(removeValue, srcValue);
		if (index == -1) {
			return srcValue;
		}
		int[][] result = new int[srcValue.length - 1][];
		System.arraycopy(srcValue, 0, result, 0, index);
		System.arraycopy(srcValue, index + 1, result, index, srcValue.length - 1 - index);
		return result;
	}

	public static byte[] removeByIndex(byte[] srcValue, int removeIndex) {
		if (srcValue == null || (srcValue.length == 0)) {
			return null;
		}
		if (removeIndex < 0 || removeIndex >= srcValue.length) {
			return srcValue;
		}
		byte[] result = new byte[srcValue.length - 1];
		System.arraycopy(srcValue, 0, result, 0, removeIndex);
		System.arraycopy(srcValue, removeIndex + 1, result, removeIndex, srcValue.length - 1 - removeIndex);
		return result;
	}
	
	
	

	public static int[] removeByIndex(int[] srcValue, int removeIndex) {
		if (srcValue == null || (srcValue.length == 0)) {
			return null;
		}
		if (removeIndex < 0 || removeIndex >= srcValue.length) {
			return srcValue;
		}
		int[] result = new int[srcValue.length - 1];
		System.arraycopy(srcValue, 0, result, 0, removeIndex);
		System.arraycopy(srcValue, removeIndex + 1, result, removeIndex, srcValue.length - 1 - removeIndex);
		return result;
	}

	public static int[][] removeByIndex(int[][] srcValue, int removeIndex) {
		if (srcValue == null || (srcValue.length == 0)) {
			return null;
		}
		if (removeIndex < 0 || removeIndex >= srcValue.length) {
			return srcValue;
		}
		int[][] result = new int[srcValue.length - 1][];
		System.arraycopy(srcValue, 0, result, 0, removeIndex);
		System.arraycopy(srcValue, removeIndex + 1, result, removeIndex, srcValue.length - 1 - removeIndex);
		return result;
	}

	public static long[] removeByIndex(long[] srcValue, int removeIndex) {
		if (srcValue == null || (srcValue.length == 0)) {
			return null;
		}
		if (removeIndex < 0 || removeIndex >= srcValue.length) {
			return srcValue;
		}
		long[] result = new long[srcValue.length - 1];
		System.arraycopy(srcValue, 0, result, 0, removeIndex);
		System.arraycopy(srcValue, removeIndex + 1, result, removeIndex, srcValue.length - 1 - removeIndex);
		return result;
	}

	public static int flipValue(byte findValue, byte[] srcValue) {
		if ((srcValue == null) || (srcValue.length == 0)) {
			return -1;
		}
		int len = srcValue.length - 1;
		for (int i = len; i >= 0; i--) {
			if (srcValue[i] == findValue) {
				return srcValue[i] *= -1;
			}
		}
		return -1;
	}

	public static byte[] removeByValueFromLast(byte removeValue, byte[] srcValue) {
		if ((srcValue == null) || (srcValue.length == 0)) {
			return null;
		}
		int index = findIndexDesc(removeValue, srcValue);
		if (index == -1) {
			return srcValue;
		}
		byte[] result = new byte[srcValue.length - 1];
		System.arraycopy(srcValue, 0, result, 0, index);
		System.arraycopy(srcValue, index + 1, result, index, srcValue.length - 1 - index);
		return result;
	}

	public static void sort(byte[][] srcValue) {
		byte len = (byte) srcValue.length;
		for (byte i = 0; i < len - 1; i++) {
			for (byte j = (byte) (i + 1); j < len; j++) {
				if (srcValue[i][0] > srcValue[j][0]) {
					byte abyte1[] = srcValue[i];
					srcValue[i] = srcValue[j];
					srcValue[j] = abyte1;
				}
			}
		}
	}

	/**
	 * 排序，按升序排
	 *
	 * @param srcValue
	 *            byte[]
	 */
	public static void sort(byte[] srcValue) {
		if (srcValue == null) {
			return;
		}

		int len = srcValue.length;
		for (int i = 0; i < len - 1; i++) {
			for (int j = (i + 1); j < len; j++) {
				if (srcValue[i] > srcValue[j]) {
					byte temp = srcValue[i];
					srcValue[i] = srcValue[j];
					srcValue[j] = temp;
				}
			}
		}
	}

	/**
	 * 排序，按升序排
	 *
	 * @param srcValue
	 *            byte[]
	 */
	public static void sort(int[] srcValue) {
		if (srcValue == null) {
			return;
		}
		int len = srcValue.length;
		for (int i = 0; i < len - 1; i++) {
			for (int j = (i + 1); j < len; j++) {
				if (srcValue[i] > srcValue[j]) {
					int temp = srcValue[i];
					srcValue[i] = srcValue[j];
					srcValue[j] = temp;
				}
			}
		}
	}

	public static void sort(int[][] srcValue, int id) {
		int len = srcValue.length;
		for (int i = 0; i < len - 1; i++) {
			for (int j = (i + 1); j < len; j++) {
				if (srcValue[i][id] > srcValue[j][id]) {
					int abyte1[] = srcValue[i];
					srcValue[i] = srcValue[j];
					srcValue[j] = abyte1;
				}
			}
		}
	}

	public static void sortAscending(byte[] srcValue) {
		if ((srcValue == null) || (srcValue.length == 0)) {
			return;
		}
		byte temp = 0;
		int firstSorted = srcValue.length - 1;
		int last = 0;
		boolean exchanged = true;
		for (int i = 0; exchanged && (i < srcValue.length); i++) {
			exchanged = false;
			last = firstSorted;
			for (int j = 0; j < last; j++) {
				if (srcValue[j] > srcValue[j + 1]) {
					temp = srcValue[j];
					srcValue[j] = srcValue[j + 1];
					srcValue[j + 1] = temp;
					exchanged = true;
					firstSorted = j;
				}
			}
		}

	}

	/**
	 * 排序，按降序排列
	 *
	 * @param srcValue
	 *            byte[]
	 */
	public static void sortDesc(byte[] srcValue) {
		if (srcValue == null) {
			return;
		}
		int len = srcValue.length;
		for (int i = 0; i < len - 1; i++) {
			for (int j = (i + 1); j < len; j++) {
				if (srcValue[i] < srcValue[j]) {
					byte temp = srcValue[i];
					srcValue[i] = srcValue[j];
					srcValue[j] = temp;
				}
			}
		}
	}

	/**
	 * 排序，按降序排列
	 *
	 * @param srcValue
	 *            byte[]
	 */
	public static void sortDesc(int[] srcValue) {
		if (srcValue == null) {
			return;
		}
		int len = srcValue.length;
		for (int i = 0; i < len - 1; i++) {
			for (int j = (i + 1); j < len; j++) {
				if (srcValue[i] < srcValue[j]) {
					int temp = srcValue[i];
					srcValue[i] = srcValue[j];
					srcValue[j] = temp;
				}
			}
		}
	}

	public static void sortDescending(byte[] srcValue) {
		if ((srcValue == null) || (srcValue.length == 0)) {
			return;
		}
		byte temp = 0;
		int firstSorted = srcValue.length - 1;
		int last = 0;
		boolean exchanged = true;
		for (int i = 0; exchanged && (i < srcValue.length); i++) {
			exchanged = false;
			last = firstSorted;
			for (int j = 0; j < last; j++) {
				if (srcValue[j] < srcValue[j + 1]) {
					temp = srcValue[j];
					srcValue[j] = srcValue[j + 1];
					srcValue[j + 1] = temp;
					exchanged = true;
					firstSorted = j;
				}
			}
		}
	}

	public void sort(byte[][] srcValue, byte id) {
		byte len = (byte) srcValue.length;
		for (byte i = 0; i < len - 1; i++) {
			for (byte j = (byte) (i + 1); j < len; j++) {
				if (srcValue[i][id] > srcValue[j][id]) {
					byte abyte1[] = srcValue[i];
					srcValue[i] = srcValue[j];
					srcValue[j] = abyte1;
				}
			}
		}
	}

	public void sort(byte[][] srcValue, byte[][] b) {
		int len = srcValue.length;
		if ((b == null) || (b.length != len)) {
			throw new java.lang.IllegalArgumentException();
		}
		for (int i = 0; i < len - 1; i++) {
			for (int j = (i + 1); j < len; j++) {
				if (srcValue[i][0] > srcValue[j][0]) {
					byte abyte1[] = srcValue[i];
					srcValue[i] = srcValue[j];
					srcValue[j] = abyte1;
				}
			}
		}
	}
	
	static public ArrayList<Byte> intArrToByteArr( List<Integer> obj){
		
		ArrayList<Byte> res = new ArrayList<>();
		if ( obj == null || obj.size() == 0 ) {
			return res;
		}
		
		for ( Integer v : obj) {
			res.add(Byte.valueOf(v.toString()));
		}
		return res;
	}

}
