package com.app.bizlinked.fragments;


import android.arch.lifecycle.ViewModelProviders;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.bizlinked.BuildConfig;
import com.app.bizlinked.R;
import com.app.bizlinked.activities.MainActivity;
import com.app.bizlinked.constant.AppConstant;
import com.app.bizlinked.customViews.TitleBar;
import com.app.bizlinked.db.AppDBHelper;
import com.app.bizlinked.fragments.baseClass.BaseFragment;
import com.app.bizlinked.helpers.animation.AnimationHelpers;
import com.app.bizlinked.helpers.common.KeyboardHelper;
import com.app.bizlinked.helpers.common.Utils;
import com.app.bizlinked.helpers.sync_helpers.SyncManager;
import com.app.bizlinked.listener.custom.DataSaveAndConvertInterface;
import com.app.bizlinked.models.db.City;
import com.app.bizlinked.models.db.Profile;
import com.app.bizlinked.models.enums.EntityEnum;
import com.app.bizlinked.models.sync_response.ProfileSyncResponse;
import com.app.bizlinked.models.viewmodel.ProfileViewModal;
import com.app.bizlinked.webhelpers.WebApiRequest;
import com.daimajia.androidanimations.library.Techniques;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.poovam.pinedittextfield.LinePinField;
import com.poovam.pinedittextfield.PinField;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;
import io.github.krtkush.lineartimer.LinearTimer;
import io.github.krtkush.lineartimer.LinearTimerView;

/**
 * A simple {@link Fragment} subclass.
 */
public class PhoneNumberVerificationFragment extends BaseFragment {


    @BindView(R.id.linePinFiledCode)
    LinePinField linePinFiledCode;

    @BindView(R.id.btnSignIn)
    Button btnSignIn;

    @BindView(R.id.tvTitleText)
    TextView tvTitleText;

    @BindView(R.id.tvResendCode)
    TextView tvResendCode;

    @BindView(R.id.llMainView)
    LinearLayout llMainView;

    @BindView(R.id.ltResendTimer)
    LinearTimerView ltResendTimer;

    @BindView(R.id.flTimerView)
    FrameLayout flTimerView;

    @BindView(R.id.tvTimer)
    TextView tvTimer;


    private long RESEND_CODE_TIMER_IN_SEC = 0;
    private int OTP_LENGTH = 0;
    private LinearTimer linearTimerResendCodeObject = null;
    private String phoneNumber = null;


    //View Modal
    ProfileViewModal profileViewModel;


    public PhoneNumberVerificationFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCustomBackPressed() {
        preferenceHelper.putUserTokenType(AppConstant.AUTH_TOKEN_TYPES.BASIC);
        stopCodeTimerEnd();
        activityReference.onPageBack();
    }

    @Override
    protected void setTitleBar(TitleBar titleBar) {

        titleBar.showHeaderView();
        titleBar.setLeftTitleText(getResources().getString(R.string.phone_number_verification_text));
        titleBar.showLeftIconAndListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCustomBackPressed();
            }
        });

    }

    @Override
    protected int getMainLayout() {
        return R.layout.fragment_phone_number_verification;
    }

    @Override
    protected void onFragmentViewReady(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState, View rootView) {


        //EdiText Number Listener
        setListener();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (llMainView == null && rootView != null && rootView.findViewById(R.id.llMainView) != null) {
                    llMainView = rootView.findViewById(R.id.llMainView);
                }

                if (llMainView != null) {
                    int duration = 500;
                    for (int index = 0; index < llMainView.getChildCount(); index++) {
                        AnimationHelpers.animate(Techniques.SlideInUp, duration, llMainView.getChildAt(index));
                        duration += 100;
                    }
                }

                //AnimationHelpers.animate(Techniques.BounceInUp, 600, flLoginView);
            }
        }, 300);

        setupPhoneNumberVerificationScreen();


        // Get the ViewModel.
        profileViewModel = ViewModelProviders.of(this).get(ProfileViewModal.class);

    }

    @Override
    public void afterBackStackChange() {
        super.afterBackStackChange();

        //linePinFiledCode.performClick();
//        linePinFiledCode.requestFocus();
    }

    private void setupPhoneNumberVerificationScreen() {

        if (linePinFiledCode != null) {
            //OTP Length
            linePinFiledCode.setNumberOfFields(OTP_LENGTH);
        }


//        KeyboardHelper.showSoftKeyboard(activityReference, getView());

        //Set Resend Timer (OTP Expire Timer)
        setResendTimerListener();
        resendCodeTimerStart(true);

        //Set Phone Number
        tvTitleText.setText(activityReference.getString(R.string.we_sent_you_verification) + " " + phoneNumber);
    }


    private void resendCodeTimerStart(boolean start) {
        if (linearTimerResendCodeObject != null) {

            //Comment hide logic
//            tvResendCode.setVisibility(View.GONE);

            //New Code to Disabled the Resend Code Button
            tvResendCode.setVisibility(View.VISIBLE);
            tvResendCode.setAlpha(0.5f);
            tvResendCode.setEnabled(false);

            AnimationHelpers.animate(Techniques.FadeIn, 600, flTimerView);
            if (start)
                linearTimerResendCodeObject.startTimer();
            else
                linearTimerResendCodeObject.restartTimer();
        }

    }

    private void resendCodeTimerEnd() {
        if (linearTimerResendCodeObject != null && ltResendTimer != null) {

            flTimerView.setVisibility(View.GONE);


            //New Code to Enabled the Resend Code Button
            tvResendCode.setVisibility(View.VISIBLE);
            tvResendCode.setAlpha(1f);
            tvResendCode.setEnabled(true);

            //Comment show logic
//            AnimationHelpers.animate(Techniques.FadeIn, 600, tvResendCode);

        }
    }

    private void stopCodeTimerEnd() {
        try {
            if (linearTimerResendCodeObject != null && flTimerView != null && tvResendCode != null) {
                linearTimerResendCodeObject.pauseTimer();
                linearTimerResendCodeObject = null;
//            flTimerView.setVisibility(View.GONE);
//            tvResendCode.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setResendTimerListener() {
        linearTimerResendCodeObject = new LinearTimer.Builder()
                .linearTimerView(ltResendTimer)
                .duration(RESEND_CODE_TIMER_IN_SEC * 1000)
                .timerListener(new LinearTimer.TimerListener() {
                    @Override
                    public void animationComplete() {
                        resendCodeTimerEnd();
                    }

                    @Override
                    public void timerTick(long tickUpdateInMillis) {

                        try {

                            long timerInSec = RESEND_CODE_TIMER_IN_SEC - (tickUpdateInMillis / 1000);

                            int[] formattedTime = Utils.splitToComponentTimes(timerInSec);
                            if (tvTimer != null) {
                                String minsString = formattedTime[1] < 10 ? "0" + formattedTime[1] : "" + formattedTime[1];
                                String secString = formattedTime[2] < 10 ? "0" + formattedTime[2] : "" + formattedTime[2];
                                tvTimer.setText(minsString + ":" + secString + " s");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onTimerReset() {

                    }
                })
                .build();

    }


    private void setListener() {

        linePinFiledCode.setOnTextCompleteListener(new PinField.OnTextCompleteListener() {
            @Override
            public boolean onTextComplete(@NotNull String enteredText) {
                return true; // Return true to keep the keyboard open else return false to close the keyboard
            }
        });

    }


    @OnClick({R.id.btnSignIn, R.id.tvResendCode})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnSignIn:
                String enteredPin = linePinFiledCode.getText().toString().trim();
                if (!Utils.isEmptyOrNull(enteredPin)
                        && enteredPin.length() == OTP_LENGTH) {

                    preferenceHelper.putUserTokenType(AppConstant.AUTH_TOKEN_TYPES.BASIC);
                    verifyCodeFromServer();
                } else {
                    Utils.showSnackBar(activityReference, getView(), activityReference.getString(R.string.err_enter_pin_correctly), ContextCompat.getColor(activityReference, R.color.grayColor));
                }
                break;

            case R.id.tvResendCode:
                preferenceHelper.putUserTokenType(AppConstant.AUTH_TOKEN_TYPES.BEARER);
                resendVerifyCode();
                break;
        }
    }

    private void resendVerifyCode() {

        HashMap<String, Object> params = new HashMap<>();

        if (!Utils.isEmptyOrNull(phoneNumber))
            params.put("phoneNumber", phoneNumber);

        WebApiRequest.getInstance(activityReference, true).generateOTPFromServer(params, new WebApiRequest.APIRequestDataCallBack() {
            @Override
            public void onSuccess(JsonElement response) {

                if (response != null && response.isJsonObject()) {

                    if (!response.isJsonNull()) {

                        JsonObject responseObject = response.getAsJsonObject();

                        if (responseObject.has("expiresInSeconds")) {

                            RESEND_CODE_TIMER_IN_SEC = responseObject.get("expiresInSeconds").getAsInt();
                            setOTPLengthFromServer(4);

                            resendCodeTimerStart(false);

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

    private void verifyCodeFromServer() {

        HashMap<String, Object> params = new HashMap<>();


        params.put("grant_type", BuildConfig.GRANT_TYPE);
        params.put("client_id", BuildConfig.CLIENT_ID);

        if (!Utils.isEmptyOrNull(phoneNumber))
            params.put("username", phoneNumber);

        if (!Utils.isEmptyOrNull(linePinFiledCode.getText().toString().trim()))
            params.put("password", linePinFiledCode.getText().toString().trim());

        WebApiRequest.getInstance(activityReference, true).verifyOTPFromServer(params, new WebApiRequest.APIRequestDataCallBack() {
            @Override
            public void onSuccess(JsonElement response) {

                if (response != null && response.isJsonObject()) {

                    //Stop Timer
                    stopCodeTimerEnd();

                    if (!response.isJsonNull()) {

                        JsonObject responseObject = response.getAsJsonObject();

                        if (responseObject.has("access_token")) {
                            preferenceHelper.putUserTokenType(responseObject.get("token_type").getAsString());
                            preferenceHelper.putUserToken(responseObject.get("access_token").getAsString());
                            preferenceHelper.putUserRefreshToken(responseObject.get("refresh_token").getAsString());


                            //City and Business Category
                            SyncManager.getInstance().addEntityToQueue(EntityEnum.BusinessCategory);
                            SyncManager.getInstance().addEntityToQueue(EntityEnum.City);


                            //Check Company Profile
                            getCompanyProfile();
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

    private void getCompanyProfile() {

        HashMap<String, Object> params = new HashMap<>();

        getMainActivity().getFirebaseToken();
        if (!Utils.isEmptyOrNull(preferenceHelper.getDeviceToken()))
            params.put("deviceToken", preferenceHelper.getDeviceToken());

        params.put("platform", AppConstant.ANDROID.toUpperCase());

        WebApiRequest.getInstance(activityReference, true).getCompanyProfileFromServer(params, new WebApiRequest.APIRequestDataCallBack() {
            @Override
            public void onSuccess(JsonElement response) {

                if (response != null && response.isJsonObject()) {

                    if (!response.isJsonNull()) {

                        //if user has profile

                        JsonObject responseObject = response.getAsJsonObject();
                        if (responseObject.has("id")) {

                            preferenceHelper.putProfileId(responseObject.get("id").getAsString());
                            preferenceHelper.setLoginStatus(true);


                            //Perform in Background Thread

                            SyncManager.getInstance().addEntityToQueue(EntityEnum.Profile);
                            SyncManager.getInstance().addEntityToQueue(EntityEnum.Link);
                            SyncManager.getInstance().addEntityToQueue(EntityEnum.Image);
                            SyncManager.getInstance().addEntityToQueue(EntityEnum.ProductCategory);
                            SyncManager.getInstance().addEntityToQueue(EntityEnum.Product);
                            SyncManager.getInstance().addEntityToQueue(EntityEnum.ProductImage);
                            SyncManager.getInstance().addEntityToQueue(EntityEnum.Order);
                            SyncManager.getInstance().addEntityToQueue(EntityEnum.OrderStatus);
                            SyncManager.getInstance().addEntityToQueue(EntityEnum.CompanyPackage);
                            SyncManager.getInstance().addEntityToQueue(EntityEnum.ImageSyncUtility);


                            activityReference.emptyBackStack();
                            CustomerSupplierListFragment customerSupplierListFragment = new CustomerSupplierListFragment();
                            activityReference.fragmentBackStackChangeListener(customerSupplierListFragment);
                        }

                    }
                } else {

                    ProfileFragment profileFragment = new ProfileFragment();
                    profileFragment.setMobileNumber(phoneNumber);
                    activityReference.addSupportFragment(profileFragment, AppConstant.TRANSITION_TYPES.SLIDE);
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


    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;

    }

    public void setOTPExpireTimeInSec(int otpExpiresInSeconds) {
        RESEND_CODE_TIMER_IN_SEC = otpExpiresInSeconds;
    }

    public void setOTPLengthFromServer(int OTPLength) {
        OTP_LENGTH = OTPLength;
    }
}