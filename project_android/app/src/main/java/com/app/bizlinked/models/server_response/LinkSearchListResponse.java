package com.app.bizlinked.models.server_response;

import android.os.Parcel;
import android.os.Parcelable;

import com.app.bizlinked.models.sync_response.BaseSyncResponse;
import com.app.bizlinked.models.sync_response.ProfileSyncResponse;

import java.util.ArrayList;

public class LinkSearchListResponse extends BaseSyncResponse implements Parcelable {

    private ArrayList<ProfileSyncResponse> content = null;
    private Integer size;
    private Integer totalPages;

    public ArrayList<ProfileSyncResponse> getContent() {
        return content;
    }

    public Integer getSize() {
        return size;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public LinkSearchListResponse() {}

    protected LinkSearchListResponse(Parcel in) {
        size = in.readInt();
        totalPages = in.readInt();
        in.readList(content, ProfileSyncResponse.class.getClassLoader());
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
    public static final Parcelable.Creator<LinkSearchListResponse> CREATOR = new Parcelable.Creator<LinkSearchListResponse>() {
        @Override
        public LinkSearchListResponse createFromParcel(Parcel in) {
            return new LinkSearchListResponse(in);
        }

        @Override
        public LinkSearchListResponse[] newArray(int size) {
            return new LinkSearchListResponse[size];
        }
    };

    @Override
    public String getId() {
        return null;
    }

    @Override
    public String getLastModified() {
        return null;
    }
}


