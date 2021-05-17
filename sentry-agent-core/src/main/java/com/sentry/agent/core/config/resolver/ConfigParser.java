package com.sentry.agent.core.config.resolver;

import com.sentry.agent.core.config.props.SentryAgentConfig;

public interface ConfigParser {

    SentryAgentConfig parse(ConfigResolver resolver);
}
