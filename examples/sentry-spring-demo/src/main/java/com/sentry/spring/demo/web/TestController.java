package com.sentry.spring.demo.web;

import com.sentry.spring.demo.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@RestController
public class TestController {

    private Random random = new Random();

    @Autowired
    private TestService testService;

    @GetMapping("/test")
    public String test() {
        return random.nextBoolean() ? testService.test1() : testService.test2();
    }
}
