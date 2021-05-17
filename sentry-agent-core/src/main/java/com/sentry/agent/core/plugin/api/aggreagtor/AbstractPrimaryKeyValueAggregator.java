package com.sentry.agent.core.plugin.api.aggreagtor;

import com.sentry.agent.core.plugin.api.ModelAggregator;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

public abstract class AbstractPrimaryKeyValueAggregator<KEY, V> implements ModelAggregator {

    protected volatile ConcurrentMap<KEY, V> valueStats;

    private AtomicLong count;

    public AbstractPrimaryKeyValueAggregator() {
        this(16);
    }

    public AbstractPrimaryKeyValueAggregator(int i) {
        this.count = new AtomicLong(0L);
        this.valueStats = new ConcurrentHashMap<>(i);
    }

    protected abstract Class<V> getValueType();

    protected V getValue(KEY key, boolean create) {
        V v = this.valueStats.get(key);
        if (v == null && create) {
            v = this.createValueInstance(key);
            V oldvalue = this.valueStats.putIfAbsent(key, v);
            if (oldvalue != null) {
                v = oldvalue;
            } else {
                this.count.incrementAndGet();
            }
        }
        return v;
    }

    protected V getValue(KEY key) {
        return getValue(key, true);
    }

    private V createValueInstance(KEY key) {
        try {
            Constructor<V> constructor = this.getValueType().getConstructor(key.getClass());
            return constructor.newInstance(key);
        } catch (InstantiationException e) {
            throw new RuntimeException("must have a default construct method!", e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("IllegalAccessException!", e);
        } catch (InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException("InvocationTargetException |NoSuchMethodException", e);
        }
    }

    protected void removeValue(KEY key) {
        V value = this.valueStats.remove(key);
        if (value != null) {
            this.count.decrementAndGet();
        }
    }
}
