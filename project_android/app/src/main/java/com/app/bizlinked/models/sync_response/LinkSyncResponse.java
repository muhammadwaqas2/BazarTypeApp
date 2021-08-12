package com.app.bizlinked.models.sync_response;

import android.os.Parcel;
import android.os.Parcelable;

public class LinkSyncResponse extends BaseSyncResponse implements Parcelable {

    private String id;
    private String companyId;
    private Boolean isDeleted;
    private String lastModified;
    private String customerCompanyId;
    private String initiatorCompanyId;
    private ProfileSyncResponse linkedCompany;
    private String linkedCompanyBySubjectCompanyId;
    private String linkedCompanyId;
    private String linkedCompanyRelation;
    private String status;
    private String supplierCompanyId;

    public String getId() {
        return id;
    }

    public String getCompanyId() {
        return companyId;
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

    public String getInitiatorCompanyId() {
        return initiatorCompanyId;
    }

    public ProfileSyncResponse getLinkedCompany() {
        return linkedCompany;
    }

    public String getLinkedCompanyBySubjectCompanyId() {
        return linkedCompanyBySubjectCompanyId;
    }

    public String getLinkedCompanyId() {
        return linkedCompanyId;
    }

    public String getLinkedCompanyRelation() {
        return linkedCompanyRelation;
    }

    public String getStatus() {
        return status;
    }

    public String getSupplierCompanyId() {
        return supplierCompanyId;
    }

    public LinkSyncResponse() {}

    protected LinkSyncResponse(Parcel in) {
        id = in.readString();
        companyId = in.readString();
        customerCompanyId = in.readString();
        initiatorCompanyId = in.readString();
        linkedCompany = in.readParcelable(ProfileSyncResponse.class.getClassLoader());
        linkedCompanyBySubjectCompanyId = in.readString();
        linkedCompanyId = in.readString();
        linkedCompanyRelation = in.readString();
        status = in.readString();
        supplierCompanyId = in.readString();
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
        dest.writeString(customerCompanyId);
        dest.writeString(initiatorCompanyId);
        dest.writeParcelable(linkedCompany, flags);
        dest.writeString(linkedCompanyBySubjectCompanyId);
        dest.writeString(linkedCompanyId);
        dest.writeString(linkedCompanyRelation);
        dest.writeString(status);
        dest.writeString(supplierCompanyId);
        dest.writeInt(isDeleted ? 1 : 0);
        dest.writeString(lastModified);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<LinkSyncResponse> CREATOR = new Parcelable.Creator<LinkSyncResponse>() {
        @Override
        public LinkSyncResponse createFromParcel(Parcel in) {
            return new LinkSyncResponse(in);
        }

        @Override
        public LinkSyncResponse[] newArray(int size) {
            return new LinkSyncResponse[size];
        }
    };
}

