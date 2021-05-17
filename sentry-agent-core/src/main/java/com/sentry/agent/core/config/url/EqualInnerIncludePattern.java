package com.sentry.agent.core.config.url;

public class EqualInnerIncludePattern extends AbstractUrlIncludePattern {

    private AbstractUrlIncludePattern parent;

    public EqualInnerIncludePattern() {
        super( null,  null,  null);
    }

    public boolean isValid() {
        return null != this.parent && this.parent.isValid();
    }

    public boolean matches(String url) {
        return null == this.parent || this.parent.matches(url);
    }

    public AbstractUrlIncludePattern getParent() {
        return this.parent;
    }

    public void setParent(AbstractUrlIncludePattern parent) {
        if (null == this.parent) {
            this.parent = parent;
        }
    }
}
