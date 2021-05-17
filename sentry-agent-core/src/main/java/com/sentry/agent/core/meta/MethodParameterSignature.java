package com.sentry.agent.core.meta;

public class MethodParameterSignature {

    private final String paramSignature;

    public MethodParameterSignature(String s) {
        this.paramSignature = s;
    }

    public String getClassTypeString() {
        String mtype;
        if (this.paramSignature.startsWith("[[L")) {
            mtype = this.paramSignature.substring(3, this.paramSignature.length() - 1);
            mtype = mtype.replaceAll("/", ".");
            return mtype;
        } else if (this.paramSignature.startsWith("[L")) {
            mtype = this.paramSignature.substring(2, this.paramSignature.length() - 1);
            mtype = mtype.replaceAll("/", ".");
            return mtype;
        } else if (this.paramSignature.startsWith("L")) {
            mtype = this.paramSignature.substring(1, this.paramSignature.length() - 1);
            mtype = mtype.replaceAll("/", ".");
            return mtype;
        } else {
            return null;
        }
    }

    public String toDisplayString() {
        String ss;
        String mType;
        if (this.paramSignature.startsWith("[[L")) {
            mType = this.paramSignature.substring(3, this.paramSignature.length() - 1);
            ss = this.getDisplay(mType);
            return ss + "[][]";
        } else if (this.paramSignature.startsWith("[L")) {
            mType = this.paramSignature.substring(2, this.paramSignature.length() - 1);
            ss = this.getDisplay(mType);
            return ss + "[]";
        } else if (this.paramSignature.startsWith("L")) {
            mType = this.paramSignature.substring(1, this.paramSignature.length() - 1);
            return this.getDisplay(mType);
        } else {
            char c = this.paramSignature.charAt(this.paramSignature.length() - 1);
            ss = this.getTypeString(c);
            if (this.paramSignature.startsWith("[[")) {
                return ss + "[][]";
            } else {
                return this.paramSignature.startsWith("[") ? ss + "[]" : ss;
            }
        }
    }

    private String getDisplay(String ss) {
        ss = ss.replaceAll("/", ".");
        int idx = ss.lastIndexOf(".");
        return idx < 0 ? ss : ss.substring(idx + 1, ss.length());
    }

    private String getTypeString(char c) {
        if (c == 'Z') {
            return "boolean";
        } else if (c == 'C') {
            return "char";
        } else if (c == 'B') {
            return "byte";
        } else if (c == 'I') {
            return "int";
        } else if (c == 'F') {
            return "float";
        } else if (c == 'J') {
            return "long";
        } else if (c == 'D') {
            return "double";
        } else if (c == 'S') {
            return "short";
        } else {
            throw new RuntimeException("wrong type:" + c);
        }
    }
}
