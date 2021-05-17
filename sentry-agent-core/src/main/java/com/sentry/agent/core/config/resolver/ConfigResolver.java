package com.sentry.agent.core.config.resolver;

import java.io.InputStream;

public interface ConfigResolver {

    InputStream resolve();
}
