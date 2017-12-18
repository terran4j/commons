package com.terran4j.test.website

import com.terran4j.commons.website.config.WebsiteConfiguration
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Import
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody

@Controller
open class HelloController {

    /**
     * http://localhost:8080
     */
    @RequestMapping("/index.html")
    @ResponseBody
    fun hello(): String {
        return "hello, world"
    }

}

@Import(WebsiteConfiguration::class)
@SpringBootApplication
open class MainApp {
}

fun main(args: Array<String>) {
    SpringApplication.run(MainApp::class.java, *args)
}