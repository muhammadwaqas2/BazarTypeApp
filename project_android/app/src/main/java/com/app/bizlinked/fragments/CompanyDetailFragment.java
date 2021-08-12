package com.app.bizlinked.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.bizlinked.R;
import com.app.bizlinked.constant.AppConstant;
import com.app.bizlinked.customViews.TitleBar;
import com.app.bizlinked.dialog.DialogClass;
import com.app.bizlinked.fragments.baseClass.BaseFragment;
import com.app.bizlinked.helpers.common.Utils;
import com.app.bizlinked.models.enums.LinkRelationEnum;
import com.app.bizlinked.models.enums.LinkStatusEnum;
import com.app.bizlinked.models.enums.ProductViewEnum;
import com.app.bizlinked.models.enums.ProfileViewEnum;
import com.app.bizlinked.models.viewmodel.LinkViewModel;
import com.app.bizlinked.models.viewmodel.LinksViewModel;
import com.app.bizlinked.models.viewmodel.ProfileViewModal;

import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;

public class CompanyDetailFragment extends BaseFragment {


    @BindView(R.id.llMainView)
    LinearLayout llMainView;

    @BindView(R.id.llShowProducts)
    LinearLayout llShowProducts;

    @BindView(R.id.tvProfileName)
    TextView tvProfileName;
    @BindView(R.id.tvProfileCategory)
    TextView tvProfileCategory;
    @BindView(R.id.tvProfilePhoneNo)
    TextView tvProfilePhoneNo;
    @BindView(R.id.tvProfileAddress)
    TextView tvProfileAddress;
    @BindView(R.id.tvProfileCity)
    TextView tvProfileCity;

    @BindView(R.id.ivProfileCoverImage)
    CircleImageView ivProfileCoverImage;
    @BindView(R.id.ivProfileMapView)
    ImageView ivProfileMapView;

    //View Modal
    ProfileViewModal profileViewModal = null;

    //For Current logged in Company
    private String profileId = null;

    //For Any Company Profile
    LinkViewModel linkViewModel = null;
    ProfileViewModal companyProfileViewModel = null;
    ProfileViewEnum profileViewEnum;
    LinkRelationEnum companyRelation;
    TitleBar titleBar = null;




    public CompanyDetailFragment() {
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

        if(profileViewEnum.equals(ProfileViewEnum.COMPANY_PROFILE)){

            if(linkViewModel != null){
                titleBar.showHeaderTitle(linkViewModel.getName());
            }else if(profileViewModal != null){
                titleBar.showHeaderTitle(profileViewModal.getName());
            }

        }else if(profileViewEnum.equals(ProfileViewEnum.LOGGED_IN_COMPANY_PROFILE)){
            titleBar.showHeaderTitle(getResources().getString(R.string.profile_text));
        }

        titleBar.setLeftTitleText(getResources().getString(R.string.back));
        titleBar.showLeftIconAndListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activityReference.onBackPressed();
            }
        });

        //Logged in user has option to Edit profile
        if (profileViewEnum.equals(ProfileViewEnum.LOGGED_IN_COMPANY_PROFILE)) {
            titleBar.showRightTextAndSetListener(activityReference.getString(R.string.edit_text), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ProfileFragment profileFragment = new ProfileFragment();
                    profileFragment.setProfileId(profileId);
                    activityReference.addSupportFragment(profileFragment, AppConstant.TRANSITION_TYPES.SLIDE);
                }
            });

        } else if (profileViewEnum.equals(ProfileViewEnum.COMPANY_PROFILE)) {
            //Company has option to unlink
           if(linkViewModel != null && linkViewModel.getStatus().equalsIgnoreCase(LinkStatusEnum.LINKED.getValue())){

               titleBar.showRightTextAndColorAndClickListener(View.VISIBLE, activityReference.getString(R.string.unlink), ContextCompat.getColor(activityReference, R.color.red),new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {


//
                       DialogClass.createYesNoDialog(activityReference, new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialog, int which) {

                               if(linkViewModel != null){
                                   companyProfileViewModel.unlink(linkViewModel.getLinkedCompanyID());
                                   onCustomBackPressed();
                               }else if(companyProfileViewModel != null){
                                   companyProfileViewModel.unlink(companyProfileViewModel.getId());
                               }

                               //Remove icon
                               titleBar.showRightTextAndColorAndClickListener(View.GONE, "", ContextCompat.getColor(activityReference, R.color.red), null);

                               dialog.dismiss();

                               onCustomBackPressed();

                           }
                       }, R.string.are_you_sure_you_want_to_unlink).show();

                   }
               });
           }

        }
    }

    @Override
    protected int getMainLayout() {
        return R.layout.fragment_company_detail;
    }

    @Override
    protected void onFragmentViewReady(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState, View rootView) {

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//
//                if(llMainView != null && isAdded() && isVisible()){
//                    int duration = 300;
//                    for (int index = 0; index < llMainView.getChildCount(); index++) {
//                        AnimationHelpers.animate(Techniques.SlideInUp, duration, llMainView.getChildAt(index));
//                        duration += 100;
//                    }
//                }
//            }
//        },100);


        //Setup Screen For category
        initializeViewModal();
        populateDataOnUI();
    }

    @Override
    public void afterBackStackChange(){

        setTitleBar(titleBar);

        if(profileViewEnum.equals(ProfileViewEnum.LOGGED_IN_COMPANY_PROFILE)){
            getMainActivity().setBottomMenuFromSelectedOption(AppConstant.BOTTOM_MENU.SETTING);
            llShowProducts.setVisibility(View.GONE);
        }else if(profileViewEnum.equals(ProfileViewEnum.COMPANY_PROFILE)) {

            getMainActivity().setBottomMenuFromSelectedOption(AppConstant.BOTTOM_MENU.LINKS);


            //In case of Supplier Only
            if(companyRelation != null && companyRelation.getValue().equalsIgnoreCase(LinkRelationEnum.SUPPLIER.getValue())){
                llShowProducts.setVisibility(View.VISIBLE);
                llShowProducts.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        ProductCategoryListFragment productCategoryListFragment = new ProductCategoryListFragment();
                        productCategoryListFragment.setProductViewType(ProductViewEnum.COMPANY_PROFILE);
                        productCategoryListFragment.setLinkViewModel(linkViewModel);
                        productCategoryListFragment.setCompanyProfileViewModel(profileViewModal);
                        activityReference.realAddSupportFragment(productCategoryListFragment, AppConstant.TRANSITION_TYPES.SLIDE);
                    }
                });
            }


        }
    }


    private void initializeViewModal() {


        //Profile Model
        profileViewModal = ViewModelProviders.of(this).get(ProfileViewModal.class);

        if(profileViewEnum.equals(ProfileViewEnum.LOGGED_IN_COMPANY_PROFILE)){
            //init by company id
            if(!Utils.isEmptyOrNull(profileId)){
                profileViewModal.init(profileId);
            }
        }else if(profileViewEnum.equals(ProfileViewEnum.COMPANY_PROFILE)){
            //init by company objcte
            if(companyProfileViewModel != null){
                profileViewModal.init(companyProfileViewModel);
            }
        }

    }

    private void populateDataOnUI() {

        if(profileViewModal != null){


            //Name
            if(!Utils.isEmptyOrNull(profileViewModal.getName())){
                tvProfileName.setText(profileViewModal.getName());
            }

            //Mobile Phone
            if(!Utils.isEmptyOrNull(profileViewModal.getMobileNumber())){
                tvProfilePhoneNo.setText(profileViewModal.getMobileNumber());
            }

            //Address
            StringBuilder address = new StringBuilder();

            if(!Utils.isEmptyOrNull(profileViewModal.getSuitNumber())){

                if(address.toString().length() > 0)
                    address.append(",");

                address.append(profileViewModal.getSuitNumber());
            }

            if(!Utils.isEmptyOrNull(profileViewModal.getStreetAddress())){

                if(address.toString().length() > 0)
                    address.append(",");

                address.append(profileViewModal.getStreetAddress());

            }

            if(!Utils.isEmptyOrNull(profileViewModal.getMarketName())){

                if(address.toString().length() > 0)
                    address.append(",");

                address.append(profileViewModal.getMarketName());
            }

            //Set Address
            tvProfileAddress.setText(address.toString().trim());

            //Selected City Set
            if(profileViewModal.getSelectedCity() != null){
                tvProfileCity.setText(profileViewModal.getSelectedCity().getName());
            }

            //Selected Business Category
            if(profileViewModal.getBusinessCategory() != null && profileViewModal.getSelectedCategory() != null){
                tvProfileCategory.setText(profileViewModal.getSelectedCategory().getName());
            }

            // Cover Image
            if(profileViewModal.getCoverImage() != null && profileViewModal.getCoverImage().getData() != null && profileViewModal.getCoverImage().getData().length > 0){
                Bitmap imageBitmap = BitmapFactory.decodeByteArray(profileViewModal.getCoverImage().getData(), 0, profileViewModal.getCoverImage().getData().length);
                ivProfileCoverImage.setImageBitmap(imageBitmap);
            }else if(profileViewModal.getLogo() != null && profileViewModal.getLogo().getData() != null && profileViewModal.getLogo().getData().length > 0){
                Bitmap imageBitmap = BitmapFactory.decodeByteArray(profileViewModal.getLogo().getData(), 0, profileViewModal.getLogo().getData().length);
                ivProfileCoverImage.setImageBitmap(imageBitmap);
            }

            //Lat, lng
            if(profileViewModal.getLatitude() != null && profileViewModal.getLongitude() != null){
                String imageURL = "http://maps.google.com/maps/api/staticmap?center=" + profileViewModal.getLatitude() + "," + profileViewModal.getLongitude() + "&zoom=13&size=500x500&sensor=false&key=" + activityReference.getString(R.string.google_map_key);
                Utils.setImageFromGlide(activityReference, ivProfileMapView, null, imageURL);
            }
        }
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    public void setType(ProfileViewEnum profileViewEnum) {
        this.profileViewEnum = profileViewEnum;
    }

    public void setCompanyRelation(LinkRelationEnum companyRelation) {
        this.companyRelation = companyRelation;
    }

    public void setCompanyProfileViewModel(ProfileViewModal companyProfileViewModel) {
        this.companyProfileViewModel = companyProfileViewModel;
    }


    public void setLinkViewModel(LinkViewModel linkViewModel) {
        this.linkViewModel = linkViewModel;
    }
}


