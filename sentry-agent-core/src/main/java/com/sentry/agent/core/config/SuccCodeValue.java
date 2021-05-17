package com.sentry.agent.core.config;

import java.util.*;

public class SuccCodeValue {

    private final List<Integer> values = new ArrayList<>();
    private final Set<Interval> ins = new HashSet<>();

    public SuccCodeValue(String value) {
        String[] vs = value.split(",");
        for(String v : vs) {
            v = v.trim();
            if (v != null && v.length() > 0) {
                if (!v.contains("-")) {
                    Integer t = Integer.valueOf(v);
                    if (!this.values.contains(t)) {
                        this.values.add(t);
                    }
                } else {
                    String[] vv = v.split("-");
                    if (2 != vv.length) {
                        throw new IllegalArgumentException("'successCode' " + v + " is illegal.");
                    }
                    int s = Integer.parseInt(vv[0]);
                    int e = Integer.parseInt(vv[1]);
                    if (e <= s) {
                        throw new IllegalArgumentException("'successCode' " + v + " not valid.");
                    }

                    this.ins.add(new SuccCodeValue.Interval(s, e));
                }
            }
        }

        this.values.sort(Comparator.comparingInt(o -> o));
    }

    public boolean isSuccCode(int code) {
        if (Collections.binarySearch(this.values, code) >= 0) {
            return true;
        } else {
            for(Interval in : this.ins) {
                if(in.isSuccCode(code)) {
                    return true;
                }
            }
            return false;
        }
    }

    private static class Interval {
        private final int start;
        private final int end;

        public Interval(int start, int end) {
            this.start = start;
            this.end = end;
        }

        public boolean isSuccCode(int code) {
            return code >= this.start && code <= this.end;
        }

        public String toString() {
            return this.start + "-->" + this.end;
        }
    }
}
