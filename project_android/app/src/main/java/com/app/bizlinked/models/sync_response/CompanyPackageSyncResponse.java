package com.app.bizlinked.models.sync_response;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CompanyPackageSyncResponse extends BaseSyncResponse implements Parcelable {

    private String id;
    private String companyId;
    private String packageId;
    private Boolean isActive;
    private Boolean isDirty;
    private Boolean isDeleted;
    private String lastModified;
    
    @SerializedName("package")
    @Expose
    private PackageSyncResponse packg;

    @Override
    public String getId() {
        return id;
    }

    public String getCompanyId() {
        return companyId;
    }

    public String getPackageId() {
        return packageId;
    }

    public Boolean getActive() {
        return isActive;
    }

    public Boolean getDirty() {
        return isDirty;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    @Override
    public String getLastModified() {
        return lastModified;
    }

    public PackageSyncResponse getPackage() {
        return packg;
    }

    protected CompanyPackageSyncResponse(Parcel in) {
        id = in.readString();
        companyId = in.readString();
        packageId = in.readString();
        isActive = in.readInt() == 1;
        isDirty = in.readInt() == 1;
        isDeleted = in.readInt() == 1;
        lastModified = in.readString();
        packg = in.readParcelable(PackageSyncResponse.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(companyId);
        dest.writeString(packageId);
        dest.writeInt(isActive ? 1 : 0);
        dest.writeInt(isDirty ? 1 : 0);
        dest.writeInt(isDeleted ? 1 : 0);
        dest.writeString(lastModified);
        dest.writeParcelable(packg, flags);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<CompanyPackageSyncResponse> CREATOR = new Parcelable.Creator<CompanyPackageSyncResponse>() {
        @Override
        public CompanyPackageSyncResponse createFromParcel(Parcel in) {
            return new CompanyPackageSyncResponse(in);
        }

        @Override
        public CompanyPackageSyncResponse[] newArray(int size) {
            return new CompanyPackageSyncResponse[size];
        }
    };
}