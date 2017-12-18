package com.terran4j.commons.website.controller

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Controller
import org.springframework.util.StringUtils
import org.springframework.web.bind.annotation.RequestMapping

@Controller
class WelcomeController {

    @Value("\${server.website.welcome:index.html}")
    private val welcomePath: String = "index.html"

    @RequestMapping("/")
    fun index(): String? {
        if (StringUtils.isEmpty(welcomePath)) {
            return null
        }
        var path = welcomePath.trim()
        if ("/" == path) {
            return null
        }
        if (!path.startsWith("/")) {
            path = "/" + path
        }
        return "redirect:" + path
    }

}