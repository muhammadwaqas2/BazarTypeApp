package com.app.bizlinked.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.bizlinked.R;
import com.app.bizlinked.constant.AppConstant;
import com.app.bizlinked.customViews.TitleBar;
import com.app.bizlinked.dialog.DialogClass;
import com.app.bizlinked.fragments.baseClass.BaseFragment;
import com.app.bizlinked.helpers.animation.AnimationHelpers;
import com.app.bizlinked.helpers.common.Utils;
import com.app.bizlinked.models.enums.ProfileViewEnum;
import com.app.bizlinked.models.viewmodel.ProfileViewModal;
import com.app.bizlinked.models.viewmodel.SettingViewModel;
import com.app.bizlinked.webhelpers.WebApiRequest;
import com.daimajia.androidanimations.library.Techniques;
import com.google.gson.JsonElement;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class SettingFragment extends BaseFragment {


    @BindView(R.id.tvCompanyName)
    TextView tvCompanyName;
    @BindView(R.id.tvEditProfile)
    TextView tvEditProfile;
    @BindView(R.id.ivCompanyImage)
    CircleImageView ivCompanyImage;
    @BindView(R.id.llLogout)
    LinearLayout llLogout;


    //View Modal
    SettingViewModel settingViewModal = null;
    //View Modal
    ProfileViewModal profileViewModal = null;


    @BindView(R.id.llMainView)
    LinearLayout llMainView;

    public SettingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCustomBackPressed() {
        activityReference.onPageBack();
    }

    @Override
    protected void setTitleBar(TitleBar titleBar) {

        titleBar.showHeaderView();
        titleBar.showHeaderTitle(activityReference.getString(R.string.setting_text));
//        titleBar.showRightSearchIconAndSetListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                activityReference.addSupportFragment(new ProductSearchFragment(), AppConstant.TRANSITION_TYPES.SLIDE);
////                Toast.makeText(activityReference, "Search Click...", Toast.LENGTH_SHORT).show();
//                //showSearchViewAndSearchFromServer();
//            }
//        });


    }

    @Override
    protected int getMainLayout() {
        return R.layout.fragment_setting;
    }

    @Override
    protected void onFragmentViewReady(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState, View rootView) {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if(llMainView != null && isAdded() && isVisible()){
                    int duration = 300;
                    for (int index = 0; index < llMainView.getChildCount(); index++) {
                        AnimationHelpers.animate(Techniques.SlideInUp, duration, llMainView.getChildAt(index));
                        duration += 100;
//                        llMainView.getChildAt(index).setVisibility(View.VISIBLE);
                    }
                }

                //AnimationHelpers.animate(Techniques.BounceInUp, 600, flLoginView);
            }
        },100);


        //Setup Screen For category
        initializeViewModal();
        populateDataOnUI();
    }

    private void initializeViewModal() {

        // Get the ViewModel.
        settingViewModal = ViewModelProviders.of(this).get(SettingViewModel.class);

        //Profile Model
        profileViewModal = ViewModelProviders.of(this).get(ProfileViewModal.class);

        //init by company id
        if(preferenceHelper != null && !Utils.isEmptyOrNull(preferenceHelper.getProfileId())){
            profileViewModal.init(preferenceHelper.getProfileId());
        }

    }

    private void populateDataOnUI() {

        if(profileViewModal != null){


            //Name
            if(!Utils.isEmptyOrNull(profileViewModal.getName())){
                tvCompanyName.setText(profileViewModal.getName());
            }

            if(profileViewModal.getCoverImage() != null && profileViewModal.getCoverImage().getData() != null && profileViewModal.getCoverImage().getData().length > 0){
                Bitmap imageBitmap = BitmapFactory.decodeByteArray(profileViewModal.getCoverImage().getData(), 0, profileViewModal.getCoverImage().getData().length);
                ivCompanyImage.setImageBitmap(imageBitmap);
            }else if(profileViewModal.getLogo() != null && profileViewModal.getLogo().getData() != null && profileViewModal.getLogo().getData().length > 0){
                Bitmap imageBitmap = BitmapFactory.decodeByteArray(profileViewModal.getLogo().getData(), 0, profileViewModal.getLogo().getData().length);
                ivCompanyImage.setImageBitmap(imageBitmap);
            }
        }
    }



    @OnClick({R.id.llLogout, R.id.tvEditProfile, R.id.tvCompanyName})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tvEditProfile:
            case R.id.tvCompanyName:

                CompanyDetailFragment companyDetailFragment = new CompanyDetailFragment();
                companyDetailFragment.setType(ProfileViewEnum.LOGGED_IN_COMPANY_PROFILE);
                if(preferenceHelper != null && !Utils.isEmptyOrNull(preferenceHelper.getProfileId())){
                    companyDetailFragment.setProfileId(preferenceHelper.getProfileId());
                }
                activityReference.addSupportFragment(companyDetailFragment, AppConstant.TRANSITION_TYPES.SLIDE);
                break;
            case R.id.llLogout:

                DialogClass.createMessageDialog(activityReference, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        logoutFromServer();
                    }
                }, activityReference.getString(R.string.are_you_sure_you_want_to_logout), activityReference.getString(R.string.alert)).show();

                break;
        }
    }

    private void logoutFromServer() {

        HashMap<String, Object> params = new HashMap<>();

        params.put("companyId", preferenceHelper.getProfileId());
//        params.put("deviceToken", preferenceHelper.getProfileId());

        WebApiRequest.getInstance(activityReference, true).logoutFromServer(AppConstant.ServerAPICalls.logoutURL, params, new WebApiRequest.APIRequestDataCallBack() {
            @Override
            public void onSuccess(JsonElement response) {

                settingViewModal.logout();
                getMainActivity().afterLogoutTask();

            }

            @Override
            public void onError(String errorResponse) {

            }

            @Override
            public void onNoNetwork() {

            }
        });

    }

}