package com.terran4j.test.hi;

import com.terran4j.commons.hi.HttpClient;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(
        classes = {TestHiApp.class},
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT
)
public class BaseHiTest {

    @Autowired
    protected ApplicationContext context;

    protected final HttpClient create() {
        return HttpClient.create(HttpClientTest.class,
                this.getClass().getSimpleName() + ".json",
                context);
    }
}
