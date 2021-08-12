package com.app.bizlinked.models.db;

import com.app.bizlinked.db.AppDBHelper;
import com.app.bizlinked.helpers.common.Utils;
import com.app.bizlinked.helpers.realm.FindDBHelper;
import com.app.bizlinked.helpers.ui.dialogs.DateFormatHelper;
import com.app.bizlinked.listener.custom.Convertable;
import com.app.bizlinked.models.db.base_class.BaseEntity;
import com.app.bizlinked.models.db.base_class.IBaseEntity;
import com.app.bizlinked.models.sync_response.ProfileSyncResponse;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Date;
import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Profile extends RealmObject implements IBaseEntity, Convertable<ProfileSyncResponse> {


    @PrimaryKey
    private String id = UUID.randomUUID().toString();

    private String name;
    private BusinessCategory businessCategory;
    private String mobileNumber;
    private String suitNumber;
    private String marketName;
    private String streetAddress;
    private City city;
    private Double latitude;
    private Double longitude;
    private Image coverImage;
    private Image logo;
    private BaseEntity baseEntity;


    public Profile() {
        this.baseEntity = new BaseEntity();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BusinessCategory getBusinessCategory() {
        return businessCategory;
    }

    public void setBusinessCategory(BusinessCategory businessCategory) {
        this.businessCategory = businessCategory;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getSuitNumber() {
        return suitNumber;
    }

    public void setSuitNumber(String suitNumber) {
        this.suitNumber = suitNumber;
    }

    public String getMarketName() {
        return marketName;
    }

    public void setMarketName(String marketName) {
        this.marketName = marketName;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Image getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(Image coverImage) {
        this.coverImage = coverImage;
    }

    public Image getLogo() {
        return logo;
    }

    public void setLogo(Image logo) {
        this.logo = logo;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public Date getLastModifiedDate() {
        return baseEntity.getLastModifiedDate();
    }

    @Override
    public void setLastModifiedDate(Date lastModifiedDate) {
        baseEntity.setLastModifiedDate(lastModifiedDate);
    }

    @Override
    public Boolean getDirty() {
        return baseEntity.getDirty();
    }

    @Override
    public void setDirty(Boolean dirty) {
        baseEntity.setDirty(dirty);
    }

    @Override
    public Boolean getDeleted() {
        return baseEntity.getDeleted();
    }

    @Override
    public void setDeleted(Boolean deleted) {
        baseEntity.setDeleted(deleted);
    }

    @Override
    public JsonElement encode() {

        JsonObject jsonObject = new JsonObject();

        if (getBusinessCategory() != null)
            jsonObject.addProperty("businessCategoryId", getBusinessCategory().getId());

        if (getCity() != null)
            jsonObject.addProperty("cityId", getCity().getId());

        if (getCoverImage() != null)
            jsonObject.addProperty("coverImageId", getCoverImage().getId());

        if (getLatitude() != null)
            jsonObject.addProperty("latitude", getLatitude());

        if (getLongitude() != null)
            jsonObject.addProperty("longitude", getLongitude());

        if (!Utils.isEmptyOrNull(getId()))
            jsonObject.addProperty("id", getId());

        if (!Utils.isEmptyOrNull(getMarketName()))
            jsonObject.addProperty("marketName", getMarketName());

        if (!Utils.isEmptyOrNull(getMobileNumber()))
            jsonObject.addProperty("mobileNumber", getMobileNumber());

        if (!Utils.isEmptyOrNull(getName()))
            jsonObject.addProperty("name", getName());

        if (!Utils.isEmptyOrNull(getStreetAddress()))
            jsonObject.addProperty("streetAddress", getStreetAddress());

        if (!Utils.isEmptyOrNull(getSuitNumber()))
            jsonObject.addProperty("suitNumber", getSuitNumber());

        if (getLogo() != null)
            jsonObject.add("logoImage", getLogo().encode());

        if (getLogo() != null)
            jsonObject.addProperty("logoImageId", getLogo().getId());


        jsonObject.addProperty("isDeleted", getDeleted());
        jsonObject.addProperty("lastModified", DateFormatHelper.convertDateToServerFormattedString(getLastModifiedDate()));

        return jsonObject;
    }

    @Override
    public void decode(ProfileSyncResponse decodeAbleClass) {

        //Business Category
        BusinessCategory businessCategory = FindDBHelper.byPrimaryKey(AppDBHelper.getRealmInstance(), BusinessCategory.class, decodeAbleClass.getBusinessCategoryId());
        if (businessCategory != null) {
            setBusinessCategory(businessCategory);
        }


        //Logo
        Image logoImage = null;
        if (decodeAbleClass.getLogoImage() != null) {

            logoImage = FindDBHelper.byPrimaryKey(AppDBHelper.getRealmInstance(), Image.class, decodeAbleClass.getLogoImage().getId());
            if (logoImage != null) {
                setLogo(logoImage);
            } else {
                Image logoImg = null;
                if(AppDBHelper.getRealmInstance().isInTransaction()){
                    logoImg = AppDBHelper.getRealmInstance().createObject(Image.class, decodeAbleClass.getLogoImage().getId());
                }else{
                    logoImg = new Image();
                    logoImg.setId(decodeAbleClass.getLogoImage().getId());
                }
                logoImg.decode(decodeAbleClass.getLogoImage());

                //assignments
                setLogo(logoImg);
            }
        }

        if (decodeAbleClass.getLogoImage() != null && getLogo() == null) {
            Image logoImg = AppDBHelper.getRealmInstance().createObject(Image.class, decodeAbleClass.getLogoImage().getId());
            logoImg.decode(decodeAbleClass.getLogoImage());
            setLogo(logoImg);
        }

        //City
        City city = FindDBHelper.byPrimaryKey(AppDBHelper.getRealmInstance(), City.class, decodeAbleClass.getCityId());
        if (city != null) {
            setCity(city);
        }

        //Cover Image
        if (decodeAbleClass.getCoverImage() != null) {
            Image coverImage = FindDBHelper.byPrimaryKey(AppDBHelper.getRealmInstance(), Image.class, decodeAbleClass.getCoverImage().getId());
            if (coverImage != null) {
                setCoverImage(coverImage);
            } else {

                Image coverImg;
                if(AppDBHelper.getRealmInstance().isInTransaction()){
                    coverImg = AppDBHelper.getRealmInstance().createObject(Image.class, decodeAbleClass.getLogoImage().getId());
                }else{
                    coverImg = new Image();
                    coverImg.setId(decodeAbleClass.getLogoImage().getId());
                }
                coverImg.decode(decodeAbleClass.getLogoImage());
                setCoverImage(coverImg);
            }
        }

        // Name
        if (!Utils.isEmptyOrNull(decodeAbleClass.getName()))
            setName(decodeAbleClass.getName());

        // Mobile Number
        if (!Utils.isEmptyOrNull(decodeAbleClass.getMobileNumber()))
            setMobileNumber(decodeAbleClass.getMobileNumber());

        // Suit Number
        if (!Utils.isEmptyOrNull(decodeAbleClass.getSuitNumber()))
            setSuitNumber(decodeAbleClass.getSuitNumber());

        // Market Nam
        if (!Utils.isEmptyOrNull(decodeAbleClass.getMarketName()))
            setMarketName(decodeAbleClass.getMarketName());

        // Street Address
        if (!Utils.isEmptyOrNull(decodeAbleClass.getStreetAddress()))
            setStreetAddress(decodeAbleClass.getStreetAddress());

        // Latitude
        if (decodeAbleClass.getLatitude() != null && decodeAbleClass.getLatitude() != 0.0)
            setLatitude(decodeAbleClass.getLatitude());

        // Longitude
        if (decodeAbleClass.getLongitude() != null && decodeAbleClass.getLongitude() != 0.0)
            setLongitude(decodeAbleClass.getLongitude());

        // isDirty
        setDirty(false);

        // isDeleted
        if (decodeAbleClass.getDeleted() != null)
            setDeleted(decodeAbleClass.getDeleted());

        if (!Utils.isEmptyOrNull(decodeAbleClass.getLastModified()))
            setLastModifiedDate(DateFormatHelper.convertStringToServerFormattedDate(decodeAbleClass.getLastModified()));
    }

}
