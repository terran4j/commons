package com.terran4j.commons.website.controller

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Controller
import org.springframework.util.StringUtils
import org.springframework.web.bind.annotation.RequestMapping

/**
 *  欢迎页
 * @author terran4j
 */
@Controller
open class WelcomeController {

    @Value("\${server.website.welcome:index.html}")
    var welcomePath: String = "index.html"

    /**
     * 欢迎页
     */
    @RequestMapping("/")
    fun index(): String? {
        if (StringUtils.isEmpty(welcomePath)) {
            welcomePath = "index.html"
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