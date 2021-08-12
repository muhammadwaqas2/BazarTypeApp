package com.app.bizlinked.fragments;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.app.bizlinked.R;
import com.app.bizlinked.constant.AppConstant;
import com.app.bizlinked.customViews.TitleBar;
import com.app.bizlinked.db.AppDBHelper;
import com.app.bizlinked.fragments.baseClass.BaseFragment;
import com.app.bizlinked.helpers.animation.AnimationHelpers;
import com.app.bizlinked.helpers.common.Utils;
import com.app.bizlinked.helpers.realm.FindDBHelper;
import com.app.bizlinked.helpers.sync_helpers.SyncManager;
import com.app.bizlinked.listener.MediaTypePicker;
import com.app.bizlinked.listener.custom.DatabaseTransactionInterface;
import com.app.bizlinked.listener.custom.ItemPageListenerInterface;
import com.app.bizlinked.listener.custom.OnDataSelectInterface;
import com.app.bizlinked.listener.custom.DataSaveAndConvertInterface;
import com.app.bizlinked.models.GenericNameIdModal;
import com.app.bizlinked.models.db.BusinessCategory;
import com.app.bizlinked.models.db.City;
import com.app.bizlinked.models.db.Image;
import com.app.bizlinked.models.db.Profile;
import com.app.bizlinked.models.enums.EntityEnum;
import com.app.bizlinked.models.sync_response.ProfileSyncResponse;
import com.app.bizlinked.models.viewmodel.ProfileViewModal;
import com.app.bizlinked.webhelpers.WebApiRequest;
import com.daimajia.androidanimations.library.Techniques;
import com.github.rubensousa.bottomsheetbuilder.BottomSheetBuilder;
import com.github.rubensousa.bottomsheetbuilder.BottomSheetMenuDialog;
import com.github.rubensousa.bottomsheetbuilder.adapter.BottomSheetItemClickListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonElement;
import com.gun0912.tedpermission.TedPermission;
import com.mobsandgeeks.saripaar.annotation.Length;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Order;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.RealmResults;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends BaseFragment implements MediaTypePicker, OnMapReadyCallback {

    @Order(1)
    @NotEmpty(trim = true)
    @Length(min = AppConstant.VALIDATION_RULES.NAME_MIN_LENGTH, messageResId = R.string.error_name)
    @BindView(R.id.etName)
    EditText etName;

    //@Order(2)
    //@NotEmpty(trim = true, messageResId = R.string.error_business_category)
    @BindView(R.id.tvBusinessCategory)
    TextView tvBusinessCategory;
    @BindView(R.id.llBusinessCategoryContainer)
    LinearLayout llBusinessCategoryContainer;

    //@Order(3)
    //@NotEmpty(trim = true, messageResId = R.string.error_shop_no_office_no)
    @BindView(R.id.etShopNo)
    EditText etShopNo;

    //@Order(4)
    //@NotEmpty(trim = true, messageResId = R.string.error_market_name)
    @BindView(R.id.etMarketName)
    EditText etMarketName;

    //@Order(5)
    //@NotEmpty(trim = true, messageResId = R.string.error_street_address)
    @BindView(R.id.etStreetAddress)
    EditText etStreetAddress;

//    @Order(6)
//    @NotEmpty(trim = true, messageResId = R.string.error_city)
    @BindView(R.id.tvCity)
    TextView tvCity;
    @BindView(R.id.llCityContainer)
    LinearLayout llCityContainer;


    @BindView(R.id.ivProfileImage)
    CircleImageView ivProfileImage;

    @BindView(R.id.llMainView)
    LinearLayout llMainView;

    @BindView(R.id.btnSaveAddress)
    Button btnSaveAddress;

    @BindView(R.id.btnSaveMapAddress)
    Button btnSaveMapAddress;


    //Tabs
    @BindView(R.id.tvAddressTab)
    TextView tvAddressTab;
    @BindView(R.id.tvOnMapTab)
    TextView tvOnMapTab;
    @BindView(R.id.llAddressTabSection)
    LinearLayout llAddressTabSection;
    @BindView(R.id.llMapTabSection)
    LinearLayout llMapTabSection;
    //Tabs

    //Map
    SupportMapFragment mapView;

    private File imageFile = null;
    TitleBar titleBar;

    private String profileId = null;
    private String phoneNumber;

    //View Modal
    ProfileViewModal profileViewModel;

    GoogleMap mGoogleMap = null;



    public ProfileFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCustomBackPressed() {
        activityReference.onPageBack();
    }

    @Override
    protected void setTitleBar(TitleBar titleBar) {

        this.titleBar = titleBar;
        titleBar.showHeaderView();
        titleBar.setLeftTitleText(getResources().getString(R.string.profile_text));
        titleBar.showLeftIconAndListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activityReference.onBackPressed();
            }
        });

    }

    @Override
    protected int getMainLayout() {
        return R.layout.fragment_profile;
    }

    @Override
    protected void onFragmentViewReady(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState, View rootView) {


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                int duration = 500;
                for (int index = 0; index < llMainView.getChildCount(); index++) {
                    AnimationHelpers.animate(Techniques.SlideInUp, duration, llMainView.getChildAt(index));
                    duration += 100;
                }
                //AnimationHelpers.animate(Techniques.BounceInUp, 600, flLoginView);

                //Map View by default false
                llMapTabSection.setVisibility(View.GONE);
            }
        },100);

        //Setp Screen For profile
        initializeProfileViewModal();
        populateDataOnUI();
        setupMap();

        getMainActivity().locationPermission();

//        addDummyDataForDB();
    }


    private void openListSelectionScreen(List<GenericNameIdModal> data, OnDataSelectInterface onDataSelectInterface) {

        ItemSelectionListFragment itemSelectionListFragment = new ItemSelectionListFragment();
        itemSelectionListFragment.setSingleSelection(true);
        itemSelectionListFragment.setItemList(data);
        itemSelectionListFragment.setItemPageListener(new ItemPageListenerInterface() {
            @Override
            public void itemPageListenerInterfaceCallBack(List<GenericNameIdModal> items, boolean isSingleSelection) {

                setTitleBar(titleBar);

                if(items != null){
                    LinkedHashMap<String, String> selectedData = new LinkedHashMap<>();
                    for (int index = 0; index < items.size(); index++) {

                        if(items.get(index).isSelect() && isSingleSelection){
                            selectedData.put(items.get(index).getId(), items.get(index).getTitle());

                            //Select Data Callback
                            onDataSelectInterface.onDataSelectCallBack(items.get(index).getId());
                            break;
                        }
                    }
                }
            }
        });
        activityReference.realAddSupportFragment(itemSelectionListFragment, AppConstant.TRANSITION_TYPES.SLIDE);

    }

    private void setupMap() {

        mapView = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapView);
        mapView.getMapAsync(ProfileFragment.this);
    }

    @Override
    public void onMapReady(GoogleMap map) {

        this.mGoogleMap = map;

        if(Utils.checkLocationPermission(activityReference)){
            mGoogleMap.setMyLocationEnabled(true);
        }

        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);

        mGoogleMap.setBuildingsEnabled(true);
//        mGoogleMap.setTrafficEnabled(true);


        map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        map.getUiSettings().setZoomGesturesEnabled(true);
        //map.setMyLocationEnabled(true);
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                map.clear();
                map.addMarker(new MarkerOptions()
                    .anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
                    .position(new LatLng(latLng.latitude, latLng.longitude)));

                //Add Lat,Lng to  view models
                profileViewModel.setLatitude(latLng.latitude);
                profileViewModel.setLongitude(latLng.longitude);
            }
        });

        LatLng KARACHI = new LatLng(24.8607, 67.0011);
        // Move the camera instantly to hamburg with a zoom of 15.
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(KARACHI, 15));
        // Zoom in, animating the camera.
        map.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);

        setUserSelectedLocation();

    }

    private void setUserSelectedLocation() {

        if(mGoogleMap != null && profileViewModel.getLatitude() != null && profileViewModel.getLongitude() != null){

            LatLng profileMarker = new LatLng(profileViewModel.getLatitude(), profileViewModel.getLongitude());

            mGoogleMap.addMarker(new MarkerOptions()
                    .anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
                    .position(new LatLng(profileMarker.latitude, profileMarker.longitude)));

            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(profileMarker, 15));

        }
    }

    private void initializeProfileViewModal() {

        // Get the ViewModel.
        profileViewModel = ViewModelProviders.of(this).get(ProfileViewModal.class);

        //Set City Observer
        profileViewModel.getAllCitiesFromDB().observe(this, new Observer<RealmResults<City>>() {
            @Override
            public void onChanged(@Nullable RealmResults<City> cities) {
                profileViewModel.setCities(cities);
                //Update the UI
                setDataAndClickListenerForCities();
            }
        });


        //Set Business Category Observer
        profileViewModel.getAllBusinessCategoriesFromDB().observe(this, new Observer<RealmResults<BusinessCategory>>() {
            @Override
            public void onChanged(@Nullable RealmResults<BusinessCategory> businessCategories) {
                profileViewModel.setBusinessCategories(businessCategories);
                //Update the UI
                setDataAndClickListenerForBusinessCategories();
            }
        });


        //Edit Profile Case
        if(profileId != null) {
            profileViewModel.init(profileId);
        }

    }

    private void populateDataOnUI() {

        if(profileViewModel != null){

            //id Update Profile Case
            if(!Utils.isEmptyOrNull(profileViewModel.getId())){
                btnSaveAddress.setText(activityReference.getString(R.string.update_text));
                btnSaveMapAddress.setText(activityReference.getString(R.string.update_text));
            }

            //Name Set
            if(!Utils.isEmptyOrNull(profileViewModel.getName())){
                etName.setText(profileViewModel.getName());
            }

            // Business Category
            // Business Category observer which updates the UI.
            final Observer<BusinessCategory> businessCategoryChangeObserver = new Observer<BusinessCategory>() {
                @Override
                public void onChanged(@Nullable final BusinessCategory businessCategoryObj) {

                    if(businessCategoryObj != null){
                        // City Name Update the UI, in this case, a TextView.
                        tvBusinessCategory.setText(businessCategoryObj.getName());
                        tvBusinessCategory.setTag(businessCategoryObj.getId());
                    }
                }
            };
            // Business Category Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
            profileViewModel.getBusinessCategory().observe(this, businessCategoryChangeObserver);
            // Business Category

            if(profileViewModel.getSelectedCategory() != null){
                tvBusinessCategory.setText(profileViewModel.getSelectedCategory().getName());
                tvBusinessCategory.setTag(profileViewModel.getSelectedCategory().getId());
            }



            //Shop No. Set
            if(!Utils.isEmptyOrNull(profileViewModel.getSuitNumber())){
                etShopNo.setText(profileViewModel.getSuitNumber());
            }

            //Market Name Set
            if(!Utils.isEmptyOrNull(profileViewModel.getMarketName())){
                etMarketName.setText(profileViewModel.getMarketName());
            }

            //Street Address Set
            if(!Utils.isEmptyOrNull(profileViewModel.getStreetAddress())){
                etStreetAddress.setText(profileViewModel.getStreetAddress());
            }

            // City Name
            // City Name observer which updates the UI.
            final Observer<City> cityChangeObserver = new Observer<City>() {
                @Override
                public void onChanged(@Nullable final City cityObj) {

                    if(cityObj != null){
                        // City Name Update the UI, in this case, a TextView.
                        tvCity.setText(cityObj.getName());
                        tvCity.setTag(cityObj.getId());
                    }
                }
            };
            // City Name Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
            profileViewModel.getCity().observe(this, cityChangeObserver);
            // City Name

            if(profileViewModel.getSelectedCity() != null){
                tvCity.setText(profileViewModel.getSelectedCity().getName());
                tvCity.setTag(profileViewModel.getSelectedCity().getId());
            }


            setUserSelectedLocation();

            //Cover Image
            if(profileViewModel.getLogo() != null  && profileViewModel.getLogo().getData() != null && profileViewModel.getLogo().getData().length > 0){
                Bitmap imageBitmap = BitmapFactory.decodeByteArray(profileViewModel.getLogo().getData(), 0, profileViewModel.getLogo().getData().length);
                ivProfileImage.setImageBitmap(imageBitmap);
            }
        }
    }

    private void setDataAndClickListenerForCities() {

        final List<GenericNameIdModal> data = new ArrayList<>();
        List<City> cities = profileViewModel.getCities();
        for (int index = 0; index < cities.size(); index++) {
            data.add(new GenericNameIdModal(cities.get(index).getId(),cities.get(index).getName()));
        }

        View.OnClickListener vOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(data.size() > 0){
                    openListSelectionScreen(data, new OnDataSelectInterface() {
                        @Override
                        public void onDataSelectCallBack(String id) {
                            //View Modal Set
                            City selectedCity = FindDBHelper.byPrimaryKey(AppDBHelper.getRealmInstance(), City.class, id);
                            profileViewModel.getCity().setValue(selectedCity);
                        }
                    });
                }
            }
        };

        llCityContainer.setOnClickListener(null);
        llCityContainer.setOnClickListener(vOnClickListener);
    }

    private void setDataAndClickListenerForBusinessCategories() {

        final List<GenericNameIdModal> data = new ArrayList<>();
        List<BusinessCategory> businessCategories = profileViewModel.getBusinessCategories();
        for (int index = 0; index < businessCategories.size(); index++) {
            data.add(new GenericNameIdModal(businessCategories.get(index).getId(), businessCategories.get(index).getName()));
        }

        View.OnClickListener vOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(data.size() > 0){
                    openListSelectionScreen(data, new OnDataSelectInterface() {
                        @Override
                        public void onDataSelectCallBack(String id) {
                            //View Modal Set
                            BusinessCategory selectedBusinessCategory = FindDBHelper.byPrimaryKey(AppDBHelper.getRealmInstance(), BusinessCategory.class, id);
                            profileViewModel.getBusinessCategory().setValue(selectedBusinessCategory);
                        }
                    });
                }
            }
        };

        llBusinessCategoryContainer.setOnClickListener(null);
        llBusinessCategoryContainer.setOnClickListener(vOnClickListener);
    }


    private void uploadNewUserProfileToServer() {

        HashMap<String, Object> userInfoMap = new HashMap<>();


        if(!Utils.isEmptyOrNull(phoneNumber))
            userInfoMap.put("mobileNumber", phoneNumber);

        if(!Utils.isEmptyOrNull(etName.getText().toString()))
            userInfoMap.put("name", etName.getText().toString());

        if(!Utils.isEmptyOrNull(etShopNo.getText().toString()))
            userInfoMap.put("suitNumber", etShopNo.getText().toString());

        if(!Utils.isEmptyOrNull(etMarketName.getText().toString()))
            userInfoMap.put("marketName", etMarketName.getText().toString());

        if(!Utils.isEmptyOrNull(etStreetAddress.getText().toString()))
            userInfoMap.put("streetAddress", etStreetAddress.getText().toString());

        if(profileViewModel.getLatitude() != null && profileViewModel.getLatitude() != 0.0)
            userInfoMap.put("latitude", profileViewModel.getLatitude());

        if(profileViewModel.getLongitude() != null && profileViewModel.getLongitude() != 0.0)
            userInfoMap.put("longitude", profileViewModel.getLongitude());

        if(profileViewModel.getBusinessCategory() != null && profileViewModel.getBusinessCategory().getValue() != null
         && !Utils.isEmptyOrNull(profileViewModel.getBusinessCategory().getValue().getId()))
            userInfoMap.put("businessCategoryId", profileViewModel.getBusinessCategory().getValue().getId());


        if(profileViewModel.getCity() != null && profileViewModel.getCity().getValue() != null
                && !Utils.isEmptyOrNull(profileViewModel.getCity().getValue().getId()))
            userInfoMap.put("cityId", profileViewModel.getCity().getValue().getId());


        //for logo
        if(profileViewModel.getLogo() != null && !Utils.isEmptyOrNull(profileViewModel.getLogo().getId()))
            userInfoMap.put("logoImage", profileViewModel.getLogo().encode());


        Log.d(ProfileFragment.class.getName(), userInfoMap.toString());


        WebApiRequest.getInstance(activityReference, true).uploadProfileToServer(userInfoMap, new WebApiRequest.APIRequestDataCallBack() {
            @Override
            public void onSuccess(JsonElement response) {

                if(response != null && response.isJsonObject()){

                    profileViewModel.addServerCreatedProfileToDB(response.getAsJsonObject(), new DataSaveAndConvertInterface<ProfileSyncResponse, Profile>() {
                        @Override
                        public void onSuccess(ProfileSyncResponse profileSyncResponse, Profile profile) {

                            preferenceHelper.putProfileId(profileSyncResponse.getId());
                            preferenceHelper.setLoginStatus(true);


                            //Company Packages
                            SyncManager.getInstance().addEntityToQueue(EntityEnum.CompanyPackage);

                            //Go to main Screen
                            activityReference.emptyBackStack();
                            activityReference.addSupportFragment(new CustomerSupplierListFragment(), AppConstant.TRANSITION_TYPES.SLIDE);

                        }

                        @Override
                        public void onError() {
                            Utils.showSnackBar(activityReference, getView(),
                                    activityReference.getString(R.string.err_profile_db_save), ContextCompat.getColor(activityReference, R.color.red));

                        }
                    });
                }
            }

            @Override
            public void onError(String errorResponse) {

            }

            @Override
            public void onNoNetwork() {

            }
        });
    }


    @Override
    public void onValidationSuccess() {


        if(imageFile != null){

            profileViewModel.setImageFile(imageFile);

            if(profileId != null){

                setDatatoViewModel();
                profileViewModel.updateProfile(new DatabaseTransactionInterface() {
                    @Override
                    public void onSuccessTransaction() {
                        onCustomBackPressed();

                    }

                    @Override
                    public void onErrorTransaction() {

                    }
                });

            }else{
                profileViewModel.saveImageToDb(imageFile, new DataSaveAndConvertInterface<Object, Image>() {
                    @Override
                    public void onSuccess(Object syncResponse, Image logoImage) {

                        //Set to View Modal
                        profileViewModel.setLogo(logoImage);
                        //Create Profile Case
                        uploadNewUserProfileToServer();


                    }

                    @Override
                    public void onError() {
                        Utils.showSnackBar(activityReference, getView(),
                                activityReference.getString(R.string.err_image_db_save), ContextCompat.getColor(activityReference, R.color.red));

                    }
                });

            }


        }else{

            //Edit Profile Case
            if(profileId != null){
                setDatatoViewModel();
                profileViewModel.updateProfile(new DatabaseTransactionInterface() {
                    @Override
                    public void onSuccessTransaction() {
                        onCustomBackPressed();
                    }

                    @Override
                    public void onErrorTransaction() {

                    }
                });
            }else{

                //Create Profile Case
                uploadNewUserProfileToServer();
            }
        }
    }

    private void setDatatoViewModel() {

        if(!Utils.isEmptyOrNull(phoneNumber))
            profileViewModel.setMobileNumber(phoneNumber);

        if(!Utils.isEmptyOrNull(etName.getText().toString()))
            profileViewModel.setName(etName.getText().toString());

        if(!Utils.isEmptyOrNull(etShopNo.getText().toString()))
            profileViewModel.setSuitNumber(etShopNo.getText().toString());

        if(!Utils.isEmptyOrNull(etMarketName.getText().toString()))
            profileViewModel.setMarketName(etMarketName.getText().toString());

        if(!Utils.isEmptyOrNull(etStreetAddress.getText().toString()))
            profileViewModel.setStreetAddress(etStreetAddress.getText().toString());

    }

    @Override
    public void onValidationFail() {}



    @OnClick({R.id.btnSaveAddress, R.id.btnSaveMapAddress, R.id.ivCameraIcon,
            R.id.ivProfileImage,
            //R.id.flCity, R.id.tvCity, R.id.flBusinessCategory, R.id.tvBusinessCategory,
            R.id.tvAddressTab, R.id.tvOnMapTab})
    public void onViewClicked(View view) {
        switch (view.getId()) {

            case R.id.btnSaveAddress:
                validateFields();
                break;
            case R.id.btnSaveMapAddress:
                validateFields();
                break;


            case R.id.tvAddressTab:

                tvOnMapTab.setBackgroundColor(Color.TRANSPARENT);
                tvAddressTab.setBackgroundResource(R.drawable.primary_dark_top_bottom_left_corner_button_bg_drawable);
                llAddressTabSection.setVisibility(View.VISIBLE);
                llMapTabSection.setVisibility(View.GONE);

                break;

            case R.id.tvOnMapTab:

                tvAddressTab.setBackgroundColor(Color.TRANSPARENT);
                tvOnMapTab.setBackgroundResource(R.drawable.primary_dark_top_bottom_right_corner_button_bg_drawable);
                llAddressTabSection.setVisibility(View.GONE);
                llMapTabSection.setVisibility(View.VISIBLE);

                break;

//            case R.id.flBusinessCategory:
//            case R.id.tvBusinessCategory:
//                Toast.makeText(activityReference, "Business Category Click..", Toast.LENGTH_SHORT).show();
//                break;

//            case R.id.flCity:
//            case R.id.tvCity:
//                Toast.makeText(activityReference, "City Click..", Toast.LENGTH_SHORT).show();
//                break;
            case R.id.ivProfileImage:
            case R.id.ivCameraIcon:

                if(!Utils.isEmptyOrNull(profileId) && profileViewModel.getLogo() != null){

                    TypedValue typedValue = new TypedValue();
                    @ColorInt int color;
                    activityReference.getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
                    color = typedValue.data;


                    BottomSheetMenuDialog mBottomSheetDialog = new BottomSheetBuilder(activityReference)
                            .setMode(BottomSheetBuilder.MODE_LIST)
                            .setIconTintColor(color)
                            .addItem(0, activityReference.getString(R.string.upload_photo), R.drawable.ic_camera)
                            .addDividerItem()
                            .addItem(1, activityReference.getString(R.string.remove), R.drawable.ic_cancel)
                            .setItemClickListener(new BottomSheetItemClickListener() {
                                @Override
                                public void onBottomSheetItemClick(MenuItem item) {
                                    switch (item.getItemId()) {
                                        case 0:
                                            activityReference.openImagePicker(ProfileFragment.this);
                                            break;
                                        case 1:
                                            profileViewModel.setImageFile(null);
                                            profileViewModel.setImageDeleted(true);
                                            ivProfileImage.setImageResource(R.drawable.image_placeholder);
                                            break;
                                    }
                                    Log.d("Item click", item.getTitle() + "");
                                }
                            })
                            .createDialog();
                    mBottomSheetDialog.show();

                }else{
                    activityReference.openImagePicker(ProfileFragment.this);
                }
                break;
        }
    }

    @Override
    public void onPhotoClicked(ArrayList<File> file) {

        Log.d(ProfileFragment.class.getName(), file.toString());

        if(file.size() > 0){
            imageFile = file.get(0);
            ivProfileImage.setImageBitmap(BitmapFactory.decodeFile(imageFile.getAbsolutePath()));
        }
    }

    @Override
    public void onDocClicked(ArrayList<String> files) {

    }

    public void setMobileNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }
}
