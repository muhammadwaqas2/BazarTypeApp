package com.app.bizlinked.models.sync_response;

import android.os.Parcel;
import android.os.Parcelable;

public class ProfileSyncResponse extends BaseSyncResponse implements Parcelable {

    private String businessCategoryId;
    private String cityId;
    private String id;
    private Boolean isDeleted;
    private String lastModified;
    private String lastSynchronized;
    private Double latitude;
    private String coverImageId;
    private ImageSyncResponse coverImage;
    private String logoImageId;
    private ImageSyncResponse logoImage;
    private Double longitude;
    private String marketName;
    private String mobileNumber;
    private String name;
    private String streetAddress;
    private String suitNumber;


    public String getBusinessCategoryId() {
        return businessCategoryId;
    }

    public String getCityId() {
        return cityId;
    }

    public ImageSyncResponse getCoverImage() {
        return coverImage;
    }

    public String getCoverImageId() {
        return coverImageId;
    }

    public String getId() {
        return id;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public String getLastModified() {
        return lastModified;
    }

    public String getLastSynchronized() {
        return lastSynchronized;
    }

    public Double getLatitude() {
        return latitude;
    }

    public ImageSyncResponse getLogoImage() {
        return logoImage;
    }

    public String getLogoImageId() {
        return logoImageId;
    }

    public Double getLongitude() {
        return longitude;
    }

    public String getMarketName() {
        return marketName;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public String getName() {
        return name;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public String getSuitNumber() {
        return suitNumber;
    }

    public ProfileSyncResponse() { }

    protected ProfileSyncResponse(Parcel in) {
        businessCategoryId = in.readString();
        cityId = in.readString();
        coverImage = (ImageSyncResponse) in.readValue(ImageSyncResponse.class.getClassLoader());
        coverImageId = in.readString();
        id = in.readString();
        isDeleted = in.readInt() == 1;
        lastModified = in.readString();
        lastSynchronized = in.readString();
        latitude = in.readDouble();
        logoImage = (ImageSyncResponse) in.readValue(ImageSyncResponse.class.getClassLoader());
        logoImageId = in.readString();
        longitude = in.readDouble();
        marketName = in.readString();
        mobileNumber = in.readString();
        name = in.readString();
        streetAddress = in.readString();
        suitNumber = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(businessCategoryId);
        dest.writeString(cityId);
        dest.writeValue(coverImage);
        dest.writeString(coverImageId);
        dest.writeString(id);
        dest.writeInt(isDeleted ? 1 : 0);
        dest.writeString(lastModified);
        dest.writeString(lastSynchronized);
        dest.writeDouble(latitude);
        dest.writeValue(logoImage);
        dest.writeString(logoImageId);
        dest.writeDouble(longitude);
        dest.writeString(marketName);
        dest.writeString(mobileNumber);
        dest.writeString(name);
        dest.writeString(streetAddress);
        dest.writeString(suitNumber);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<ProfileSyncResponse> CREATOR = new Parcelable.Creator<ProfileSyncResponse>() {
        @Override
        public ProfileSyncResponse createFromParcel(Parcel in) {
            return new ProfileSyncResponse(in);
        }

        @Override
        public ProfileSyncResponse[] newArray(int size) {
            return new ProfileSyncResponse[size];
        }
    };
}
