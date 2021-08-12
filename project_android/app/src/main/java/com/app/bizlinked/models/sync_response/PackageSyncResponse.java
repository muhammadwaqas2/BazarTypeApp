package com.app.bizlinked.models.sync_response;

import android.os.Parcel;
import android.os.Parcelable;

public class PackageSyncResponse extends BaseSyncResponse implements Parcelable{

    private String id;
    private String companyId;
    private String name;
    private String description;
    private Integer orderQuantity;
    private Integer expiryDays;
    private Double price;
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

    public String getDescription() {
        return description;
    }

    public Integer getOrderQuantity() {
        return orderQuantity;
    }

    public Integer getExpiryDays() {
        return expiryDays;
    }

    public Double getPrice() {
        return price;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public String getLastModified() {
        return lastModified;
    }



    public PackageSyncResponse() {}

    protected PackageSyncResponse(Parcel in) {
        id = in.readString();
        companyId = in.readString();
        name = in.readString();
        description = in.readString();
        orderQuantity = in.readInt();
        expiryDays = in.readInt();
        price = in.readDouble();
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
        dest.writeString(description);
        dest.writeInt(orderQuantity);
        dest.writeInt(expiryDays);
        dest.writeDouble(price);
        dest.writeInt(isDeleted ? 1 : 0);
        dest.writeString(lastModified);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<PackageSyncResponse> CREATOR = new Parcelable.Creator<PackageSyncResponse>() {
        @Override
        public PackageSyncResponse createFromParcel(Parcel in) {
            return new PackageSyncResponse(in);
        }

        @Override
        public PackageSyncResponse[] newArray(int size) {
            return new PackageSyncResponse[size];
        }
    };
}

