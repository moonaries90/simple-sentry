package com.sentry.agent.core.config.resolver;

import java.io.InputStream;

public class DefaultConfigResolver implements ConfigResolver {

    private final String location;

    public DefaultConfigResolver(String location) {
        this.location = location;
    }

    @Override
    public InputStream resolve() {
        String _location = location;
        if(_location == null) {
            return null;
        }
        if (_location.startsWith("classpath:")) {
            _location = _location.substring("classpath:".length());
        }
        return Thread.currentThread().getContextClassLoader().getResourceAsStream(_location);
    }
}
