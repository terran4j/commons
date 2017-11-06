package com.terran4j.common.util.error;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.terran4j.commons.util.error.BusinessException;
import com.terran4j.commons.util.error.ErrorCodes;
import com.terran4j.commons.util.error.ErrorReport;

@RunWith(SpringJUnit4ClassRunner.class)
public class BusinessExceptionTest {

	private static final Logger log = LoggerFactory.getLogger(BusinessExceptionTest.class);

	@Test
	public void testGetReport() {
		try {
			String mobile = "13800000000";
			String comment = "ABC";
			commentByMobile(mobile, comment);
		} catch (BusinessException e) {
			ErrorReport report = e.getReport();
			System.out.println(report.toString());
			Assert.assertEquals(MockErrorCode.INVALID_CONFIG_VALUE.getValue(), report.getCode().getValue());
			return;
		}
	}

	@Test
	public void testDefaultErrorCodes() {
		try {
			throw new BusinessException(ErrorCodes.ACCESS_DENY);
		} catch (BusinessException e) {
			String msg = e.getMessage();
			log.info(ErrorCodes.ACCESS_DENY + " = " + msg);
			Assert.assertEquals("访问被拒绝", msg);
		}
	}

	public String getConfig(String key) {
		return "abc";
	}

	public int getConfigAsInt(String key) throws BusinessException {
		String value = getConfig(key);
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			throw new BusinessException(MockErrorCode.INVALID_CONFIG_VALUE, e) //
					.put("key", key).put("value", value) //
					.setMessage("获取配置项[${key}]时出错，值[${value}]不是一个数字");
		}
	}

	public long getPoint(long userId, String event) throws BusinessException {
		try {
			String key = "point." + event;
			return getConfigAsInt(key);
		} catch (BusinessException e) {
			throw e.reThrow("获取用户[${userId}]在事件[${event}]中对应的积分值时出错") //
					.put("event", event).put("userId", userId);
		}
	}

	public void plusPoint(long userId, long point) {
	}

	public void plusPoint(long userId, String event) throws BusinessException {
		try {
			long point = getPoint(userId, event);
			plusPoint(userId, point);
		} catch (BusinessException e) {
			throw e.reThrow("用户[${userId}]参与事件[${event}]后，在增加积分时出错。") //
					.put("event", event).put("userId", userId);
		}
	}

	public long getUserId(String mobile) {
		return 1;
	}

	public void commentByMobile(String mobile, String comment) throws BusinessException {
		try {
			long userId = getUserId(mobile);
			plusPoint(userId, "commentByMobile");
		} catch (BusinessException e) {
			throw e.reThrow("用户[${mobile}]在参加手机评论活动时出错") //
					.put("mobile", mobile);
		}
	}

}
