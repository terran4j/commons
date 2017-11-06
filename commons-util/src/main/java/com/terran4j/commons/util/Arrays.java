package com.terran4j.commons.util;

public class Arrays {

	/**
	 * 将两个String数组合并成一个，第2个数组直接连在第1个数组的后面，不会重排序。
	 * @param array1
	 * @param array2
	 * @return
	 */
	public static String[] concat(String[] array1, String[] array2) {
		if (array1 == null && array2 == null) {
			return null;
		}
		if (array1 == null) {
			return safeCopy(array2);
		}
		if (array2 == null) {
			return safeCopy(array1);
		}

		String[] dest = new String[array1.length + array2.length];
		System.arraycopy(array1, 0, dest, 0, array1.length);
		System.arraycopy(array2, 0, dest, array1.length,
				array2.length);
		return dest;
	}

	public static String[] safeCopy(String[] source) {
		if (source == null) {
			return null;
		}

		String[] dest = new String[source.length];
		System.arraycopy(source, 0, dest, 0, source.length);
		return dest;
	}

}
