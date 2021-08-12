package com.app.bizlinked.models.sync_response;

import android.os.Parcel;
import android.os.Parcelable;

public class OrderDetailSyncResponse extends BaseSyncResponse implements Parcelable {


    private String id;
    private String orderId;
    private Double price;
    private String productCategoryId;
    private String productCategoryName;
    private String productId;
    private String productImageId;
    private String productName;
    private Integer quantity;
    private Boolean isDeleted;
    private String lastModified;

    public String getId() {
        return id;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public String getLastModified() {
        return lastModified;
    }


    public String getOrderId() {
        return orderId;
    }

    public Double getPrice() {
        return price;
    }

    public String getProductCategoryId() {
        return productCategoryId;
    }

    public String getProductCategoryName() {
        return productCategoryName;
    }

    public String getProductId() {
        return productId;
    }

    public String getProductImageId() {
        return productImageId;
    }

    public String getProductName() {
        return productName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public OrderDetailSyncResponse() {}

    protected OrderDetailSyncResponse(Parcel in) {
        id = in.readString();
        orderId = in.readString();
        price = in.readDouble();
        productCategoryId = in.readString();
        productCategoryName = in.readString();
        productId = in.readString();
        productImageId = in.readString();
        productName = in.readString();
        quantity = in.readInt();
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
        dest.writeString(orderId);
        dest.writeDouble(price);
        dest.writeString(productCategoryId);
        dest.writeString(productCategoryName);
        dest.writeString(productId);
        dest.writeString(productImageId);
        dest.writeString(productName);
        dest.writeInt(quantity);
        dest.writeInt(isDeleted ? 1 : 0);
        dest.writeString(lastModified);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<OrderDetailSyncResponse> CREATOR = new Parcelable.Creator<OrderDetailSyncResponse>() {
        @Override
        public OrderDetailSyncResponse createFromParcel(Parcel in) {
            return new OrderDetailSyncResponse(in);
        }

        @Override
        public OrderDetailSyncResponse[] newArray(int size) {
            return new OrderDetailSyncResponse[size];
        }
    };
}