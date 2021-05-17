package com.sentry.agent.core.plugin.api.aggreagtor;

import com.sentry.agent.core.plugin.api.PrimaryKey;

public abstract class MultiPrimaryKeyAggregator<T> extends AbstractPrimaryKeyValueAggregator<PrimaryKey, T> {

    protected abstract int primaryKeyLength();

    protected T getValue(String... pks) {
        int c = this.primaryKeyLength();
        if (pks.length != c) {
            throw new RuntimeException("primary key field count must equal with what you defined:" + c + ",actual:" + pks.length);
        } else {
            PrimaryKey pk = new PrimaryKey(pks);
            return super.getValue(pk);
        }
    }

    protected void removeValue(String... pks) {
        int c = this.primaryKeyLength();
        if (pks.length != c) {
            throw new RuntimeException("primary key field count must equal with what you defined:" + c + ",actual:" + pks.length);
        } else {
            PrimaryKey pk = new PrimaryKey(pks);
            super.removeValue(pk);
        }
    }
}
