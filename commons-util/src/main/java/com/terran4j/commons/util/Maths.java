package com.terran4j.commons.util;

import java.security.InvalidParameterException;

public class Maths {

	/**
	 * 在指定的范围区间内取值，<br>
	 * 当小于最小值时，取最小值；当大于最大值时，取最大值；在这个范围区间内，取原值。
	 * 
	 * @param value 原值。
	 * @param min 最小值。
	 * @param max 最大值。
	 * @return 范围内的值。
	 */
	public static final int limitIn(int value, Integer min, Integer max) {
		if (min != null && max != null && min > max) {
			String msg = String.format("min can't larger than max, min = %d, max = %d.", min, max);
			throw new InvalidParameterException(msg);
		}
		if (max != null && value > max) {
			value = max;
		}
		if (min != null && value < min) {
			value = min;
		}
		return value;
	}

	public static final long limitIn(long value, Long min, Long max) {
		if (min != null && max != null && min > max) {
			String msg = String.format("min can't larger than max, min = %d, max = %d.", min, max);
			throw new InvalidParameterException(msg);
		}
		if (max != null && value > max) {
			value = max;
		}
		if (min != null && value < min) {
			value = min;
		}
		return value;
	}
}
