package com.app.bizlinked.models.sync_response;

import android.os.Parcelable;
import java.util.ArrayList;
import java.io.Serializable;

public class SyncResponse<T> implements Serializable {

    ArrayList<T> delta = new ArrayList();
    private String lastSyncDate;


    public ArrayList<T> getDelta() {
        return delta;
    }

    public void setDelta(ArrayList<T> delta) {
        this.delta = delta;
    }

    public String getLastSyncDate() {
        return lastSyncDate;
    }

    public void setLastSyncDate(String lastSyncDate) {
        this.lastSyncDate = lastSyncDate;
    }
}
