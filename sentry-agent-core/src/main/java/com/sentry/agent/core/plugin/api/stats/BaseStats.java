package com.sentry.agent.core.plugin.api.stats;

import com.sentry.agent.core.util.CompareUtil;
import com.sentry.agent.core.util.ExceptionUtil;
import com.sentry.agent.core.util.PrometheusRegisterHolder;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Tag;

import java.util.concurrent.atomic.AtomicLong;

public class BaseStats {

    private transient long lastMaxTime = 0L;

    // for counter
    protected final AtomicLong invokeCount = new AtomicLong(),
            errorCount = new AtomicLong(),
            runningCount = new AtomicLong(),
            maxTime = new AtomicLong(),
            concurrentMax = new AtomicLong(),
            nanoTotal = new AtomicLong();

    protected String name;

    protected Iterable<Tag> tags;

    public BaseStats(String name, Iterable<Tag> tags) {
        this.name = name;
        this.tags = tags;
    }

    public Iterable<Tag> getTags() {
        return tags;
    }

    public String getName() {
        return name;
    }

    public AtomicLong getInvokeCount() {
        return invokeCount;
    }

    public AtomicLong getErrorCount() {
        return errorCount;
    }

    public AtomicLong getRunningCount() {
        return runningCount;
    }

    public AtomicLong getMaxTime() {
        return maxTime;
    }

    public AtomicLong getConcurrentMax() {
        return concurrentMax;
    }

    public AtomicLong getNanoTotal() {
        return nanoTotal;
    }

    public long onStart() {
        long t = System.nanoTime();
        this.invokeCount.incrementAndGet();
        long currentTime = System.currentTimeMillis();
        long currentRunningCount = this.runningCount.incrementAndGet();
        if(currentTime - lastMaxTime > 60000L) {
            if (CompareUtil.setMaxValue(this.concurrentMax, currentRunningCount)) {
                lastMaxTime = currentTime;
            }
        } else {
            this.concurrentMax.set(currentRunningCount);
        }
        return t;
    }

    public void onThrowable(Throwable t) {
        this.errorCount.incrementAndGet();
        String message = t.getMessage();
        if(PrometheusRegisterHolder.getRegistry() != null) {
            Counter.builder(name + "_error").tags(tags)
                    .tag("info", message == null ? "null" : message)
                    .tag("detail", ExceptionUtil.getThrowableStackTrace(t))
                    .register(PrometheusRegisterHolder.getRegistry()).increment();
        }
    }

    public long onFinally(long start) {
        long end = System.nanoTime();
        this.runningCount.decrementAndGet();
        long used = end - start;
        this.nanoTotal.accumulateAndGet(used, Long::sum);
        long currentTime = System.currentTimeMillis();
        if(currentTime - lastMaxTime > 60000L) {
            if (CompareUtil.setMaxValue(this.maxTime, used / 1000000L)) {
                lastMaxTime = currentTime;
            }
        } else {
            this.maxTime.set(used / 1000000L);
        }
        return used / 1000000L;
    }
}
