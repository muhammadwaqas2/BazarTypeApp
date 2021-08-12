package com.app.bizlinked.fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.app.bizlinked.adapters.CategorySelectionAdapter;
import com.app.bizlinked.adapters.CompanyPackageAdapter;
import com.app.bizlinked.constant.AppConstant;
import com.app.bizlinked.customViews.TitleBar;
import com.app.bizlinked.db.AppDBHelper;
import com.app.bizlinked.fragments.baseClass.BaseFragment;
import com.app.bizlinked.helpers.animation.AnimationHelpers;
import com.app.bizlinked.helpers.common.Utils;
import com.app.bizlinked.helpers.sync_helpers.SyncManager;
import com.app.bizlinked.listener.custom.DataRecyclerViewClickInterface;
import com.app.bizlinked.listener.custom.DataSaveAndConvertInterface;
import com.app.bizlinked.models.db.CompanyPackage;
import com.app.bizlinked.models.db.Package;
import com.app.bizlinked.models.db.ProductCategory;
import com.app.bizlinked.models.db.Profile;
import com.app.bizlinked.models.db.SyncedEntity;
import com.app.bizlinked.models.enums.EntityEnum;
import com.app.bizlinked.models.sync_response.ProfileSyncResponse;
import com.app.bizlinked.models.viewmodel.ProfileViewModal;
import com.app.bizlinked.models.viewmodel.SettingViewModel;
import com.app.bizlinked.models.viewmodel.WalletViewModel;
import com.app.bizlinked.webhelpers.WebApiRequest;
import com.daimajia.androidanimations.library.Techniques;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import io.realm.RealmResults;

public class WalletFragment extends BaseFragment {


    @BindView(R.id.tvCreditBalance)
    TextView tvCreditBalance;
    @BindView(R.id.tvParaText)
    TextView tvParaText;


    //Recycler View Section
    @BindView(R.id.flCompanyPackageContainer)
    FrameLayout flCompanyPackageContainer;
    @BindView(R.id.swipeRefreshCompanyPackage)
    SwipeRefreshLayout swipeRefreshCompanyPackage;
    @BindView(R.id.rvCompanyPackage)
    RecyclerView rvCompanyPackage;
    @BindView(R.id.llNoCompanyPackageDataFound)
    LinearLayout llNoCompanyPackageDataFound;
    //Recycler View Section



    CompanyPackageAdapter companyPackageAdapter = null;

    //View Modal
    WalletViewModel walletViewModel = null;
//    //View Modal
//    ProfileViewModal profileViewModal = null;


    @BindView(R.id.llMainView)
    LinearLayout llMainView;

    public WalletFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCustomBackPressed() {
        activityReference.onPageBack();
    }

    @Override
    protected void setTitleBar(TitleBar titleBar) {

        titleBar.showHeaderView();
        titleBar.showHeaderTitle(activityReference.getString(R.string.my_wallet));
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
        return R.layout.fragment_wallet;
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


//        AppDBHelper.getRealmInstance().executeTransaction(new Realm.Transaction() {
//            @Override
//            public void execute(Realm realm) {
//                realm.where(SyncedEntity.class).equalTo("entityName", EntityEnum.CompanyPackage.getEntityName()).findAll().deleteAllFromRealm();
//                realm.where(CompanyPackage.class).findAll().deleteAllFromRealm();
//                realm.where(Package.class).findAll().deleteAllFromRealm();
//            }
//        });

        //Setup Screen For Wallet
        initializeViewModal();
        populateDataOnUI();

        initializeAdapter();
    }

    private void initializeViewModal() {

        // Get the ViewModel.
        walletViewModel = ViewModelProviders.of(this).get(WalletViewModel.class);

        //Set Packages Observer
        walletViewModel.getAllCompanyPackages().observe(this, new Observer<RealmResults<CompanyPackage>>() {
            @Override
            public void onChanged(@Nullable RealmResults<CompanyPackage> packages) {
                checkHasCompanyPackages(packages != null && packages.size() > 0);
            }
        });
    }

    private void populateDataOnUI() {

        if(walletViewModel != null) {


            walletViewModel.getCredit().observe(this, new Observer<Integer>() {
                @Override
                public void onChanged(@Nullable Integer credit) {
                    tvCreditBalance.setText(String.valueOf(credit));
                }
            });

//            if(walletViewModel.getCredit().getValue() == null){
                getCreditBalanceFromServer();
//            }

        }

    }

    private void getCreditBalanceFromServer() {

        HashMap<String, Object> params = new HashMap<>();

        WebApiRequest.getInstance(activityReference, false).getCreditBalanceFromServer(AppConstant.ServerAPICalls.creditBalanceURL, params, new WebApiRequest.APIRequestDataCallBack() {
            @Override
            public void onSuccess(JsonElement response) {

                if(response != null && response.isJsonObject()){

                    JsonObject responseObject = response.getAsJsonObject();

                    if(responseObject.has("balance")){
                        walletViewModel.getCredit().setValue(responseObject.get("balance").getAsInt());
                    }
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


    private void initializeAdapter() {

        RealmResults<CompanyPackage> companyPackages = walletViewModel.getAllCompanyPackagesFromDB();

        checkHasCompanyPackages(companyPackages != null && companyPackages.size() > 0);

        companyPackageAdapter = new CompanyPackageAdapter(activityReference, companyPackages, new DataRecyclerViewClickInterface<CompanyPackage>() {
            @Override
            public void onClick(CompanyPackage object, int position) {

                CompanyPackageFragment companyPackageFragment = new CompanyPackageFragment();
                companyPackageFragment.setCompanyPackageObject(object);
                activityReference.addSupportFragment(companyPackageFragment, AppConstant.TRANSITION_TYPES.SLIDE);

            }

            @Override
            public void onEditClick(CompanyPackage object, int position) {
//                Toast.makeText(activityReference, object.getPackage().getName(), Toast.LENGTH_SHORT).show();

            }
        });

        swipeRefreshCompanyPackage.setColorSchemeResources(R.color.appColorPrimary);
        swipeRefreshCompanyPackage.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //Company Package Sync
                SyncManager.getInstance().addEntityToQueue(EntityEnum.CompanyPackage);

                swipeRefreshCompanyPackage.setRefreshing(false);
            }
        });

        rvCompanyPackage.setLayoutManager(new LinearLayoutManager(activityReference));
        rvCompanyPackage.setAdapter(companyPackageAdapter);

    }

    private void checkHasCompanyPackages(boolean isData) {

        if(isData){
            rvCompanyPackage.setVisibility(View.VISIBLE);
            llNoCompanyPackageDataFound.setVisibility(View.GONE);

        }else{
            rvCompanyPackage.setVisibility(View.GONE);
            llNoCompanyPackageDataFound.setVisibility(View.VISIBLE);
        }

    }

}