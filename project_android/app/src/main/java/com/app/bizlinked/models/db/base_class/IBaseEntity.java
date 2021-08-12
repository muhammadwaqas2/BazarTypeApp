package com.app.bizlinked.models.db.base_class;

import java.util.Date;

public interface IBaseEntity {

    Date getLastModifiedDate();
    void setLastModifiedDate(Date lastModifiedDate);
    Boolean getDirty();
    void setDirty(Boolean dirty);
    Boolean getDeleted();
    void setDeleted(Boolean deleted);

    //Create Because of Sync
    void setId(String id);
    String getId();
}
