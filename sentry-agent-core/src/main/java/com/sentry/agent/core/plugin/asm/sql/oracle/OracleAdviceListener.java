package com.sentry.agent.core.plugin.asm.sql.oracle;

import com.sentry.agent.core.plugin.api.PrimaryKey;
import com.sentry.agent.core.plugin.asm.AsmAdviceListener;
import com.sentry.agent.core.plugin.asm.sql.SqlHelper;
import com.sentry.agent.core.util.IntegerMap;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class OracleAdviceListener extends AsmAdviceListener {

    private static final OracleAggregator oracleAggregator = new OracleAggregator();

    private static final IntegerMap sqlCounter = new IntegerMap(2000);

    @Override
    protected PrimaryKey preparePrimaryKey(ClassLoader loader, String className, String methodName, String methodDesc, Object target, Object[] args) throws Throwable {
        Field connectionField = target.getClass().getField("connection");
        connectionField.setAccessible(true);
        Object connection = connectionField.get(target);
        Field urlField = connection.getClass().getField("url");
        urlField.setAccessible(true);
        String url = (String) urlField.get(connection);
        int cnId = SqlHelper.getConnectionId(url);
        String sql = null;
        if (cnId >= 0) {
            if (className.equals("oracle.jdbc.driver.OracleStatement")) {
                if (args != null && args.length > 0) {
                    sql = String.valueOf(args[0]);
                }
            } else if (className.equals("oracle.jdbc.driver.OraclePreparedStatement")) {
                Method getOriginalSql = target.getClass().getMethod("getOriginalSql");
                getOriginalSql.setAccessible(true);
                sql = (String) getOriginalSql.invoke(target);
            }
        }
        if(sql != null && sql.length() > 0) {
            if(sqlCounter.registerResource(sql) >= 0) {
                return new PrimaryKey(sql, String.valueOf(cnId));
            }
        }
        return null;
    }

    @Override
    protected OracleAggregator getAggregator() {
        return oracleAggregator;
    }
}
