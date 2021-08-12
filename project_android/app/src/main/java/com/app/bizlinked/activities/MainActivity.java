package com.app.bizlinked.activities;


import android.Manifest;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.app.bizlinked.BizLinkedApplicationClass;
import com.app.bizlinked.BuildConfig;
import com.app.bizlinked.R;
import com.app.bizlinked.activities.baseClass.BaseActivity;
import com.app.bizlinked.constant.AppConstant;
import com.app.bizlinked.customViews.TitleBar;
import com.app.bizlinked.db.AppDBHelper;
import com.app.bizlinked.fragments.CustomerSupplierListFragment;
import com.app.bizlinked.fragments.InvitationsFragment;
import com.app.bizlinked.fragments.OrderListFragment;
import com.app.bizlinked.fragments.PhoneNumberFragment;
import com.app.bizlinked.fragments.ProductCategoryListFragment;
import com.app.bizlinked.fragments.SettingFragment;
import com.app.bizlinked.fragments.WalletFragment;
import com.app.bizlinked.fragments.baseClass.BaseFragment;
import com.app.bizlinked.helpers.common.Utils;
import com.app.bizlinked.helpers.file.FileHelper;
import com.app.bizlinked.helpers.preference.PreferenceHelper;
import com.app.bizlinked.helpers.sync_helpers.ImageSyncUtility;
import com.app.bizlinked.helpers.sync_helpers.SyncManager;
import com.app.bizlinked.listener.custom.OnSyncCompleteInterafce;
import com.app.bizlinked.models.db.Link;
import com.app.bizlinked.models.db.Order;
import com.app.bizlinked.models.db.OrderStatus;
import com.app.bizlinked.models.db.SyncQueue;
import com.app.bizlinked.models.db.BusinessCategory;
import com.app.bizlinked.models.db.City;
import com.app.bizlinked.models.db.CompanyPackage;
import com.app.bizlinked.models.db.Image;
import com.app.bizlinked.models.db.Link;
import com.app.bizlinked.models.db.Order;
import com.app.bizlinked.models.db.OrderDetail;
import com.app.bizlinked.models.db.OrderStatus;
import com.app.bizlinked.models.db.Product;
import com.app.bizlinked.models.db.ProductCategory;
import com.app.bizlinked.models.db.ProductImage;
import com.app.bizlinked.models.db.Profile;
import com.app.bizlinked.models.db.SyncQueue;
import com.app.bizlinked.models.db.SyncedEntity;
import com.app.bizlinked.models.db.base_class.BaseEntity;
import com.app.bizlinked.models.enums.EntityEnum;
import com.app.bizlinked.models.enums.LinkRelationEnum;
import com.app.bizlinked.models.enums.NotificationTypeEnum;
import com.app.bizlinked.models.enums.OrderScreenStatusEnum;
import com.app.bizlinked.models.enums.ProductViewEnum;
import com.app.bizlinked.models.sync_response.LinkSyncResponse;
import com.app.bizlinked.models.sync_response.OrderStatusSyncResponse;
import com.app.bizlinked.models.sync_response.OrderSyncResponse;
import com.app.bizlinked.receivers.NetworkChangeReceiver;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import io.realm.RealmSchema;
import io.realm.internal.RealmObjectProxy;
import io.sentry.Sentry;

import static com.app.bizlinked.db.AppDBHelper.getRealmInstance;

public class MainActivity extends BaseActivity {


    @BindView(R.id.fragmentContainer)
    FrameLayout fragmentContainer;

    @BindView(R.id.mTitleBar)
    TitleBar mTitleBar;

    // Bottom Menu
    @BindView(R.id.llBottomMenu)
    LinearLayout llBottomMenu;

    @BindView(R.id.llLinks)
    LinearLayout llLinks;
    @BindView(R.id.llProducts)
    LinearLayout llProducts;
    @BindView(R.id.llOrders)
    LinearLayout llOrders;
    @BindView(R.id.llWallet)
    LinearLayout llWallet;
    @BindView(R.id.llSettings)
    LinearLayout llSettings;
    // Bottom Menu

    private static TextView tvCheckConnection;
    private static BroadcastReceiver mNetworkReceiver;


    @Override
    public int getMainLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public int getFragmentFrameLayoutId() {
        return R.id.fragmentContainer;
    }

    @Override
    protected void onViewReady() {

        //Sync Manager iniialize and Image Sync Utility st Activity Reference
        ImageSyncUtility.getInstance().setActivityReference(this);
        SyncManager.getInstance().setActivityReference(this);
        SyncManager.getInstance().init();


        //initialize if Network not connected
//            tvCheckConnection = findViewById(R.id.tvCheckConnection);
//        mNetworkReceiver = new NetworkChangeReceiver();
//        registerNetworkBroadcastForNetwork();


        //Function For getting token from firebase and send it to server
        getFirebaseToken();

        setAndBindTitleBar();

        //Empty the stack
        emptyBackStack();

        if (prefHelper.getLoginStatus()) {
            prefHelper.putUserTokenType(AppConstant.AUTH_TOKEN_TYPES.BASIC);
            //If User is not logged in
            addSupportFragment(new PhoneNumberFragment(), AppConstant.TRANSITION_TYPES.SLIDE);
        } else {

            //First Page Open
            CustomerSupplierListFragment customerSupplierListFragment = new CustomerSupplierListFragment();
            fragmentBackStackChangeListener(customerSupplierListFragment);



            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Check if Click on Notification
                    if (getIntent() != null && getIntent().getExtras() != null) {
                        Map<String, String> notificationData = parseDataIntoMap(getIntent().getExtras().getString(AppConstant.PUSH_CONFIG.PUSH_DATA_BODY));
                        processNotificationData(notificationData); // For Sync Calls
                        navigateToSpecificNotificationScreen(notificationData); // For UI Change
                    }

                }
            }, 500);

        }


    }


    public void getFirebaseToken() {

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(getClass().getName(), "getInstanceId failed", task.getException());
                            return;
                        }

                        if (task.getResult() != null) {
                            // Get new Instance ID token
                            String token = task.getResult().getToken();
                            Log.d("PUSH_TOKEN", token);
                            prefHelper.putDeviceToken(token);
                        }
                    }
                });
    }


    /***************************
     * Bottom Menu WOrk
     * ***************************/


    private void resetBottomSelectedBackgroundColor(LinearLayout llBottomParentView) {
        for (int index = 0; index < llBottomParentView.getChildCount(); index++) {
            llBottomParentView.getChildAt(index).setBackgroundColor(Color.TRANSPARENT);
        }
    }


    public void setBottomMenuFromSelectedOption(String selectedBottomMenu) {

        resetBottomSelectedBackgroundColor(llBottomMenu);

        switch (selectedBottomMenu) {
            case AppConstant.BOTTOM_MENU.LINKS:
                setBottomMenuVisibility(View.VISIBLE);
                setBottomSelectedBackgroundColor(llLinks);
                break;
            case AppConstant.BOTTOM_MENU.PRODUCTS:
                setBottomMenuVisibility(View.VISIBLE);
                setBottomSelectedBackgroundColor(llProducts);
                break;
            case AppConstant.BOTTOM_MENU.ORDERS:
                setBottomMenuVisibility(View.VISIBLE);
                setBottomSelectedBackgroundColor(llOrders);
                break;
            case AppConstant.BOTTOM_MENU.WALLET:
                setBottomMenuVisibility(View.VISIBLE);
                setBottomSelectedBackgroundColor(llWallet);
                break;
            case AppConstant.BOTTOM_MENU.SETTING:
                setBottomMenuVisibility(View.VISIBLE);
                setBottomSelectedBackgroundColor(llSettings);
                break;
        }
    }


    public void setBottomMenuVisibility(int visibility) {
        llBottomMenu.setVisibility(visibility);
    }

    private void setBottomSelectedBackgroundColor(LinearLayout llSelectedView) {

        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = getTheme();
        @ColorInt int color;
        theme.resolveAttribute(R.attr.colorPrimaryDark, typedValue, true);
        color = typedValue.data;

        llSelectedView.setBackgroundColor(color);
    }


    public void setOnBottomMenuClickListener(BaseFragment frag) {

        String fragName = frag.getClass().getName();
        if (fragName.contains(AppConstant.BOTTOM_MENU.LINKS)) {
            if (isFragmentPresent(frag.getClass().getName()) == null) {
                // Because of first fragment
                replaceSupportFragment(frag, AppConstant.TRANSITION_TYPES.SLIDE);
            }
            setBottomMenuFromSelectedOption(AppConstant.BOTTOM_MENU.LINKS);
        } else if (fragName.contains(AppConstant.BOTTOM_MENU.PRODUCTS)) {
            if (isFragmentPresent(frag.getClass().getName()) == null) {
                replaceSupportFragment(frag, AppConstant.TRANSITION_TYPES.SLIDE);
            }
            setBottomMenuFromSelectedOption(AppConstant.BOTTOM_MENU.PRODUCTS);

        } else if (fragName.contains(AppConstant.BOTTOM_MENU.ORDERS)) {
            if (isFragmentPresent(frag.getClass().getName()) == null) {
                replaceSupportFragment(frag, AppConstant.TRANSITION_TYPES.SLIDE);
            }
            setBottomMenuFromSelectedOption(AppConstant.BOTTOM_MENU.ORDERS);

        } else if (fragName.contains(AppConstant.BOTTOM_MENU.WALLET)) {
            if (isFragmentPresent(frag.getClass().getName()) == null) {
                replaceSupportFragment(frag, AppConstant.TRANSITION_TYPES.SLIDE);
            }
            setBottomMenuFromSelectedOption(AppConstant.BOTTOM_MENU.WALLET);

        } else if (fragName.contains(AppConstant.BOTTOM_MENU.COMPANY_PACKAGE)) {
            setBottomMenuVisibility(View.VISIBLE);
            setBottomSelectedBackgroundColor(llWallet);
        } else if (fragName.contains(AppConstant.BOTTOM_MENU.SETTING)) {
            if (isFragmentPresent(frag.getClass().getName()) == null) {
                replaceSupportFragment(frag, AppConstant.TRANSITION_TYPES.SLIDE);
            }
            setBottomMenuFromSelectedOption(AppConstant.BOTTOM_MENU.SETTING);
        }
//        else if (
//                fragName.contains(AppConstant.BOTTOM_MENU.ADD_OR_EDIT_CATEGORY_FRAGMENT)
//                        ||
//                        fragName.contains(AppConstant.BOTTOM_MENU.ADD_OR_EDIT_PRODUCT_FRAGMENT)) {
//
//            setBottomMenuFromSelectedOption(AppConstant.BOTTOM_MENU.PRODUCTS);

//        } else if (fragName.contains(AppConstant.BOTTOM_MENU.COMPANY_DETAIL)) {
//
//            setBottomMenuFromSelectedOption(AppConstant.BOTTOM_MENU.SETTING);

//        }
//        else if (fragName.contains(AppConstant.BOTTOM_MENU.INVITATIONS)) {
//            setBottomMenuFromSelectedOption(AppConstant.BOTTOM_MENU.LINKS);
//        }
//        else
//            if (fragName.contains(AppConstant.BOTTOM_MENU.PROFILE)) {
//
//            // Edit Profile Page Case
//            if(prefHelper != null && !Utils.isEmptyOrNull(prefHelper.getProfileId())){
//                setBottomMenuFromSelectedOption(AppConstant.BOTTOM_MENU.SETTING);
//            }
//        }


    }


    @Override
    public void fragmentBackStackChangeListener(BaseFragment fragment) {
        resetBottomSelectedBackgroundColor(llBottomMenu);
        setBottomMenuVisibility(View.GONE);
        setOnBottomMenuClickListener(fragment);
    }


    @OnClick({R.id.llLinks, R.id.llProducts, R.id.llOrders, R.id.llWallet, R.id.llSettings})
    public void onViewClicked(View view) {

        emptyBackStack();
        resetBottomSelectedBackgroundColor(llBottomMenu);
        setBottomMenuVisibility(View.GONE);

        switch (view.getId()) {

            case R.id.llLinks:
                setOnBottomMenuClickListener(new CustomerSupplierListFragment());
                break;
            case R.id.llProducts:
                ProductCategoryListFragment productCategoryListFragment = new ProductCategoryListFragment();
                productCategoryListFragment.setProductViewType(ProductViewEnum.LOGGED_IN_COMPANY_PROFILE);
                setOnBottomMenuClickListener(productCategoryListFragment);
                break;
            case R.id.llOrders:

                OrderListFragment orderListFragment = new OrderListFragment();
                orderListFragment.setShowSearchFilter(true);
                orderListFragment.setScreenStatus(OrderScreenStatusEnum.RECEIVED);
                setOnBottomMenuClickListener(orderListFragment);
                break;
            case R.id.llWallet:
                setOnBottomMenuClickListener(new WalletFragment());
                break;

            case R.id.llSettings:
                setOnBottomMenuClickListener(new SettingFragment());
                break;
        }
    }


    public void locationPermission() {
        TedPermission.with(MainActivity.this)
                .setPermissions(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
                .setPermissionListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
//                        getGoogleLocation();
                    }

                    @Override
                    public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                        //canceldUserLocation();
                        Utils.showSnackBar(context, getMainView(),
                                context.getString(R.string.permission_denied), ContextCompat.getColor(context, R.color.red));
                    }
                }).check();
    }



    /***************************
     * Bottom Menu WOrk
     * ***************************/


//    private void getTheTokenFromFirebaserForPush() {
//
//        FirebaseInstanceId.getInstance().getInstanceId()
//                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
//                        if (!task.isSuccessful()) {
//                            Log.w(getClass().getName(), "getInstanceId failed", task.getException());
//                            return;
//                        }
//
//                        try {
//                            // Get new Instance ID token
//                            String token = task.getResult().getToken();
//
//                            Log.d("PUSH_TOKEN", token);
//                            sendPushRegistrationToServer(token);
//                        }catch (Exception e){
//                            e.printStackTrace();
//                        }
//                    }
//                });
//    }
//
//    private void sendPushRegistrationToServer(String token) {
//
//        HashMap<String, Object> deviceInfo = new HashMap<>();
//
//        deviceInfo.putAll(DeviceInfo.getHardwareInfo(context));
//        deviceInfo.put("DEVICE_ID", DeviceInfo.getDeviceID(context));
//
//        HashMap<String, Object> deviceInfoParams = new HashMap<>(deviceInfo.size());
//        for (Map.Entry<String, Object> entry : deviceInfo.entrySet()) {
//            deviceInfoParams.put(entry.getKey().toLowerCase(), entry.getValue());
//        }
//
//
//        //Custom Param
//        deviceInfoParams.put("registration_id", token);
//        deviceInfoParams.put("os", "android");
//        deviceInfoParams.put("user", prefHelper.getUser().getId());
//        deviceInfoParams.put("name", prefHelper.getUser().getFull_name());
//
//
//        WebApiRequest.getInstance(MainActivity.this, false).sendPushTokenToServer(deviceInfoParams, new WebApiRequest.APIRequestDataCallBack() {
//            @Override
//            public void onSuccess(JsonElement response) {
//                if(prefHelper != null){
//                    prefHelper.putDeviceToken(token);
//                }
//            }
//
//            @Override
//            public void onError(JsonElement response) {
//
//            }
//
//            @Override
//            public void onNoNetwork() {
//
//            }
//        });
//    }
//
    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver((mMessageReceiver),
                new IntentFilter(AppConstant.PUSH_CONFIG.PUSH_SERVICE_FILTER)
        );
    }

    @Override
    protected void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onStop();
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent != null && intent.getExtras() != null) {

                //For New Token Receive
                if (!Utils.isEmptyOrNull(intent.getExtras().getString(AppConstant.PUSH_CONFIG.PUSH_TOKEN_KEY))) {
                    if (prefHelper != null)
                        prefHelper.putDeviceToken(intent.getExtras().getString(AppConstant.PUSH_CONFIG.PUSH_TOKEN_KEY));
                }

                //If user logged in and has token
                if(prefHelper.getLoginStatus() && !Utils.isEmptyOrNull(prefHelper.getUserToken())){
                    if (!Utils.isEmptyOrNull(intent.getExtras().getString(AppConstant.PUSH_CONFIG.PUSH_DATA_BODY))) {
                        Map<String, String> notificationData = parseDataIntoMap(intent.getExtras().getString(AppConstant.PUSH_CONFIG.PUSH_DATA_BODY));
                        processNotificationData(notificationData); // Push Comes in Foreground sync calls
                    }
                }

            }
        }
    };

    private void processNotificationData(Map<String, String> notificationData) {

        if (notificationData != null) {

            if (notificationData.containsKey("type") && !Utils.isEmptyOrNull(notificationData.get("type"))) {

                String type = notificationData.get("type");

                if (type.equalsIgnoreCase(NotificationTypeEnum.LINK_REQUEST_ADD.getValue())
                        || type.equalsIgnoreCase(NotificationTypeEnum.LINK_REQUEST_ACCEPTED.getValue())
                        || type.equalsIgnoreCase(NotificationTypeEnum.LINK_REQUEST_SYNC.getValue())) {

                    //Link Sync
                    SyncQueue obj = new SyncQueue();
                    obj.setEntityName(EntityEnum.Link.getEntityName());
                    obj.setPriority(EntityEnum.Link.getPriority());

                    SyncManager.getInstance().syncEntity(obj, new OnSyncCompleteInterafce() {
                        @Override
                        public void onSyncComplete(String queue) {
                            Log.d("PUSH_SYNC_COMPLETE", "Link Sync Successful from notification");
                            SyncManager.getInstance().addEntityToQueue(EntityEnum.ImageSyncUtility);
                        }
                    });

                } else if (type.equalsIgnoreCase(NotificationTypeEnum.ORDER_SUBMITTED.getValue())
                        || type.equalsIgnoreCase(NotificationTypeEnum.ORDER_SYNC.getValue())) {

                    //Order Sync
                    SyncQueue obj = new SyncQueue();
                    obj.setEntityName(EntityEnum.Order.getEntityName());
                    obj.setPriority(EntityEnum.Order.getPriority());

                    SyncManager.getInstance().syncEntity(obj, new OnSyncCompleteInterafce() {
                        @Override
                        public void onSyncComplete(String queue) {

                            Log.d("PUSH_SYNC_COMPLETE", "Order Sync Successful from notification");

                            //OrderStatus Sync
                            SyncQueue obj = new SyncQueue();
                            obj.setEntityName(EntityEnum.OrderStatus.getEntityName());
                            obj.setPriority(EntityEnum.OrderStatus.getPriority());

                            SyncManager.getInstance().syncEntity(obj, new OnSyncCompleteInterafce() {
                                @Override
                                public void onSyncComplete(String queue) {
                                    Log.d("PUSH_SYNC_COMPLETE", "OrderStatus Sync Successful from notification");
                                    SyncManager.getInstance().addEntityToQueue(EntityEnum.ImageSyncUtility);
                                }
                            });

                        }
                    });

                } else if (type.equalsIgnoreCase(NotificationTypeEnum.ORDER_APPROVED_REJECTED.getValue())
                        || type.equalsIgnoreCase(NotificationTypeEnum.ORDER_DELIVERED.getValue())
                        || type.equalsIgnoreCase(NotificationTypeEnum.ORDER_RECEIVED.getValue())) {

                    //OrderStatus Sync
                    SyncQueue obj = new SyncQueue();
                    obj.setEntityName(EntityEnum.OrderStatus.getEntityName());
                    obj.setPriority(EntityEnum.OrderStatus.getPriority());

                    SyncManager.getInstance().syncEntity(obj, new OnSyncCompleteInterafce() {
                        @Override
                        public void onSyncComplete(String queue) {
                            Log.d("PUSH_SYNC_COMPLETE", "OrderStatus Sync Successful from notification");
                            SyncManager.getInstance().addEntityToQueue(EntityEnum.ImageSyncUtility);
                        }
                    });

                }

            }
        }

    }


    private void navigateToSpecificNotificationScreen(Map<String, String> notificationData) {

        if (notificationData != null) {

            if (notificationData.containsKey("type") && !Utils.isEmptyOrNull(notificationData.get("type"))) {

                String type = notificationData.get("type");

                if (type.equalsIgnoreCase(NotificationTypeEnum.LINK_REQUEST_ADD.getValue())) {

                    if (notificationData.containsKey("linkType")) {

                        String linkType = notificationData.get("linkType");

                        //Open Invitation For Customer
                        if (linkType.equalsIgnoreCase(LinkRelationEnum.CUSTOMER.getValue())) {

                            InvitationsFragment customerInvitationsFragment = new InvitationsFragment();
                            customerInvitationsFragment.setType(LinkRelationEnum.CUSTOMER);
                            customerInvitationsFragment.setInvitationTypeTitleText(getString(R.string.customer_invitations));
                            addSupportFragment(customerInvitationsFragment, AppConstant.TRANSITION_TYPES.SLIDE);

                        } else if (linkType.equalsIgnoreCase(LinkRelationEnum.SUPPLIER.getValue())) {

                            //Open Invitation For Supplier
                            InvitationsFragment supplierInvitationsFragment = new InvitationsFragment();
                            supplierInvitationsFragment.setType(LinkRelationEnum.SUPPLIER);
                            supplierInvitationsFragment.setInvitationTypeTitleText(getString(R.string.supplier_invitations));
                            addSupportFragment(supplierInvitationsFragment, AppConstant.TRANSITION_TYPES.SLIDE);
                        }

                    }
                } else if (type.equalsIgnoreCase(NotificationTypeEnum.ORDER_SUBMITTED.getValue())
                        || type.equalsIgnoreCase(NotificationTypeEnum.ORDER_RECEIVED.getValue())) {

                    resetBottomSelectedBackgroundColor(llBottomMenu);
                    setBottomMenuVisibility(View.GONE);

                    OrderListFragment orderListFragment = new OrderListFragment();
                    orderListFragment.setShowSearchFilter(true);
                    orderListFragment.setScreenStatus(OrderScreenStatusEnum.RECEIVED);
                    orderListFragment.setSelectedTab(OrderScreenStatusEnum.RECEIVED);
                    setOnBottomMenuClickListener(orderListFragment);

                } else if (type.equalsIgnoreCase(NotificationTypeEnum.ORDER_APPROVED_REJECTED.getValue())
                        || type.equalsIgnoreCase(NotificationTypeEnum.ORDER_DELIVERED.getValue())) {

                    resetBottomSelectedBackgroundColor(llBottomMenu);
                    setBottomMenuVisibility(View.GONE);

                    OrderListFragment orderListFragment = new OrderListFragment();
                    orderListFragment.setShowSearchFilter(true);
                    orderListFragment.setScreenStatus(OrderScreenStatusEnum.PLACED);
                    orderListFragment.setSelectedTab(OrderScreenStatusEnum.PLACED);
                    setOnBottomMenuClickListener(orderListFragment);

                }

            }
        }
    }

    private Map<String, String> parseDataIntoMap(String mapString) {

        mapString = mapString.substring(1, mapString.length() - 1);           //remove curly brackets
        String[] keyValuePairs = mapString.split(",");              //split the string to creat key-value pairs
        Map<String, String> map = new HashMap<>();

        for (String pair : keyValuePairs) {                        //iterate over the pairs
            String[] entry = pair.split("=");                   //split the pairs to get key and value
            map.put(entry[0].trim(), entry[1].trim());          //add them to the hashmap and trim whitespaces
        }

        return map;
    }


    public static void networkStatusText(Context context, boolean flag) {

        try {
//            if(tvCheckConnection != null){
//                if(flag){
//                    tvCheckConnection.setText(context.getString(R.string.network_available));
//                    tvCheckConnection.setBackgroundColor(ContextCompat.getColor(context, R.color.green));
//                    tvCheckConnection.setTextColor(Color.WHITE);
//
//                    Handler handler = new Handler();
//                    Runnable delayrunnable = new Runnable() {
//                        @Override
//                        public void run() {
//                            tvCheckConnection.setVisibility(View.GONE);
//                        }
//                    };
//                    handler.postDelayed(delayrunnable, 3000);
//                }else {
//                    tvCheckConnection.setVisibility(View.VISIBLE);
//                    tvCheckConnection.setText(context.getString(R.string.no_network_available));
//                    tvCheckConnection.setBackgroundColor(Color.RED);
//                    tvCheckConnection.setTextColor(Color.WHITE);
//                }
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void registerNetworkBroadcastForNetwork() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
    }

    protected void unregisterNetworkChanges() {
        try {
            if (mNetworkReceiver != null)
                unregisterReceiver(mNetworkReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }



    @Override
    protected void onDestroy() {
        //unregisterNetworkChanges();
        if (getRealmInstance() != null)
            getRealmInstance().close();
        super.onDestroy();
    }


    private void setAndBindTitleBar() {
        mTitleBar.setVisibility(View.VISIBLE);
    }

    public TitleBar getTitleBar() {
        return mTitleBar;
    }

    public void afterLogoutTask() {

        AppDBHelper.getRealmInstance().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                AppDBHelper.getRealmInstance().removeAllChangeListeners();

                AppDBHelper.getRealmInstance().delete(SyncQueue.class);
                AppDBHelper.getRealmInstance().delete(SyncedEntity.class);
                AppDBHelper.getRealmInstance().delete(Profile.class);
                AppDBHelper.getRealmInstance().delete(ProductImage.class);
                AppDBHelper.getRealmInstance().delete(ProductCategory.class);
                AppDBHelper.getRealmInstance().delete(Product.class);
                AppDBHelper.getRealmInstance().delete(com.app.bizlinked.models.db.Package.class);
                AppDBHelper.getRealmInstance().delete(OrderStatus.class);
                AppDBHelper.getRealmInstance().delete(OrderDetail.class);
                AppDBHelper.getRealmInstance().delete(Order.class);
                AppDBHelper.getRealmInstance().delete(Link.class);
                AppDBHelper.getRealmInstance().delete(Image.class);
                AppDBHelper.getRealmInstance().delete(CompanyPackage.class);
                AppDBHelper.getRealmInstance().delete(City.class);
                AppDBHelper.getRealmInstance().delete(BusinessCategory.class);
                AppDBHelper.getRealmInstance().delete(BaseEntity.class);

                AppDBHelper.getRealmInstance().deleteAll();

                //Cache remove
                FileHelper.deleteCache(context);
            }
        });


        prefHelper.getSharedPreferences().edit().clear().apply();


        SyncManager.getInstance().syncQueue = null;
        SyncManager.getInstance().setFirstTime(true);

        emptyBackStack();
        prefHelper.putUserTokenType(AppConstant.AUTH_TOKEN_TYPES.BASIC);
        prefHelper.removeLoginPreference();


        changeActivity(MainActivity.class, true);

    }


//    public void showTitleBarMenu(View v, boolean isGenerateCodeOptionShow, boolean isEmergencyContactShow){
//
//        PopupMenu popup = new PopupMenu(this, v);
//        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                switch (item.getItemId()) {
//                    case R.id.menu_emergency:
//                        addSupportFragment(new EmergencyContact1Fragment(), AppConstant.TRANSITION_TYPES.SLIDE);
//                        return true;
//                    case R.id.menu_generate_code:
//
//                        SaffonaAppDialogs saffonaAppDialogs = new SaffonaAppDialogs();
//                        if(!isPinGenerated()){
//                            saffonaAppDialogs.showGenerateCodeDialog(MainActivity.this, MainActivity.this);
//                        }else if(!isPinVerified()){
//                            saffonaAppDialogs.showVerifyCodeDialog(MainActivity.this, MainActivity.this);
//                        }else{
//                            saffonaAppDialogs.showPinResetOrRegenerateCodeDialog(MainActivity.this, MainActivity.this);
//                        }
//                        return true;
//
//                    case R.id.menu_setting:
//                        addSupportFragment(new SettingFragment(), AppConstant.TRANSITION_TYPES.SLIDE);
//                        return true;
//
//                    case R.id.menu_profile:
//                        addSupportFragment(new EditProfileFragment(), AppConstant.TRANSITION_TYPES.SLIDE);
//                        return true;
//                    case R.id.menu_logout:
//                        logoutDialog();
//                        return true;
//                }
//                return false;
//            }
//        });
//        popup.inflate(R.menu.titlebar_menu);
//        popup.getMenu().findItem(R.id.menu_generate_code).setVisible(isGenerateCodeOptionShow);
//        popup.getMenu().findItem(R.id.menu_emergency).setVisible(isEmergencyContactShow);
//
//        // TODO: // For Beta Release
//        popup.getMenu().findItem(R.id.menu_setting).setVisible(false);
//        popup.getMenu().findItem(R.id.menu_emergency).setVisible(false);
//        popup.getMenu().findItem(R.id.menu_generate_code).setVisible(false);
//
//
//
//        if(!isPinGenerated()){
//            popup.getMenu().findItem(R.id.menu_generate_code).setTitle(getString(R.string.generate_code_text));
//        }else if(!isPinVerified()){
//            popup.getMenu().findItem(R.id.menu_generate_code).setTitle(getString(R.string.verify_code_text));
//        }else{
//            popup.getMenu().findItem(R.id.menu_generate_code).setTitle(getString(R.string.regenerate_reset_code_text));
//        }
//
//        popup.show();
//
//    }


//    public void logoutDialog() {
//
//
//        SaffonaAppDialogs saffonaAppDialogs = new SaffonaAppDialogs();
//        saffonaAppDialogs.showLogoutDialog(MainActivity.this, new LogoutInterface() {
//            @Override
//            public void logoutCallBack() {
//                logoutFromDeviceNavToLogin();
//
//            }
//        });
//
////        AlertDialog.Builder builder;
////        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
////            builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
////        } else {
////            builder = new AlertDialog.Builder(context);
////        }
////        builder.setTitle(context.getString(R.string.alert))
////                .setMessage(context.getString(R.string.are_you_sure))
////                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
////                    public void onClick(DialogInterface dialog, int which) {
////                      logoutFromDeviceNavToLogin();
////                    }
////                })
////                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
////                    public void onClick(DialogInterface dialog, int which) {
////                        // do nothing
////                        dialog.dismiss();
////                    }
////                }).show();
//    }
//
//    private void logoutFromDeviceNavToLogin() {
//        emptyBackStack();
//        addSupportFragment(new WelcomeFragment(), AppConstant.TRANSITION_TYPES.SLIDE);
//        prefHelper.removeLoginPreference();
//
//    }
//
//
//    public void getUserDetailFromServer(final UserDataListener userDataListener) {
//
//
//        HashMap<String, Object> params = new HashMap<>();
////        userInfoMap.put("email", etUserName.getText().toString());
////        userInfoMap.put("password", etPassword.getText().toString());
//        //userInfoMap.put("device_type", AppConstant.ANDROID);
//        //userInfoMap.put("device_token", preferenceHelper.getDeviceToken());
//
//        WebApiRequest.getInstance(this, true).getUserDetail(params ,new WebApiRequest.APIRequestDataCallBack() {
//            @Override
//            public void onSuccess(JsonElement response) {
//
//                if(response != null && response.isJsonObject()){
//
//                    JsonObject jsonObject = response.getAsJsonObject();
//                    //InComplete Profile View Visibility
//                    Gson gson = new Gson();
//
//                    UserModal userModal = gson.fromJson(jsonObject, new TypeToken<UserModal>() {}.getType());
//                    prefHelper.putUser(userModal);
//                    if(userDataListener != null)
//                        userDataListener.userDataCallBack(userModal);
//
//                }
//
//            }
//
//            @Override
//            public void onError(JsonElement response) {
//            }
//
//            @Override
//            public void onNoNetwork() {
//
//            }
//        });
//
//    }
}
