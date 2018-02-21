package com.terran4j.commons.hi;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;

import com.google.gson.JsonObject;
import com.terran4j.commons.util.Strings;
import com.terran4j.commons.util.value.JsonValueSource;
import com.terran4j.commons.util.value.ValueSource;
import com.terran4j.commons.util.value.ValueSources;

public final class Request {
	
	private final Session session;
	
	private final ApplicationContext applicationContext;
	
	private final Action action;
	
	private final Map<String, String> params = new HashMap<String, String>();
	
	private final Map<String, String> expects = new HashMap<String, String>();

	public Request(Action action, Session session, ApplicationContext applicationContext) {
		super();
		this.action = action;
		this.session = session;
		this.applicationContext = applicationContext;
	}
	
	public Request params(Properties props) {
		if (props != null && !props.isEmpty()) {
			Iterator<Object> it = props.keySet().iterator();
			while (it.hasNext()) {
				Object key = it.next();
				Object value = props.get(key);
				if (key instanceof String && value instanceof String) {
					params.put((String)key, (String)value);
				}
			}
		}
		return this;
	}
	
	public Request param(String key, String value) {
		params.put(key, value);
		return this;
	}
	
	public Request expect(String key, String expectedValue) {
		if (expectedValue == null) {
			throw new NullPointerException("expectedValue is null.");
		}
		expects.put(key, expectedValue);
		return this;
	}
	
	public void exe(final int threadCount, final int exeCountPerThread, final int intervalTime) throws HttpException {
		if (threadCount < 1) {
			throw new InvalidParameterException("threadCount must more than 0: " + threadCount);
		}
		if (exeCountPerThread < 1) {
			throw new InvalidParameterException("exeCountPerThread must more than 0: " + exeCountPerThread);
		}
		if (intervalTime < 0) {
			throw new InvalidParameterException("intervalTime must more than -1: " + intervalTime);
		}
		Thread[] threads = new Thread[threadCount];
		final List<HttpException> errors = new ArrayList<HttpException>();
		CountDownLatch latch = new CountDownLatch(threadCount);
		for (int i=0; i<threadCount; i++) {
			threads[i] = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						for (int k=0; k<exeCountPerThread; k++) {
							try {
								Thread.sleep(intervalTime);
							} catch (InterruptedException e) {
								// ignore.
							}
							try {
								exe();
							} catch (HttpException e) {
								errors.add(e);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						latch.countDown();
					}
					
				}
			});
			threads[i].start();
		}
		
		// 等待所有线程结束。
		try {
			latch.await();
		} catch (InterruptedException e) {
		}
		
		if (errors.size() > 0) {
			for (HttpException error : errors) {
				error.printStackTrace();
			}
		}
	}

	
	public Response exe() throws HttpException {
		ValueSources<String, String> context = new ValueSources<String, String>();
		
		// 从 spring 环境配置中取值。
		ValueSources<String, String> envContexts = new ValueSources<>();
		final ValueSource<String, String> springContext = new ValueSource<String, String>() {
			@Override
			public String get(String key) {
				return applicationContext.getEnvironment().getProperty(key);
			}
		};
		envContexts.push(springContext);
		
		// 从 http.config.json 的环境配置数据中取值。
		final String ENV_DEFAULT = "default";
		HttpClient client = session.getHttpClient();
		ValueSource<String, String> defaultEnv = client.getEnvironment(ENV_DEFAULT);
		if (defaultEnv != null) {
			envContexts.push(defaultEnv);
		}
		String activeKey = applicationContext.getEnvironment().getProperty("spring.profiles.active");
		if (!StringUtils.isEmpty(activeKey)) {
			ValueSource<String, String> activeEnv = client.getEnvironment(activeKey);
			if (activeEnv != null) {
				envContexts.push(activeEnv);
			}
		}
		
		// 纺称环境数据。
		context.push(envContexts);

		// 从本地变量 locals 中取值。
		context.push(new ValueSource<String, String>() {
			@Override
			public String get(String key) {
				String value = session.getLocals().get(key);
				if (value == null) {
					return null;
				}
				return Strings.format(value, springContext);
			}
		});
		
		// 从入参中取值。
		final Map<String, String> actualParams = new HashMap<String, String>();
		Iterator<String> it = params.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			String value = params.get(key);
			if (value != null) {
				String actualValue = Strings.format(value, context);
				actualParams.put(key, actualValue);
			}
		}
		context.push(new ValueSource<String, String>() {
			@Override
			public String get(String key) {
				String value = actualParams.get(key);
				return value;
			}
		});
		
		JsonObject result = action.exe(context, session, actualParams);
		
		if (expects != null && expects.size() > 0) {
			ValueSource<String, String> resultValues = new JsonValueSource(result);
			it = expects.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next();
				String expectedValue = expects.get(key);
				String actualValue = resultValues.get(key);
				if (!expectedValue.equals(actualValue)) {
					throw new HttpException(HttpErrorCode.EXPECT_FAILED)
							.put("expectedValue", expectedValue).put("actualValue", actualValue)
							.as(HttpException.class);
				}
			}
		}
		
		context.pop();
		context.pop();
		
		return new Response(result, session);
	}
}