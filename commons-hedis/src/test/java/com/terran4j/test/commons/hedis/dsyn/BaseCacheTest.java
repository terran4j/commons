package com.terran4j.test.commons.hedis.dsyn;

import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.TestExecutionListeners;

import com.terran4j.commons.hedis.cache.CacheService;
import com.terran4j.test.commons.hedis.BaseSpringBootTest;
import com.terran4j.test.commons.hedis.MockitoInitializer;

@SpringBootTest(classes = { CacheTestApplication.class }, webEnvironment = WebEnvironment.NONE)
@TestExecutionListeners({ MockitoInitializer.class })
public abstract class BaseCacheTest extends BaseSpringBootTest {

	private static Logger log = LoggerFactory.getLogger(BaseCacheTest.class);

	@Autowired
	protected CacheService cacheService;
	
	@Autowired
	protected HomeService homeService;
	
	@Autowired
	protected DSynchronizedService dSynchronizedService;
	
	protected final String key = "terran4j";

	@Before
	public void setUp() {
		cacheService.remove("home-" + key);
		homeService.clear();
		log.info(StringUtils.center(" test start ", 50, "="));
	}

	@After
	public void tearDown() {
		log.info(StringUtils.center(" test end ", 50, "="));
	}
}
