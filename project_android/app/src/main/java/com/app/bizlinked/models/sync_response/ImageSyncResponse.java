package com.app.bizlinked.models.sync_response;

import android.os.Parcel;
import android.os.Parcelable;

public class ImageSyncResponse extends BaseSyncResponse implements Parcelable{

    private String id;
    private String companyId;
    private String filePath;
    private Boolean isDeleted;
    private Boolean isImageAvailable;
    private String lastModified;

    public String getId() {
        return id;
    }

    public String getCompanyId() {
        return companyId;
    }

    public String getFilePath() {
        return filePath;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public Boolean getImageAvailable() {
        return isImageAvailable;
    }

    public String getLastModified() {
        return lastModified;
    }

    public ImageSyncResponse() {}

    protected ImageSyncResponse(Parcel in) {
        id = in.readString();
        companyId = in.readString();
        filePath = in.readString();
        isDeleted = in.readInt() == 1;
        isImageAvailable = in.readInt() == 1;
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
        dest.writeString(filePath);
        dest.writeInt(isDeleted ? 1 : 0);
        dest.writeInt(isImageAvailable ? 1 : 0);
        dest.writeString(lastModified);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<ImageSyncResponse> CREATOR = new Parcelable.Creator<ImageSyncResponse>() {
        @Override
        public ImageSyncResponse createFromParcel(Parcel in) {
            return new ImageSyncResponse(in);
        }

        @Override
        public ImageSyncResponse[] newArray(int size) {
            return new ImageSyncResponse[size];
        }
    };
}
