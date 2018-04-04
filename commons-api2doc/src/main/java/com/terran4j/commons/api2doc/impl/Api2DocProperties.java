package com.terran4j.commons.api2doc.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class Api2DocProperties {

    @Value("${server.url:http://localhost:8080}")
    private String serverURL;

    @Value("${api2doc.title:true}")
    private boolean showCurl = true;

    @Value("${service.name:}")
    private String serviceName;

    @Value("${api2doc.title:}")
    private String api2docTitle;

    @Value("${api2doc.icon:}")
    private String api2docIcon;

    public String getServerURL() {
        return serverURL;
    }

    public boolean isShowCurl() {
        return showCurl;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getApi2docTitle() {
        return api2docTitle;
    }

    public String getApi2docIcon() {
        return api2docIcon;
    }
}
