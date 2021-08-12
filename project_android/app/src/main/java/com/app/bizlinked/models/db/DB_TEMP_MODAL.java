//package com.app.bizlinked.models.db;
//
//import com.app.bizlinked.models.db.base_class.BaseEntity;
//import com.app.bizlinked.models.db.base_class.IBaseEntity;
//
//import java.util.Date;
//import java.util.UUID;
//
//import io.realm.RealmObject;
//import io.realm.annotations.PrimaryKey;
//
//public class DB_TEMP_MODAL extends RealmObject implements IBaseEntity {
//
//    @PrimaryKey
//    private String id = UUID.randomUUID().toString();
//    private BaseEntity baseEntity;
//
//    public DB_TEMP_MODAL() {
//        this.baseEntity = new BaseEntity();
//    }
//
//    @Override
//    public Date getLastModifiedDate() {
//        return baseEntity.getLastModifiedDate();
//    }
//
//    @Override
//    public void setLastModifiedDate(Date lastModifiedDate) {
//        baseEntity.setLastModifiedDate(lastModifiedDate);
//    }
//
//    @Override
//    public Boolean getDirty() {
//        return baseEntity.getDirty();
//    }
//
//    @Override
//    public void setDirty(Boolean dirty) {
//        baseEntity.setDirty(dirty);
//    }
//
//    @Override
//    public Boolean getDeleted() {
//        return baseEntity.getDeleted();
//    }
//
//    @Override
//    public void setDeleted(Boolean deleted) {
//        baseEntity.setDeleted(deleted);
//    }
//}
//
