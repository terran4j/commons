package com.terran4j.commons.website.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class WelcomeController {

    @Value("${server.website.welcome:index.html}")
    private String welcomePath = "index.html";

    /**
     * 欢迎页
     */
    @RequestMapping("/")
    public String index() {
        if (StringUtils.isEmpty(welcomePath)) {
            welcomePath = "index.html";
        }
        String path = welcomePath.trim();
        if ("/" == path) {
            return null;
        }
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        return "redirect:" + path;
    }

}
