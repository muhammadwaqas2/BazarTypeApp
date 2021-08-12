package com.app.bizlinked.fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.bizlinked.R;
import com.app.bizlinked.adapters.CompanyPackageAdapter;
import com.app.bizlinked.constant.AppConstant;
import com.app.bizlinked.customViews.TitleBar;
import com.app.bizlinked.fragments.baseClass.BaseFragment;
import com.app.bizlinked.helpers.animation.AnimationHelpers;
import com.app.bizlinked.helpers.common.Utils;
import com.app.bizlinked.helpers.sync_helpers.SyncManager;
import com.app.bizlinked.listener.custom.DataRecyclerViewClickInterface;
import com.app.bizlinked.models.db.CompanyPackage;
import com.app.bizlinked.models.enums.EntityEnum;
import com.app.bizlinked.models.viewmodel.CompanyPackageViewModel;
import com.app.bizlinked.models.viewmodel.WalletViewModel;
import com.app.bizlinked.webhelpers.WebApiRequest;
import com.daimajia.androidanimations.library.Techniques;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import io.realm.RealmResults;

public class CompanyPackageFragment extends BaseFragment {


    @BindView(R.id.tvCompanyPackageDesc)
    TextView tvCompanyPackageDesc;

    @BindView(R.id.llPayCategoryList)
    LinearLayout llPayCategoryList;

    private CompanyPackage selectedCompanyPackageObj = null;

    //View Modal
    CompanyPackageViewModel companyPackageViewModel = null;


    @BindView(R.id.llMainView)
    LinearLayout llMainView;

    public CompanyPackageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCustomBackPressed() {
        activityReference.onPageBack();
    }

    @Override
    protected void setTitleBar(TitleBar titleBar) {

        titleBar.showHeaderView();
        titleBar.setLeftTitleText(getResources().getString(R.string.purchase));
        titleBar.showLeftIconAndListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activityReference.onBackPressed();
            }
        });
    }

    @Override
    protected int getMainLayout() {
        return R.layout.fragment_company_package;
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


        //Setup Screen For Wallet
        initializeViewModal();
        populateDataOnUI();
    }

    private void initializeViewModal() {

        // Get the ViewModel.
        companyPackageViewModel = ViewModelProviders.of(this).get(CompanyPackageViewModel.class);

        companyPackageViewModel.init(selectedCompanyPackageObj);

    }

    private void populateDataOnUI() {

        if(companyPackageViewModel != null) {

            //Name
            if(!Utils.isEmptyOrNull(companyPackageViewModel.getName())){
                tvCompanyPackageDesc.setText(companyPackageViewModel.getName());
            }


            if(companyPackageViewModel.getPrice() > 0.0){

                ArrayList<String> payMethods = new ArrayList<>();
                payMethods.add("Easy Paisa");
                payMethods.add("Jazz Cash");
                payMethods.add("Debit/Credit Card");

                for (int i = 0; i < payMethods.size(); i++) {

                    View payPackage = activityReference.getLayoutInflater().inflate(R.layout.item_company_package_pay, null);
                    ((TextView)payPackage.findViewById(R.id.tvCompanyPackagePayText)).setText(payMethods.get(i));

                    payPackage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Utils.showSnackBar(activityReference, getView(), "Coming Soon...", ContextCompat.getColor(activityReference, R.color.dark_gray));
                        }
                    });

                    llPayCategoryList.addView(payPackage);
                }

            }else{

                View payPackage = activityReference.getLayoutInflater().inflate(R.layout.item_company_package_pay, null);
                ((TextView)payPackage.findViewById(R.id.tvCompanyPackagePayText)).setText(activityReference.getString(R.string.claim_fo_free));

                payPackage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        purchasePackage();
                    }
                });

                llPayCategoryList.addView(payPackage);

            }



        }

    }

    private void purchasePackage() {

        HashMap<String, Object> params = new HashMap<>();

        String url = AppConstant.ServerAPICalls.purchasePackageURL + "/" + companyPackageViewModel.getCompanyPackage().getPackage().getId();

        WebApiRequest.getInstance(activityReference, true).purchasePackageFromServer(url, params, new WebApiRequest.APIRequestDataCallBack() {
            @Override
            public void onSuccess(JsonElement response) {

                if(response != null && response.isJsonObject()){

                    JsonObject responseObject = response.getAsJsonObject();

                    SyncManager.getInstance().addEntityToQueue(EntityEnum.CompanyPackage);

                    onCustomBackPressed();
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

//    private void getCreditBalanceFromServer() {
//
//        HashMap<String, Object> params = new HashMap<>();
//
//        WebApiRequest.getInstance(activityReference, false).getCreditBalanceFromServer(AppConstant.ServerAPICalls.creditBalanceURL, params, new WebApiRequest.APIRequestDataCallBack() {
//            @Override
//            public void onSuccess(JsonElement response) {
//
//                if(response != null && response.isJsonObject()){
//
//                    JsonObject responseObject = response.getAsJsonObject();
//
//                    if(responseObject.has("balance")){
//                        walletViewModel.getCredit().setValue(responseObject.get("balance").getAsInt());
//                    }
//                }
//            }
//
//            @Override
//            public void onError(String errorResponse) {
//
//            }
//
//            @Override
//            public void onNoNetwork() {
//
//            }
//        });
//
//    }
//
//
//    private void initializeAdapter() {
//
//        RealmResults<CompanyPackage> companyPackages = walletViewModel.getAllCompanyPackagesFromDB();
//
//        checkHasCompanyPackages(companyPackages != null && companyPackages.size() > 0);
//
//        companyPackageAdapter = new CompanyPackageAdapter(activityReference, companyPackages, new DataRecyclerViewClickInterface<CompanyPackage>() {
//            @Override
//            public void onClick(CompanyPackage object) {
//                Toast.makeText(activityReference, object.getPackage().getName(), Toast.LENGTH_SHORT).show();
////                productCategoryDataRecyclerViewClickInterface.onClick(object);
//
//            }
//
//            @Override
//            public void onEditClick(CompanyPackage object) {
//                Toast.makeText(activityReference, object.getPackage().getName(), Toast.LENGTH_SHORT).show();
//
//            }
//        });
//
//        rvCompanyPackage.setLayoutManager(new LinearLayoutManager(activityReference));
//        rvCompanyPackage.setAdapter(companyPackageAdapter);
//
//    }

//    private void checkHasCompanyPackages(boolean isData) {
//
//        if(isData){
//            rvCompanyPackage.setVisibility(View.VISIBLE);
//            llNoCompanyPackageDataFound.setVisibility(View.GONE);
//
//        }else{
//            rvCompanyPackage.setVisibility(View.GONE);
//            rvCompanyPackage.setVisibility(View.VISIBLE);
//        }
//
//    }


    public void setCompanyPackageObject(CompanyPackage selectedCompanyPackageObj) {
        this.selectedCompanyPackageObj = selectedCompanyPackageObj;
    }
}