package com.app.bizlinked.models.sync_response;

import android.os.Parcel;
import android.os.Parcelable;

public class ProductImageSyncResponse extends BaseSyncResponse implements Parcelable {

    private String id;
    private String imageId;
    private String productId;
    private Boolean isDeleted;
    private String lastModified;
    private ImageSyncResponse image;

    public String getId() {
        return id;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    public String getLastModified() {
        return lastModified;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }

    public ImageSyncResponse getImage() {
        return image;
    }

    public ProductImageSyncResponse() { }

    protected ProductImageSyncResponse(Parcel in) {
        id = in.readString();
        imageId = in.readString();
        productId = in.readString();
        isDeleted = in.readInt() == 1;
        lastModified = in.readString();
        image = in.readParcelable(ImageSyncResponse.class.getClassLoader());

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(imageId);
        dest.writeString(productId);
        dest.writeInt(isDeleted ? 1 : 0);
        dest.writeString(lastModified);
        dest.writeParcelable(image, flags);

    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<ProductImageSyncResponse> CREATOR = new Parcelable.Creator<ProductImageSyncResponse>() {
        @Override
        public ProductImageSyncResponse createFromParcel(Parcel in) {
            return new ProductImageSyncResponse(in);
        }

        @Override
        public ProductImageSyncResponse[] newArray(int size) {
            return new ProductImageSyncResponse[size];
        }
    };

}
