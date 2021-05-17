package com.sentry.boot.demo.service;

import org.springframework.stereotype.Component;

@Component
public class BootService {

    public String boot1() {
        return "boot1";
    }

    public String boot2() {
        return "boot2";
    }
}
