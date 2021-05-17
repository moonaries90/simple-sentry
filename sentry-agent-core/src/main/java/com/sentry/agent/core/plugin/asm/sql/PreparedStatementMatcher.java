package com.sentry.agent.core.plugin.asm.sql;

import com.sentry.agent.core.plugin.asm.AsmMethod;
import com.sentry.agent.core.plugin.asm.api.Matcher;
import org.objectweb.asm.Type;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

public class PreparedStatementMatcher implements Matcher<AsmMethod> {

    @Override
    public boolean match(AsmMethod target) {
       if((ACC_PUBLIC & target.access) != 0) {
           Type[] argTypes = Type.getArgumentTypes(target.desc);
           Type returnType = Type.getReturnType(target.desc);
           return argTypes.length == 0 && returnType.equals(methodReturnTypes.get(target.name));
       }
       return false;
    }

    private final Map<String, Type> methodReturnTypes = new HashMap<String,Type>(){{
        put("execute", Type.BOOLEAN_TYPE);
        put("executeQuery", Type.getType(ResultSet.class));
        put("executeUpdate", Type.INT_TYPE);
    }};
}
