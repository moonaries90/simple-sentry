package com.sentry.agent.core.config.props;

import com.sentry.agent.core.config.Method;

import java.util.List;

public class UrlConfig {

    private List<IncludeConfig> includes;

    private List<ExcludeConfig> excludes;

    public List<IncludeConfig> getIncludes() {
        return includes;
    }

    public void setIncludes(List<IncludeConfig> includes) {
        this.includes = includes;
    }

    public List<ExcludeConfig> getExcludes() {
        return excludes;
    }

    public void setExcludes(List<ExcludeConfig> excludes) {
        this.excludes = excludes;
    }

    public static class IncludeConfig {

        // 匹配方法
        private Method method;

        // 与 method 一同构成匹配规则
        private String pattern;

        /**
         * 当指定 target 时， 忽略真实的 url 请求，仅以 target 表示， 可以与 action 一同生效
         */
        private String target;

        /**
         * 配置方式为 param=(param1,param2) 或者仅写 param
         * 作用是将相同 URL 的请求，以参数作为区分
         * 当通过 (param1, param2) 做了限定时， 仅 param in (param1, param2) 时才会生效
         */
        private String action;

        /**
         * 逗号分隔
         * 默认200, 301, 302
         * 非 successCode 会被记录到 errorCount，见AbstractUrlIncludePattern.defaultSuccCodeValue
         */
        private String successCode;

        public Method getMethod() {
            return method;
        }

        public void setMethod(Method method) {
            this.method = method;
        }

        public String getPattern() {
            return pattern;
        }

        public void setPattern(String pattern) {
            this.pattern = pattern;
        }

        public String getTarget() {
            return target;
        }

        public void setTarget(String target) {
            this.target = target;
        }

        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }

        public String getSuccessCode() {
            return successCode;
        }

        public void setSuccessCode(String successCode) {
            this.successCode = successCode;
        }
    }

    public static class ExcludeConfig {

        private String pattern;

        private Method method;

        public String getPattern() {
            return pattern;
        }

        public void setPattern(String pattern) {
            this.pattern = pattern;
        }

        public Method getMethod() {
            return method;
        }

        public void setMethod(Method method) {
            this.method = method;
        }
    }




}
