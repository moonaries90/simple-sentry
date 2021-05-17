package com.sentry.agent.core.config.props;

import com.sentry.agent.core.util.ModifierUtil;

import java.util.regex.Pattern;

public class MethodPattern {

    public Pattern pattern;

    private String patternText;

    private String methodModifierText;

    private int methodModifier = 1;

    public MethodPattern(String patternText, String methodModifierText) {
        this.patternText = patternText;
        this.methodModifierText = methodModifierText;
    }

    public String getPatternText() {
        return this.patternText;
    }

    public void setPatternText(String patternText) {
        this.patternText = patternText;
    }

    public String getMethodModifierText() {
        return this.methodModifierText;
    }

    public void setMethodModifierText(String methodModifierText) {
        this.methodModifierText = methodModifierText;
    }

    public void compile(String parentMethodModifier) {
        if (this.patternText != null && !this.patternText.equals("")) {
            this.pattern = Pattern.compile(this.patternText);
        }
        if (this.methodModifierText == null || this.methodModifierText.equals("")) {
            this.methodModifierText = parentMethodModifier;
        }
        this.initMethodModifier();
    }

    private void initMethodModifier() {
        this.methodModifier = ModifierUtil.computeModifierVal(this.methodModifierText, 1);
    }
}
