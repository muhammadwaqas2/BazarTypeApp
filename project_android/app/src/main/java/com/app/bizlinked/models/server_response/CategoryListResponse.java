package com.app.bizlinked.models.server_response;

import android.os.Parcel;
import android.os.Parcelable;

import com.app.bizlinked.models.sync_response.BaseSyncResponse;
import com.app.bizlinked.models.sync_response.CategorySyncResponse;
import com.app.bizlinked.models.sync_response.ImageSyncResponse;
import com.app.bizlinked.models.sync_response.ProfileSyncResponse;

import java.util.ArrayList;

public class CategoryListResponse implements Parcelable {

    private ArrayList<CategorySyncResponse> content = null;
    private Integer size;
    private Integer totalPages;

    public ArrayList<CategorySyncResponse> getContent() {
        return content;
    }

    public Integer getSize() {
        return size;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public CategoryListResponse() {}

    protected CategoryListResponse(Parcel in) {
        size = in.readInt();
        totalPages = in.readInt();
        in.readList(content, CategorySyncResponse.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(size);
        dest.writeInt(totalPages);
        dest.writeTypedList(content);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<CategoryListResponse> CREATOR = new Parcelable.Creator<CategoryListResponse>() {
        @Override
        public CategoryListResponse createFromParcel(Parcel in) {
            return new CategoryListResponse(in);
        }

        @Override
        public CategoryListResponse[] newArray(int size) {
            return new CategoryListResponse[size];
        }
    };

}
