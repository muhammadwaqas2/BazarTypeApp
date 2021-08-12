package com.app.bizlinked.models.server_response;

import android.os.Parcel;
import android.os.Parcelable;

import com.app.bizlinked.models.sync_response.CategorySyncResponse;
import com.app.bizlinked.models.sync_response.ProductSyncResponse;

import java.util.ArrayList;

public class ProductListResponse implements Parcelable {

    private ArrayList<ProductSyncResponse> content = null;
    private Integer size;
    private Integer totalPages;

    public ArrayList<ProductSyncResponse> getContent() {
        return content;
    }

    public Integer getSize() {
        return size;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public ProductListResponse() {}

    protected ProductListResponse(Parcel in) {
        size = in.readInt();
        totalPages = in.readInt();
        in.readList(content, ProductSyncResponse.class.getClassLoader());
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
    public static final Creator<ProductListResponse> CREATOR = new Creator<ProductListResponse>() {
        @Override
        public ProductListResponse createFromParcel(Parcel in) {
            return new ProductListResponse(in);
        }

        @Override
        public ProductListResponse[] newArray(int size) {
            return new ProductListResponse[size];
        }
    };

}
