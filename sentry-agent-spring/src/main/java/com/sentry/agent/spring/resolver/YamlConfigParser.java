package com.sentry.agent.spring.resolver;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.sentry.agent.core.config.props.SentryAgentConfig;
import com.sentry.agent.core.config.resolver.ConfigParser;
import com.sentry.agent.core.config.resolver.ConfigResolver;

import java.io.IOException;

public class YamlConfigParser implements ConfigParser {

    private static final ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @Override
    public SentryAgentConfig parse(ConfigResolver resolver) {
        try {
            JsonNode jsonNode = objectMapper.readTree(resolver.resolve());
            jsonNode = jsonNode.at("/sentry/agent");
            return objectMapper.readValue(jsonNode.traverse(), SentryAgentConfig.class);
        } catch (IOException ignore) {
            return null;
        }
    }
}
