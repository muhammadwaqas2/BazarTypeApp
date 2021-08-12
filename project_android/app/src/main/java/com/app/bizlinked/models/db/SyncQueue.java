package com.app.bizlinked.models.db;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class SyncQueue extends RealmObject {

    @PrimaryKey
    private String entityName;

    private Integer priority = 0;


    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }
}
