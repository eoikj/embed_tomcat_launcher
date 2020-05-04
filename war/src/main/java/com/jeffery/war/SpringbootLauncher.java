package com.jeffery.war;

import com.jeffery.war.util.Util;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class SpringbootLauncher extends SpringBootServletInitializer {
    public static void main(String[] args) {
        SpringApplication.run(SpringbootLauncher.class, args);
    }

    @RequestMapping("/*")
    public String sample(){
        return Util.test();
    }
}
