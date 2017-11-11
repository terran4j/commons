package com.terran4j.test.commons.reflux;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import com.terran4j.commons.reflux.OnMessage;
import com.terran4j.commons.reflux.RefluxClient;
import com.terran4j.commons.reflux.RefluxServer;
import com.terran4j.commons.util.Strings;
import com.terran4j.commons.util.error.BusinessException;

@SpringBootTest(classes = { RefluxApplication.class }, webEnvironment = WebEnvironment.DEFINED_PORT)
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class })
@RunWith(SpringJUnit4ClassRunner.class)
public class SendAndReceiveTest {

	private static final Logger log = LoggerFactory.getLogger(SendAndReceiveTest.class);

	private CountDownLatch flag;

	@Autowired
	private RefluxClient refluxClient;
	
	@Autowired
	private RefluxServer refluxServer;
	
	@Value("${server.wsURL:ws://localhost:8080/websocket/connect}")
	private String serverURL;
	
	private String clientId = null;
	
	@Before
	public void setUp() throws BusinessException {
		clientId = TestServerEndpoint.generateClientId();
		boolean connected = refluxClient.connect(serverURL, clientId);
		Assert.assertTrue(connected);
	}

	@OnMessage
	public void onHello(Hello hello) {
		if (log.isInfoEnabled()) {
			log.info("receive message, hello = {}", Strings.toString(hello));
		}
		flag.countDown();
	}

	@Test
	public void testSendAndReceive() throws InterruptedException {
		flag = new CountDownLatch(1);
		Hello hello = new Hello("terran4j");
		long t0 = System.currentTimeMillis();
		refluxServer.send(hello, clientId);
		try {
			boolean success = flag.await(2000, TimeUnit.MILLISECONDS);
			Assert.assertTrue(success);
		} catch (InterruptedException e) {
			Assert.fail("InterruptedException: " + e.getMessage());
		}
		long t = System.currentTimeMillis() - t0;
		log.info("Send and Receive, spend: {}ms", t);
	}

}
