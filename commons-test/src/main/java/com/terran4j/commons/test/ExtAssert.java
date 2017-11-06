package com.terran4j.commons.test;

import com.terran4j.commons.util.DateTimes;
import org.junit.Assert;

import java.util.Date;

public class ExtAssert {

	protected ExtAssert() {
	}

	static String formatClassAndValue(Object value, String valueString) {
		String className = value == null ? "null" : value.getClass().getName();
		return className + "<" + valueString + ">";
	}

	static String format(String message, Object expected, Object actual) {
		String formatted = "";
		if (message != null && !message.equals("")) {
			formatted = message + " ";
		}
		String expectedString = String.valueOf(expected);
		String actualString = String.valueOf(actual);
		if (expectedString.equals(actualString)) {
			return formatted + "expected: " + formatClassAndValue(expected, expectedString) + " but was: "
					+ formatClassAndValue(actual, actualString);
		} else {
			return formatted + "expected:<" + expectedString + "> but was:<" + actualString + ">";
		}
	}

	static String format(Object expected, Object actual) {
		return format(null, expected, actual);
	}

	/**
	 * 比较两个日期对象，但允许一定的时间差。
	 * 
	 * @param expected
	 *            期望值。
	 * @param actual
	 *            实际值。
	 * @param expectedDifferenceTime
	 *            允许的时间差。
	 */
	public static void assertEquals(Date expected, Date actual, long expectedDifferenceTime) {
		if (expected == null && actual == null) {
			return;
		}
		if (expected == null || actual == null) {
			Assert.fail(format(expected, actual));
		}
		long actualDifferenceTime = Math.abs(expected.getTime() - actual.getTime());
		if (actualDifferenceTime > expectedDifferenceTime) {
			String msg = String.format("%s, expected difference is no more than %d ms, but actual is %d ms",
					format(DateTimes.toString(expected), DateTimes.toString(actual)), expectedDifferenceTime,
					actualDifferenceTime);
			Assert.fail(msg);
		}
	}
	
	public static void waitFor(final long millis) {
		if (millis <= 0) {
			return;
		}
		
		long expired = System.currentTimeMillis() + millis;
		
		long step = 0;
		if (millis > 1000) {
			step = 100;
		} else if (millis < 10){
			step = 1;
		} else {
			step = millis / 10;
		}
		
		while(true) {
			if (System.currentTimeMillis() > expired) {
				return;
			}
			try {
				Thread.sleep(step);
			} catch (InterruptedException e) {
			}
		}
	}
}
