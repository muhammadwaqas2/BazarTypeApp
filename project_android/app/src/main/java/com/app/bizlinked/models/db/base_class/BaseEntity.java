package com.app.bizlinked.models.db.base_class;

import java.util.Date;
import java.util.UUID;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class BaseEntity extends RealmObject{

    private Date lastModifiedDate = new Date();
    private Boolean isDirty = true;
    private Boolean isDeleted = false;

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public Boolean getDirty() {
        return isDirty;
    }

    public void setDirty(Boolean dirty) {
        isDirty = dirty;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }
}
