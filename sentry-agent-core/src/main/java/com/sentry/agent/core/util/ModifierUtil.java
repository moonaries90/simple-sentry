package com.sentry.agent.core.util;

public class ModifierUtil {

    public static int computeModifierVal(String modifierText, int methodModifier) {
        int count = methodModifier;
        if (modifierText != null && !modifierText.equals("")) {
            String[] arr = modifierText.split(",");
            for(String s : arr) {
                if (!"public".equals(s)) {
                    if ("private".equals(s)) {
                        count += 2;
                    } else if ("protected".equals(s)) {
                        count += 4;
                    }
                }
            }
        }
        return count;
    }
}
