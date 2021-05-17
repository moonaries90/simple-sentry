package com.sentry.boot.demo.web;

import com.sentry.boot.demo.service.BootService;
import com.sentry.boot.demo.service.DemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@RestController
public class TestController {

    @Autowired
    private BootService bootService;

    @Autowired
    private DemoService demoService;

    private Random random = new Random();

    @GetMapping("/boot")
    public String boot() {
        return random.nextBoolean() ? bootService.boot1() : bootService.boot2();
    }

    @GetMapping("/sql")
    public String sql() {
        demoService.test();
        return "test";
    }
}
