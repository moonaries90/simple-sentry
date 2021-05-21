package com.sentry.agent.core.plugin.asm.sql.mysql;

import com.sentry.agent.core.plugin.api.PrimaryKey;
import com.sentry.agent.core.plugin.asm.AsmAdviceListener;
import com.sentry.agent.core.plugin.asm.AsmAggregator;
import com.sentry.agent.core.plugin.asm.sql.SqlHelper;
import com.sentry.agent.core.util.IntegerMap;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class MysqlAdviceListener extends AsmAdviceListener {

    private static final MysqlAggregator mysqlAggregator = new MysqlAggregator();

    private static final IntegerMap sqlCounter = new IntegerMap(2000);

    @Override
    protected PrimaryKey preparePrimaryKey(ClassLoader loader, String className, String methodName, String methodDesc, Object target, Object[] args) throws Throwable {
        Field connectionField = target.getClass().getField("connection");
        connectionField.setAccessible(true);
        Object connection = connectionField.get(target);

        Field origHostToConnectToField = connection.getClass().getField("origHostToConnectTo");
        Field origPortToConnectToField = connection.getClass().getField("origPortToConnectTo");
        Field databaseField = connection.getClass().getField("database");

        String url = SqlHelper.getDbName(
                (String) origHostToConnectToField.get(connection),
                (int) origPortToConnectToField.get(connection),
                (String) databaseField.get(connection));

        int cnId = SqlHelper.getConnectionId(url);
        String sql = null;
        if(cnId >= 0) {
            switch (className) {
                case "com.mysql.jdbc.PreparedStatement": {
                    Field originalSqlField = target.getClass().getField("originalSql");
                    sql = (String) originalSqlField.get(target);
                    break;
                }
                case "com.mysql.cj.jdbc.ClientPreparedStatement": {
                    Field queryField = target.getClass().getField("query");
                    Object query = queryField.get(target);
                    Method getOriginalSqlMethod = query.getClass().getMethod("getOriginalSql");
                    sql = (String) getOriginalSqlMethod.invoke(query);
                    break;
                }
                case "com.mysql.jdbc.StatementImpl":
                case "com.mysql.cj.jdbc.StatementImpl":
                    if (args != null && args.length > 0) {
                        sql = String.valueOf(args[0]);
                    }
                    break;
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
    protected AsmAggregator<?> getAggregator() {
        return mysqlAggregator;
    }
}
