package com.sentry.agent.core.util;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class IntegerMap {

    private String[] resources;

    private ConcurrentMap<String, Integer> concurrentHashMap = new ConcurrentHashMap<>();

    private volatile boolean isFull = false;

    private volatile int size = 0;

    private volatile int max;

    public IntegerMap(int max) {
        if (max > 2000) {
            throw new RuntimeException("exeed the max value:2000");
        } else {
            this.resources = new String[max];
            this.max = max;
        }
    }

    @DoNotChange
    public int registerResource(String r) {
        Integer b = this.concurrentHashMap.get(r);
        if (b != null) {
            return b;
        } else {
            return this.isFull ? -1 : this.syncRegister(r);
        }
    }

    private synchronized int syncRegister(String r) {
        Integer b = this.concurrentHashMap.get(r);
        if (b != null) {
            return b;
        } else if (this.isFull) {
            return -1;
        } else {
            this.resources[this.size] = r;
            this.concurrentHashMap.put(r, this.size);
            int c = this.size++;
            if (this.size >= this.max) {
                this.isFull = true;
            }
            return c;
        }
    }

    public int size() {
        return this.size;
    }

    public final String getResource(int b) {
        return b >= this.max ? null : this.resources[b];
    }
}
