package com.app.bizlinked.models.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import com.app.bizlinked.BizLinkedApplicationClass;
import com.app.bizlinked.constant.AppConstant;
import com.app.bizlinked.db.AppDBHelper;
import com.app.bizlinked.helpers.common.Utils;
import com.app.bizlinked.helpers.file.FileHelper;
import com.app.bizlinked.helpers.realm.FindDBHelper;
import com.app.bizlinked.helpers.realm.RealmLiveData;
import com.app.bizlinked.helpers.sync_helpers.ImageSyncUtility;
import com.app.bizlinked.helpers.sync_helpers.SyncManager;
import com.app.bizlinked.listener.custom.DatabaseTransactionInterface;
import com.app.bizlinked.listener.custom.DataSaveAndConvertInterface;
import com.app.bizlinked.models.db.BusinessCategory;
import com.app.bizlinked.models.db.City;
import com.app.bizlinked.models.db.Image;
import com.app.bizlinked.models.db.Link;
import com.app.bizlinked.models.db.Profile;
import com.app.bizlinked.models.enums.EntityEnum;
import com.app.bizlinked.models.enums.LinkRelationEnum;
import com.app.bizlinked.models.enums.LinkStatusEnum;
import com.app.bizlinked.models.sync_response.ProfileSyncResponse;
import com.app.bizlinked.webhelpers.WebApiRequest;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmResults;
import okhttp3.ResponseBody;

public class ProfileViewModal extends BaseViewModal {


    private RealmResults<Profile> resultsProfile;
    private RealmResults<City> resultsCities;
    private RealmResults<BusinessCategory> resultsBusinessCategories;


    private Profile profile;
    private String id;
    private String name;
    private MutableLiveData<BusinessCategory> businessCategory; //Live Data
    private String mobileNumber;
    private String suitNumber;
    private String marketName;
    private String streetAddress;
    private MutableLiveData<City> city; //Live Data
    private Double latitude;
    private Double longitude;
    private File imageFile = null;
    private Image coverImage  = null;
    private Image logo = null;
    private City selectedCity  = null;
    private Boolean isImageDeleted = false;
    private BusinessCategory selectedCategory  = null;
    private List<City> cities;
    private List<BusinessCategory> businessCategories;
    private MutableLiveData<HashMap<String, byte[]>> image;

    public Profile getProfile() {
        return profile;
    }

    public MutableLiveData<HashMap<String, byte[]>> getImage() {
        if (image == null) {
            image = new MutableLiveData<>();
        }
        return image;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }


    public void init(Profile profile) {


        Profile profileDBObject = FindDBHelper.byPrimaryKey(AppDBHelper.getRealmInstance(), Profile.class, profile.getId());

        if(profileDBObject != null)
            this.profile = profileDBObject;
        else
            this.profile = profile;

        this.populateViewModel();

        //This is for Link -> Profile Download Image
        if (this.profile.getLogo() != null &&
                this.profile.getLogo().getData() == null && (this.profile.getLogo().getImageAvailable() != null &&
                this.profile.getLogo().getImageAvailable())){
            this.downloadImage(this.profile.getLogo().getId());
        }
    }


    public void downloadImage(String imageID){

        String imageDownloadURL = AppConstant.ServerAPICalls.imageDownloadURL + imageID;

        ImageSyncUtility.getInstance().downloadImageRequest(imageDownloadURL, null, new WebApiRequest.ImageRequestDataCallBack() {
            @Override
            public void onSuccess(ResponseBody response) {

                try {
                    if (response != null && response.contentLength() > 0) {

                        byte[] imageData = response.bytes();

                        HashMap<String, byte[]> hashMap = new HashMap<>();
                        hashMap.put(profile.getId(), imageData);
                        getImage().setValue(hashMap);

                        AppDBHelper.getRealmInstance().executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                profile.getLogo().setData(imageData);
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(String response) {

            }

            @Override
            public void onNoNetwork() {

            }
        });
    }

    public void init(String companyId) {

        Profile profileDBObject = FindDBHelper.byPrimaryKey(AppDBHelper.getRealmInstance(), Profile.class, companyId);

        if (profileDBObject != null) {
            this.profile = profileDBObject;
            this.populateViewModel();
        }
    }

    public void init(ProfileViewModal profileViewModel) {

        if (profileViewModel != null) {
            this.profile = profileViewModel.getProfile();
            this.populateViewModel();
        }

    }


    private void populateViewModel() {

        //id
        if (!Utils.isEmptyOrNull(profile.getId())) {
            setId(profile.getId());
        }

        //Name
        if (!Utils.isEmptyOrNull(profile.getName())) {
            setName(profile.getName());
        }

        //Mobile Number
        if (!Utils.isEmptyOrNull(profile.getMobileNumber())) {
            setMobileNumber(profile.getMobileNumber());
        }

        //Suit Number
        if (!Utils.isEmptyOrNull(profile.getSuitNumber())) {
            setSuitNumber(profile.getSuitNumber());
        }

        //Market Name
        if (!Utils.isEmptyOrNull(profile.getMarketName())) {
            setMarketName(profile.getMarketName());
        }

        //Street Address
        if (!Utils.isEmptyOrNull(profile.getStreetAddress())) {
            setStreetAddress(profile.getStreetAddress());
        }

        //Latitude
        if (profile.getLatitude() != null) {
            setLatitude(profile.getLatitude());
        }

        //Longitude
        if (profile.getLongitude() != null) {
            setLongitude(profile.getLongitude());
        }

        //Selected Business Category
        if (profile.getBusinessCategory() != null) {
            setSelectedCategory(profile.getBusinessCategory());
        }

        //Selected City
        if (profile.getCity() != null) {
            setSelectedCity(profile.getCity());
        }

        //Cover Image
        if (profile.getCoverImage() != null) {
            setCoverImage(profile.getCoverImage());
        }

        //Logo Image
        if (profile.getLogo() != null && profile.getLogo().getData() != null && profile.getLogo().getData().length > 0) {
            setLogo(profile.getLogo());
        }

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }


    public MutableLiveData<BusinessCategory> getBusinessCategory() {
        if (businessCategory == null) {
            businessCategory = new MutableLiveData<>();
        }
        return businessCategory;
    }

    public City getSelectedCity() {
        return selectedCity;
    }

    public void setSelectedCity(City selectedCity) {
        this.selectedCity = selectedCity;
    }

    public BusinessCategory getSelectedCategory() {
        return selectedCategory;
    }

    public void setSelectedCategory(BusinessCategory selectedCategory) {
        this.selectedCategory = selectedCategory;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public String getSuitNumber() {
        return suitNumber;
    }

    public String getMarketName() {
        return marketName;
    }

    public String getStreetAddress() {
        return streetAddress;
    }


    public MutableLiveData<City> getCity() {
        if (city == null) {
            city = new MutableLiveData<>();
        }
        return city;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Image getCoverImage() {
        return coverImage;
    }

    public void setName(String name) {
        this.name = name;
    }

//    public void setBusinessCategory(BusinessCategory businessCategory) {
//        this.businessCategory = businessCategory;
//    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public void setSuitNumber(String suitNumber) {
        this.suitNumber = suitNumber;
    }

    public void setMarketName(String marketName) {
        this.marketName = marketName;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

//    public void setCity(City city) {
//        this.city = city;
//    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public void setCoverImage(Image coverImage) {
        this.coverImage = coverImage;
    }

    public void setLogo(Image logo) {
        this.logo = logo;
    }

    public Image getLogo() {
        return logo;
    }

    public Boolean getImageDeleted() {
        return isImageDeleted;
    }

    public void setImageDeleted(Boolean imageDeleted) {
        isImageDeleted = imageDeleted;
    }

    public File getImageFile() {
        return imageFile;
    }

    public void setImageFile(File imageFile) {
        this.imageFile = imageFile;
    }


    public List<City> getCities() {
        return cities;
    }

    public void setCities(List<City> cities) {
        this.cities = cities;
    }

    public List<BusinessCategory> getBusinessCategories() {
        return businessCategories;
    }

    public void setBusinessCategories(List<BusinessCategory> businessCategories) {
        this.businessCategories = businessCategories;
    }

    public RealmLiveData<City> getAllCitiesFromDB() {

        resultsCities = AppDBHelper.getRealmInstance().where(City.class).findAllAsync();
        return new RealmLiveData<City>(resultsCities);
        // Async runs the fetch off the main thread, and returns
        // results as LiveData back on the main.

    }

    public RealmLiveData<BusinessCategory> getAllBusinessCategoriesFromDB() {
        resultsBusinessCategories = AppDBHelper.getRealmInstance().where(BusinessCategory.class).findAllAsync();
        return new RealmLiveData<BusinessCategory>(resultsBusinessCategories);
        // Async runs the fetch off the main thread, and returns
        // results as LiveData back on the main.

    }


    public void addServerCreatedProfileToDB(JsonObject jsonObject, DataSaveAndConvertInterface<ProfileSyncResponse, Profile> callBack) {


        Gson gson = new Gson();
        Log.d(ProfileViewModal.class.getName(), jsonObject.toString());
        ProfileSyncResponse profileSyncResponse = gson.fromJson(jsonObject, new TypeToken<ProfileSyncResponse>() {}.getType());


        try {

            if(this.profile == null)
                this.profile = new Profile();

            this.profile.decode(profileSyncResponse);
            this.profile.setId(profileSyncResponse.getId());
            this.profile.setLogo(getLogo());
            this.profile.setCoverImage(getCoverImage());


            AppDBHelper.getRealmInstance().executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.insertOrUpdate(profile);
                }
            });

            callBack.onSuccess(profileSyncResponse, FindDBHelper.byPrimaryKey(AppDBHelper.getRealmInstance(), Profile.class, profileSyncResponse.getId()));

        }catch (Exception e){
            e.printStackTrace();
            callBack.onError();
        }


//        //Insert in DB
//        AppDBHelper.getDBHelper().insertOrUpdateRecordToDB(profile, new DatabaseTransactionInterface() {
//            @Override
//            public void onSuccessTransaction() {
//
//            }
//
//            @Override
//            public void onErrorTransaction() {
//                callBack.onError();
//
//            }
//        });

        //For Image Sync
        SyncManager.getInstance().addEntityToQueue(EntityEnum.ImageSyncUtility);
    }

    public void updateProfile(DatabaseTransactionInterface callBack) {

        Profile profile = FindDBHelper.byPrimaryKey(AppDBHelper.getRealmInstance(), Profile.class, getId());
        AppDBHelper.getRealmInstance().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                profile.setName(getName());
                profile.setSuitNumber(getSuitNumber());
                profile.setMarketName(getMarketName());
                profile.setStreetAddress(getStreetAddress());
                profile.setLatitude(getLatitude());
                profile.setLongitude(getLongitude());

                if (getCity().getValue() != null && !Utils.isEmptyOrNull(getCity().getValue().getId())) {
                    profile.setCity(getCity().getValue());
                } else if (getSelectedCity() != null) {
                    profile.setCity(getSelectedCity());
                }

                if (getBusinessCategory().getValue() != null  && !Utils.isEmptyOrNull(getBusinessCategory().getValue().getId())) {
                    profile.setBusinessCategory(getBusinessCategory().getValue());
                } else if (getSelectedCategory() != null) {
                    profile.setBusinessCategory(getSelectedCategory());
                }

                if (imageFile != null) {

                    byte[] imageBytesFile = FileHelper.readBytesFromFile(imageFile);

                    //If Profile Already has image
                    if (profile.getLogo() != null) {

                        if (imageBytesFile != null && imageBytesFile.length > 0) {

                            profile.getLogo().setDeleted(true);
                            profile.getLogo().setDirty(true);
                            profile.getLogo().setLastModifiedDate(new Date());

                            Image image = realm.createObject(Image.class, UUID.randomUUID().toString());
                            image.setData(imageBytesFile);
                            profile.setLogo(image);

//
//                            profile.getLogo().setData(imageBytesFile);
//                            profile.getLogo().setLastModifiedDate(new Date());
//                            profile.getLogo().setUploaded(false);
                        }

                    } else {

                        //If Profile doesn't have image
                        if (imageBytesFile != null && imageBytesFile.length > 0) {

                            Image imgObject = realm.createObject(Image.class, UUID.randomUUID().toString());
                            imgObject.setData(imageBytesFile);
                            profile.setLogo(imgObject);
                        }

                    }

                } else if (isImageDeleted) {

                    //If we remove image from Profile
                    // remove image work
                    if (profile.getLogo() != null) {
                        profile.getLogo().setDeleted(true);
                        profile.getLogo().setDirty(true);
                        profile.getLogo().setLastModifiedDate(new Date());
                        profile.setLogo(null);
                    }
                }

//                //For Detail Page View Model Value Update
//                logo = profile.getLogo();

                profile.setDirty(true);
                profile.setLastModifiedDate(new Date());


                //AppDBHelper.getRealmInstance().insertOrUpdate(profile);

                callBack.onSuccessTransaction();
            }

        });

        //Sync
//        SyncManager.getInstance().addEntityToQueue(EntityEnum.Image);
        SyncManager.getInstance().addEntityToQueue(EntityEnum.Profile);
        SyncManager.getInstance().addEntityToQueue(EntityEnum.ImageSyncUtility);

    }


    public void addAsLink(String relation, DataSaveAndConvertInterface<Object, Link> dataSaveAndConvertInterface){

        Link link = new Link();

        Profile user = FindDBHelper.byPrimaryKey(AppDBHelper.getRealmInstance(), Profile.class, BizLinkedApplicationClass.getPreference().getProfileId());
        if(user != null) {
            link.setInitiatorCompany(user);
        }

        link.setLinkedCompany(this.profile);
        link.setLinkedCompanyRelation(relation);
        link.setStatus(LinkStatusEnum.SENT.getValue());

        if(relation.equalsIgnoreCase(LinkRelationEnum.CUSTOMER.getValue())) {
            link.setCustomerCompanyId(this.profile.getId());
            link.setSupplierCompanyId(BizLinkedApplicationClass.getPreference().getProfileId());
        }else if(relation.equalsIgnoreCase(LinkRelationEnum.SUPPLIER.getValue())) {
            link.setSupplierCompanyId(this.profile.getId());
            link.setCustomerCompanyId(BizLinkedApplicationClass.getPreference().getProfileId());
        }


        AppDBHelper.getRealmInstance().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                profile.setDirty(false);
                realm.insertOrUpdate(link);
                dataSaveAndConvertInterface.onSuccess(null, link);
            }
        });

        SyncManager.getInstance().addEntityToQueue(EntityEnum.Link);
        SyncManager.getInstance().addEntityToQueue(EntityEnum.ImageSyncUtility);
    }

    public void unlink(String id){

        Link link = AppDBHelper.getRealmInstance().where(Link.class)
                .equalTo("linkedCompany.id", id)
                .and()
                .equalTo("status", LinkStatusEnum.LINKED.getValue())
                .findFirst();

        if(link != null){

            AppDBHelper.getRealmInstance().executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    link.setStatus(LinkStatusEnum.UNLINKED.getValue());
                    link.setDirty(true);
                    link.setLastModifiedDate(new Date());
                }
            });
            SyncManager.getInstance().addEntityToQueue(EntityEnum.Link);
        }
    }
}
