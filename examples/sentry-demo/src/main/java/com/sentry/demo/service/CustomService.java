package com.sentry.demo.service;

import java.util.IllegalFormatFlagsException;
import java.util.Random;

public class CustomService {

    private Random random = new Random();

    public void testCustom() {
        if(random.nextBoolean()) {
            throw new IllegalFormatFlagsException("illegal...");
        } else {
            System.out.println("123");
        }
    }
}
