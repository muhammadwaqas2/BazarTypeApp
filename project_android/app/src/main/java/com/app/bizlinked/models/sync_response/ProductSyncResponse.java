package com.app.bizlinked.models.sync_response;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class ProductSyncResponse extends BaseSyncResponse implements Parcelable {

    private String id;
    private String companyId;
    private String categoryId;
    private CategorySyncResponse productCategory;
    private ArrayList<ProductImageSyncResponse> productImages;
    private String name;
    private String description;
    private Double price;
    private Boolean isActive;
    private Boolean isPublished;
    private Boolean isDeleted;
    private String lastModified;


    public String getId() {
        return id;
    }

    public String getCompanyId() {
        return companyId;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public CategorySyncResponse getProductCategory() {
        return productCategory;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Double getPrice() {
        return price;
    }

    public Boolean getActive() {
        return isActive;
    }

    public Boolean getPublished() {
        return isPublished;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public String getLastModified() {
        return lastModified;
    }

    public ArrayList<ProductImageSyncResponse> getProductImages() {
        return productImages;
    }

    public ProductSyncResponse() { }

    protected ProductSyncResponse(Parcel in) {
        id = in.readString();
        companyId = in.readString();
        categoryId = in.readString();
        name = in.readString();
        description = in.readString();
        price = in.readDouble();
        productCategory = in.readParcelable(CategorySyncResponse.class.getClassLoader());
        isActive = in.readInt() == 1;
        isPublished = in.readInt() == 1;
        isDeleted = in.readInt() == 1;
        lastModified = in.readString();
        in.readList(productImages, ProductImageSyncResponse.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(companyId);
        dest.writeString(categoryId);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeDouble(price);
        dest.writeParcelable(productCategory, flags);
        dest.writeInt(isActive ? 1 : 0);
        dest.writeInt(isPublished ? 1 : 0);
        dest.writeInt(isDeleted ? 1 : 0);
        dest.writeString(lastModified);
        dest.writeList(productImages);

    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<ProductSyncResponse> CREATOR = new Parcelable.Creator<ProductSyncResponse>() {
        @Override
        public ProductSyncResponse createFromParcel(Parcel in) {
            return new ProductSyncResponse(in);
        }

        @Override
        public ProductSyncResponse[] newArray(int size) {
            return new ProductSyncResponse[size];
        }
    };

}
