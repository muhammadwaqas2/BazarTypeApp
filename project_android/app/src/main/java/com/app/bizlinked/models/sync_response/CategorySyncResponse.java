package com.app.bizlinked.models.sync_response;

import android.os.Parcel;
import android.os.Parcelable;

public class CategorySyncResponse extends BaseSyncResponse  implements Parcelable {

    private String id;
    private String companyId;
    private String parentId;
    private String imageId;
    private String name;
    private String description;
    private Boolean isDeleted;
    private String lastModified;
    private ImageSyncResponse image;


    public String getId() {
        return id;
    }

    public String getCompanyId() {
        return companyId;
    }

    public String getParentId() {
        return parentId;
    }

    public String getImageId() {
        return imageId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public String getLastModified() {
        return lastModified;
    }

    public ImageSyncResponse getImage() {
        return image;
    }

    public CategorySyncResponse() {}

    protected CategorySyncResponse(Parcel in) {
        id = in.readString();
        companyId = in.readString();
        parentId = in.readString();
        imageId = in.readString();
        name = in.readString();
        description = in.readString();
        image = in.readParcelable(ImageSyncResponse.class.getClassLoader());
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
        dest.writeString(parentId);
        dest.writeString(imageId);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeParcelable(image, flags);
        dest.writeInt(isDeleted ? 1 : 0);
        dest.writeString(lastModified);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<CategorySyncResponse> CREATOR = new Parcelable.Creator<CategorySyncResponse>() {
        @Override
        public CategorySyncResponse createFromParcel(Parcel in) {
            return new CategorySyncResponse(in);
        }

        @Override
        public CategorySyncResponse[] newArray(int size) {
            return new CategorySyncResponse[size];
        }
    };
}
