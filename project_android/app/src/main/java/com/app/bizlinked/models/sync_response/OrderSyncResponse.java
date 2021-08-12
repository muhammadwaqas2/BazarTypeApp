package com.app.bizlinked.models.sync_response;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class OrderSyncResponse extends BaseSyncResponse implements Parcelable {


    private String id;
    private String customerCompanyId;
    private String initiatingCompanyId;
    private String initiationDate;
    private String supplierCompanyId;
    private ArrayList<OrderDetailSyncResponse> orderDetail;
    private OrderDetailSyncResponse orderStatus;
    private Boolean rejected;
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

    public String getCustomerCompanyId() {
        return customerCompanyId;
    }

    public String getInitiatingCompanyId() {
        return initiatingCompanyId;
    }

    public String getInitiationDate() {
        return initiationDate;
    }

    public String getSupplierCompanyId() {
        return supplierCompanyId;
    }

    public ArrayList<OrderDetailSyncResponse> getOrderDetail() {
        return orderDetail;
    }

    public OrderDetailSyncResponse getOrderStatus() {
        return orderStatus;
    }

    public Boolean getRejected() {
        return rejected;
    }

    public OrderSyncResponse() {}

    protected OrderSyncResponse(Parcel in) {
        id = in.readString();
        customerCompanyId = in.readString();
        initiatingCompanyId = in.readString();
        initiationDate = in.readString();
        supplierCompanyId = in.readString();
        in.readList(orderDetail, OrderDetailSyncResponse.class.getClassLoader());
        orderStatus = in.readParcelable(OrderDetailSyncResponse.class.getClassLoader());
        rejected = in.readInt() == 1;
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
        dest.writeString(customerCompanyId);
        dest.writeString(initiatingCompanyId);
        dest.writeString(initiationDate);
        dest.writeString(supplierCompanyId);
        dest.writeInt(rejected ? 1 : 0);
        dest.writeList(orderDetail);
        dest.writeParcelable(orderStatus, flags);
        dest.writeInt(isDeleted ? 1 : 0);
        dest.writeString(lastModified);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<OrderSyncResponse> CREATOR = new Parcelable.Creator<OrderSyncResponse>() {
        @Override
        public OrderSyncResponse createFromParcel(Parcel in) {
            return new OrderSyncResponse(in);
        }

        @Override
        public OrderSyncResponse[] newArray(int size) {
            return new OrderSyncResponse[size];
        }
    };
}

