package com.terran4j.commons.hedis.dsyn;

import java.util.UUID;

public class Server {

    private static final String instanceId = UUID.randomUUID().toString();

    public static String getInstanceId() {
        return instanceId;
    }
}
