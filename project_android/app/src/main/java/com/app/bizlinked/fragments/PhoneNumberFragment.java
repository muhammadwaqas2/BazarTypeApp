package com.app.bizlinked.fragments;


import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.bizlinked.BuildConfig;
import com.app.bizlinked.R;
import com.app.bizlinked.constant.AppConstant;
import com.app.bizlinked.customViews.TitleBar;
import com.app.bizlinked.fragments.baseClass.BaseFragment;
import com.app.bizlinked.helpers.animation.AnimationHelpers;
import com.app.bizlinked.helpers.common.Utils;
import com.app.bizlinked.webhelpers.WebApiRequest;
import com.daimajia.androidanimations.library.Techniques;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mobsandgeeks.saripaar.annotation.Length;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Order;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class PhoneNumberFragment extends BaseFragment {



    @Order(1)
    @NotEmpty(trim = true, messageResId = R.string.error_dial_code)
    @BindView(R.id.tvCountryCode)
    TextView tvCountryCode;

    @BindView(R.id.flDialCodeContainer)
    FrameLayout flDialCodeContainer;

    @Order(2)
    @NotEmpty(trim = true)
    @Length(min = AppConstant.VALIDATION_RULES.PHONE_MIN_LENGTH, messageResId = R.string.error_phone)
    @BindView(R.id.etPhoneNumber)
    EditText etPhoneNumber;


    @BindView(R.id.btnNext)
    Button btnNext;


    @BindView(R.id.llMainView)
    LinearLayout llMainView;


    public PhoneNumberFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCustomBackPressed() {
        activityReference.onPageBack();
    }

    @Override
    protected void setTitleBar(TitleBar titleBar) {

        titleBar.hideHeaderView();
    }

    @Override
    protected int getMainLayout() {
        return R.layout.fragment_phone_number;
    }

    @Override
    protected void onFragmentViewReady(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState, View rootView) {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if(llMainView != null){
                    int duration = 500;
                    for (int index = 0; index < llMainView.getChildCount(); index++) {
                        AnimationHelpers.animate(Techniques.SlideInUp, duration, llMainView.getChildAt(index));
                        duration += 100;
                    }
                }
                //AnimationHelpers.animate(Techniques.BounceInUp, 600, flLoginView);
            }
        },200);


        getAccessTokenFromServer();
        setTextWatcherListener();
    }

    private void setTextWatcherListener() {

        // User Can't start his/her number with 0

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,int count) { }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,int after) {}
            @Override
            public void afterTextChanged(Editable s) {
                //Due To Pakistan Phone number start with 3
                if (s.toString().length() == 1 && !s.toString().startsWith("3")) {
                    s.clear();
                }
            }
        };

        //Set Listener
        etPhoneNumber.addTextChangedListener(textWatcher);

    }

    private void getAccessTokenFromServer(){

        HashMap<String, Object> params = new HashMap<>();

        params.put("grant_type", BuildConfig.GRANT_TYPE);
        params.put("client_id", BuildConfig.CLIENT_ID);
        params.put("username", BuildConfig.ADMIN_USERNAME);
        params.put("password", BuildConfig.ADMIN_PASSWORD);


        WebApiRequest.getInstance(activityReference, true).getAccessTokenFromServer(params, new WebApiRequest.APIRequestDataCallBack() {
            @Override
            public void onSuccess(JsonElement response) {

                if (response != null && response.isJsonObject()) {

                    if(!response.isJsonNull()){

                        JsonObject responseObject = response.getAsJsonObject();

                        if(responseObject.has("access_token")){
                            preferenceHelper.putUserTokenType(responseObject.get("token_type").getAsString());
                            preferenceHelper.putUserToken(responseObject.get("access_token").getAsString());
                            preferenceHelper.putUserRefreshToken(responseObject.get("refresh_token").getAsString());
                        }
                    }
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

    private void generateOTPFromServer(){

        HashMap<String, Object> params = new HashMap<>();

        if (!Utils.isEmptyOrNull(etPhoneNumber.getText().toString().trim()))
            params.put("phoneNumber", "0092" + etPhoneNumber.getText().toString().trim());



        WebApiRequest.getInstance(activityReference, true).generateOTPFromServer(params, new WebApiRequest.APIRequestDataCallBack() {
            @Override
            public void onSuccess(JsonElement response) {

                if (response != null && response.isJsonObject()) {

                    if(!response.isJsonNull()){

                        JsonObject responseObject = response.getAsJsonObject();

                        if(responseObject.has("expiresInSeconds")){

                            PhoneNumberVerificationFragment phoneNumberVerificationFragment = new PhoneNumberVerificationFragment();
                            phoneNumberVerificationFragment.setPhoneNumber("0092" + etPhoneNumber.getText().toString().trim());
                            phoneNumberVerificationFragment.setOTPExpireTimeInSec(responseObject.get("expiresInSeconds").getAsInt());
                            phoneNumberVerificationFragment.setOTPLengthFromServer(4);
                            activityReference.addSupportFragment(phoneNumberVerificationFragment, AppConstant.TRANSITION_TYPES.SLIDE);

                        }
                    }
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



//
//    private void setUserPreferencesAndNavigateToHomeScreen(String token, String refresh) {
//
//        preferenceHelper.putUserToken(token);
//        preferenceHelper.putUserRefreshToken(refresh);
//        preferenceHelper.setLoginStatus(true);
//
////        activityReference.emptyBackStack();
////        activityReference.addSupportFragment(new NewsFeedFragment(), AppConstant.TRANSITION_TYPES.SLIDE);
//
//        if(getLoginActivity() != null){
//            getLoginActivity().getUserProfileAndSetupScreenForUser();
//        }else{
//            ((LoginActivity)activityReference).getUserProfileAndSetupScreenForUser();
//        }
//
//
//    }

    @Override
    public void onValidationSuccess() {
        generateOTPFromServer();
    }

    @Override
    public void onValidationFail() {}

    @OnClick({R.id.btnNext})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnNext:
                validateFields();
                break;
        }
    }


}