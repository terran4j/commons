//package com.terran4j.commons.test;
//
//import com.terran4j.commons.hi.HttpClient;
//import com.terran4j.commons.hi.HttpException;
//import com.terran4j.commons.hi.Request;
//import com.terran4j.commons.hi.Session;
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.ApplicationContext;
//import org.springframework.util.StringUtils;
//
//public class BaseHttpTest extends BaseSpringBootTest {
//
//	private static final Logger log = LoggerFactory.getLogger(BaseHttpTest.class);
//
//	@Autowired
//	protected ApplicationContext context;
//
//	protected HttpClient httpClient = null;
//
//	protected Session session = null;
//
//	@Before
//	public void setUp() throws Exception {
//		httpClient = HttpClient.create(context);
//		String appSecret = getAppSecret();
//		if (!StringUtils.isEmpty(appSecret)) {
//			httpClient.addListener(new SigListener(appSecret));
//		}
//		if (log.isInfoEnabled()) {
//			log.info("create httpClient.");
//		}
//	}
//
//	@After
//	public void tearDown() throws Exception {
//		this.session = null;
//	}
//
//	@Test
//	public void testEmpty() throws Exception {
//	}
//
//	protected final Request request(String action) throws HttpException {
//		if (this.session == null) {
//			this.session = httpClient.create();
//		}
//		return this.session.action(action);
//	}
//
//	protected final Request request(String action, boolean newSession) throws HttpException {
//		if (this.session == null || newSession) {
//			this.session = httpClient.create();
//		}
//		return this.session.action(action);
//	}
//
//	protected final Request login() throws HttpException {
//		this.session = httpClient.create();
//		return this.session.action(getLoginAction());
//	}
//
//	protected final Request login(String action) throws HttpException {
//		this.session = httpClient.create();
//		return this.session.action(action);
//	}
//
//	/**
//	 * 如果需要计算参数签名，就覆盖本方法返回 appSecret 。
//	 *
//	 * @return
//	 */
//	protected String getAppSecret() {
//		return null;
//	}
//
//	/**
//	 * 登录请求的名称。
//	 * @return
//	 */
//	protected String getLoginAction() {
//		return "login";
//	}
//
//}
