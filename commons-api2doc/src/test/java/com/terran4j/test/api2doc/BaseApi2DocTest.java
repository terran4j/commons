package com.terran4j.test.api2doc;

import com.terran4j.commons.api2doc.domain.ApiDocObject;
import com.terran4j.commons.api2doc.domain.ApiFolderObject;
import com.terran4j.commons.api2doc.impl.Api2DocCollector;
import com.terran4j.commons.util.error.BusinessException;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RunWith(SpringJUnit4ClassRunner.class)
abstract public class BaseApi2DocTest {

    protected String serverURL = "http://localhost:8080";

    protected Api2DocCollector collector = new Api2DocCollector();

    protected ApiDocObject loadDoc(String methodName) throws BusinessException {
        ApiFolderObject folder = collector.toApiFolder(
                this, this.getClass().getSimpleName());
        ApiDocObject doc = folder.getDoc(methodName);
        Assert.assertNotNull(doc);
        return doc;
    }
}
