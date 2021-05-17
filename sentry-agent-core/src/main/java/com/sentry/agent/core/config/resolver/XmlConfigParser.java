package com.sentry.agent.core.config.resolver;

import com.sentry.agent.core.config.Method;
import com.sentry.agent.core.config.props.EnabledConfig;
import com.sentry.agent.core.config.props.JavaMethodPatternConfig;
import com.sentry.agent.core.config.props.SentryAgentConfig;
import com.sentry.agent.core.config.props.UrlConfig;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XmlConfigParser implements ConfigParser {

    @Override
    public SentryAgentConfig parse(ConfigResolver resolver) {
        try {
            SentryAgentConfig agentConfig = new SentryAgentConfig();
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(resolver.resolve());
            Element element = document.getDocumentElement();
            NodeList javaMethodNodes = element.getElementsByTagName("javaMethod");
            if(javaMethodNodes.getLength() > 0) {
                agentConfig.setJavaMethods(resolveJavaMethod((Element) javaMethodNodes.item(0)));
            }

            NodeList urlNodes = element.getElementsByTagName("url");
            if(urlNodes.getLength() > 0) {
                agentConfig.setUrl(resolverUrlConfig((Element) urlNodes.item(0)));
            }

            NodeList tagsNodes = element.getElementsByTagName("tags");
            if(tagsNodes.getLength() > 0) {
                agentConfig.setTags(resolveTags((Element) tagsNodes.item(0)));
            }

            NodeList enabledNodes = element.getElementsByTagName("enabled");
            if(enabledNodes.getLength() > 0) {
                agentConfig.setEnabled(resolveEnabled((Element) enabledNodes.item(0)));
            }
            return agentConfig;
        } catch (Exception _ignore) {
            return null;
        }
    }

    private Map<String, String> resolveTags(Element tagElement) {
        Map<String, String> result = new HashMap<>();
        NodeList nodeList = tagElement.getElementsByTagName("tag");
        for(int i = 0; i < nodeList.getLength(); i ++) {
            Element element = (Element) nodeList.item(i);
            result.put(element.getAttribute("key"), element.getAttribute("value"));
        }
        return result;
    }

    private EnabledConfig resolveEnabled(Element element) {
        EnabledConfig enabledConfig = new EnabledConfig();
        NodeList nodeList = element.getElementsByTagName("item");
        if(nodeList.getLength() > 0) {
            for(int i = 0; i < nodeList.getLength(); i ++) {
                Element item = (Element) nodeList.item(i);
                String name = item.getAttribute("name");
                String enabled = item.getAttribute("value");
                if(name != null && "false".equals(enabled)) {
                    switch (name) {
                        case "javaMethod":
                            enabledConfig.setJavaMethod(false);
                            break;
                        case "sql":
                            enabledConfig.setSql(false);
                            break;
                        case "url":
                            enabledConfig.setUrl(false);
                            break;
                        case "httpClient":
                            enabledConfig.setHttpClient(false);
                            break;
                        case "redis":
                            enabledConfig.setRedis(false);
                            break;
                    }
                }
            }
        }
        return enabledConfig;
    }

    private List<JavaMethodPatternConfig> resolveJavaMethod(Element element) {
        List<JavaMethodPatternConfig> configs = new ArrayList<>();
        NodeList patternNodes = element.getElementsByTagName("class");
        for (int i = 0; i < patternNodes.getLength(); i++) {
            Element classElement = (Element) patternNodes.item(i);
            JavaMethodPatternConfig config = new JavaMethodPatternConfig();
            config.setPattern(classElement.getAttribute("pattern"));
            config.setMethodModifier(classElement.getAttribute("methodModifier"));
            config.setIgnoredExceptions(classElement.getAttribute("ignoredExceptions"));

            NodeList methods = classElement.getElementsByTagName("method");
            if (methods.getLength() > 0) {
                List<JavaMethodPatternConfig> methodConfigs = new ArrayList<>();
                for (int m = 0; m < methods.getLength(); m++) {
                    Element methodElement = (Element) methods.item(m);
                    JavaMethodPatternConfig methodConfig = new JavaMethodPatternConfig();
                    methodConfig.setPattern(methodElement.getAttribute("pattern"));
                    methodConfig.setMethodModifier(methodElement.getAttribute("methodModifier"));
                    methodConfigs.add(methodConfig);
                }
                config.setMethods(methodConfigs);
            }

            NodeList annotations = classElement.getElementsByTagName("annotation");
            if (annotations.getLength() > 0) {
                List<JavaMethodPatternConfig> annotationConfigs = new ArrayList<>();
                for (int m = 0; m < methods.getLength(); m++) {
                    Element methodElement = (Element) methods.item(m);
                    JavaMethodPatternConfig annotationConfig = new JavaMethodPatternConfig();
                    annotationConfig.setPattern(methodElement.getAttribute("pattern"));
                    annotationConfig.setMethodModifier(methodElement.getAttribute("methodModifier"));
                    annotationConfigs.add(annotationConfig);
                }
                config.setAnnotations(annotationConfigs);
            }
            configs.add(config);
        }
        return configs;
    }

    private UrlConfig resolverUrlConfig(Element element) {
        UrlConfig config = new UrlConfig();

        NodeList includes = element.getElementsByTagName("include");
        if(includes.getLength() > 0) {
            List<UrlConfig.IncludeConfig> includeConfigs = new ArrayList<>();
            for(int i = 0; i < includes.getLength(); i ++) {
                UrlConfig.IncludeConfig includeConfig = new UrlConfig.IncludeConfig();
                Element include = (Element) includes.item(i);
                includeConfig.setAction(include.getAttribute("action"));
                includeConfig.setMethod(Method.parseByName(include.getAttribute("method")));
                includeConfig.setPattern(include.getAttribute("pattern"));
                includeConfig.setTarget(include.getAttribute("target"));
                includeConfig.setSuccessCode(include.getAttribute("successCode"));
                includeConfigs.add(includeConfig);
            }
            config.setIncludes(includeConfigs);
        }

        NodeList excludes = element.getElementsByTagName("exclude");
        if(includes.getLength() > 0) {
            List<UrlConfig.ExcludeConfig> excludeConfigs = new ArrayList<>();
            for(int i = 0; i < excludes.getLength(); i ++) {
                UrlConfig.ExcludeConfig excludeConfig = new UrlConfig.ExcludeConfig();
                Element include = (Element) excludes.item(i);
                excludeConfig.setMethod(Method.parseByName(include.getAttribute("method")));
                excludeConfig.setPattern(include.getAttribute("pattern"));
                excludeConfigs.add(excludeConfig);
            }
            config.setExcludes(excludeConfigs);
        }
        return config;
    }
}
