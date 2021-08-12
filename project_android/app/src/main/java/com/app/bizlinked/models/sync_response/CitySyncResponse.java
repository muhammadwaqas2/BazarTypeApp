package com.app.bizlinked.models.sync_response;

import android.os.Parcel;
import android.os.Parcelable;

public class CitySyncResponse extends BaseSyncResponse implements Parcelable {

    private String id;
    private String companyId;
    private String name;
    private Boolean isDeleted;
    private String lastModified;


    public String getId() {
        return id;
    }

    public String getCompanyId() {
        return companyId;
    }

    public String getName() {
        return name;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public String getLastModified() {
        return lastModified;
    }

    public CitySyncResponse() {}

    protected CitySyncResponse(Parcel in) {
        id = in.readString();
        companyId = in.readString();
        name = in.readString();
        isDeleted = in.readInt() == 1;
        lastModified = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(companyId);
        dest.writeString(name);
        dest.writeInt(isDeleted ? 1 : 0);
        dest.writeString(lastModified);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<CitySyncResponse> CREATOR = new Parcelable.Creator<CitySyncResponse>() {
        @Override
        public CitySyncResponse createFromParcel(Parcel in) {
            return new CitySyncResponse(in);
        }

        @Override
        public CitySyncResponse[] newArray(int size) {
            return new CitySyncResponse[size];
        }
    };


}

