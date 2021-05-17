package com.sentry.agent.core.plugin.api.stats;

import com.sentry.agent.core.plugin.api.PrimaryKey;
import com.sentry.agent.core.config.MetricsNames;
import com.sentry.agent.core.plugin.asm.sql.SqlHelper;
import io.micrometer.core.instrument.Tags;

public class SqlStats extends SectionStats {

    public SqlStats(PrimaryKey key) {
        super(MetricsNames.SQL, Tags.of("sql", replaceLineSeparator(key.get(0)), "connection", SqlHelper.getResource(Integer.parseInt(key.get(1)))));
    }

    private static String replaceLineSeparator(String sql) {
        String line = System.lineSeparator();
        if(sql != null && sql.contains(line)) {
            sql = sql.replace(line, " ");
        }
        return sql;
    }
}
