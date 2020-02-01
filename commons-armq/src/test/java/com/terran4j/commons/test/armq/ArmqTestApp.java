package com.terran4j.commons.test.armq;

import com.terran4j.commons.armq.ArmqConfig;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@Import(ArmqConfig.class)
@SpringBootApplication
public class ArmqTestApp {
}
