package com.sentry.agent.core.config.props;

public class EnabledConfig {

    private boolean sql = true;

    private boolean javaMethod = true;

    private boolean url = true;

    private boolean httpClient = true;

    private boolean redis = true;

    public boolean isHttpClient() {
        return httpClient;
    }

    public void setHttpClient(boolean httpClient) {
        this.httpClient = httpClient;
    }

    public boolean isSql() {
        return sql;
    }

    public void setSql(boolean sql) {
        this.sql = sql;
    }

    public boolean isJavaMethod() {
        return javaMethod;
    }

    public void setJavaMethod(boolean javaMethod) {
        this.javaMethod = javaMethod;
    }

    public boolean isUrl() {
        return url;
    }

    public void setUrl(boolean url) {
        this.url = url;
    }

    public boolean isRedis() {
        return redis;
    }

    public void setRedis(boolean redis) {
        this.redis = redis;
    }
}
