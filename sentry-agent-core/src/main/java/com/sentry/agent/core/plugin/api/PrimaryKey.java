package com.sentry.agent.core.plugin.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PrimaryKey {

    private final String[] keys;

    public List<String> getKeys() {
        return new ArrayList<>(Arrays.asList(keys));
    }

    public PrimaryKey(String... pks) {
        if (pks != null && pks.length >= 1) {
            this.keys = pks;
        } else {
            throw new RuntimeException("must have at least one");
        }
    }

    public String name() {
        StringBuilder s = new StringBuilder();
        for(String key : keys) {
            s.append(key).append('_');
        }
        s.deleteCharAt(s.length() - 1);
        return s.toString();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for(String s : this.keys) {
            sb.append(s).append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append("]");
        return sb.toString();
    }

    public String get(int index) {
        return this.keys[index];
    }

    public int getKeyLength() {
        return this.keys.length;
    }

    public int hashCode() {
        return Arrays.hashCode(this.keys);
    }

    public boolean equals(Object o) {
        if (o instanceof PrimaryKey) {
            PrimaryKey pk = (PrimaryKey)o;
            return Arrays.equals(this.keys, pk.keys);
        } else {
            return false;
        }
    }
}
