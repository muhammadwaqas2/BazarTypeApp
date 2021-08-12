package com.app.bizlinked.models.sync_response;

import android.os.Parcel;
import android.os.Parcelable;

public class OrderStatusSyncResponse extends BaseSyncResponse implements Parcelable {

    private String id;
    private String approvalDate;
    private String delivered;
    private Boolean isApproved;
    private String orderId;
    private String received;
    private String submitted;
    private String orderLedgerTransactionId;
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

    public String getApprovalDate() {
        return approvalDate;
    }

    public String getDelivered() {
        return delivered;
    }

    public Boolean getApproved() {
        return isApproved;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getReceived() {
        return received;
    }

    public String getSubmitted() {
        return submitted;
    }

    public String getOrderLedgerTransactionId() {
        return orderLedgerTransactionId;
    }

    public OrderStatusSyncResponse() {}

    protected OrderStatusSyncResponse(Parcel in) {
        id = in.readString();
        approvalDate = in.readString();
        delivered = in.readString();
        orderId = in.readString();
        received = in.readString();
        submitted = in.readString();
        orderLedgerTransactionId = in.readString();
        isApproved = in.readInt() == 1;
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
        dest.writeString(approvalDate);
        dest.writeString(delivered);
        dest.writeString(orderId);
        dest.writeString(received);
        dest.writeString(submitted);
        dest.writeString(orderLedgerTransactionId);
        dest.writeInt(isApproved ? 1 : 0);
        dest.writeInt(isDeleted ? 1 : 0);
        dest.writeString(lastModified);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<OrderStatusSyncResponse> CREATOR = new Parcelable.Creator<OrderStatusSyncResponse>() {
        @Override
        public OrderStatusSyncResponse createFromParcel(Parcel in) {
            return new OrderStatusSyncResponse(in);
        }

        @Override
        public OrderStatusSyncResponse[] newArray(int size) {
            return new OrderStatusSyncResponse[size];
        }
    };
}
