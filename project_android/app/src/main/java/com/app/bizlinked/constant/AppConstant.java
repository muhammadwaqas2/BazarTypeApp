package com.app.bizlinked.constant;



import android.content.Intent;

import com.app.bizlinked.BuildConfig;

import java.util.LinkedHashMap;

public class AppConstant {

    //Image & Doc Request Codes
    public static final int SELECT_IMAGE_COUNT = 1;
    public static final int SELECT_MAX_FILE_COUNT = 10;
    public static final int SELECT_MAX_DOC_FILE_COUNT = 10;

    public static int MIN_TIME_INTERVAL_FOR_SPLASH = 1000; // in millis
    public static String ANDROID = "android";



    public class TRANSITION_TYPES {
        public static final int NONE = 2000;
        public static final int FADE = 20001;
        public static final int SLIDE = 20002;
    }

    public class VALIDATION_RULES {
        public static final int PASSWORD_MIN_LENGTH = 8;
        public static final int USER_NAME_MIN_LENGTH = 5;
        public static final int NAME_MIN_LENGTH = 3;
        public static final int PHONE_MIN_LENGTH = 10;
        public static final int CNIC_MIN_LENGTH = 13;
    }

    public class TOAST_TYPES {
        public static final int INFO = 1101;
        public static final int SUCCESS = 1102;
        public static final int ERROR = 1103;

    }

    public class CONFIGURATION{
        public static final int PAGE_SIZE = 20;
    }

    public class PUSH_CONFIG {
        public static final String PUSH_TOKEN_KEY = "PUSH_TOKEN";
        public static final String PUSH_DATA_BODY = "PUSH_DATA_BODY";
        public static final String PUSH_SERVICE_FILTER = "PUSH_SERVICE_FILTER";
    }


    public class ServerAPICalls {


        public static final String BIZLINKED_APP_URL = BuildConfig.APP_URL;
        public static final String BIZLINKED_AUTH_URL = BuildConfig.AUTH_URL;


        //For API Calls
        public static final String GET_ACCESS_TOKEN = BIZLINKED_AUTH_URL;
        public static final String VERIFY_OTP = BIZLINKED_AUTH_URL;
        public static final String GENERATE_OTP = BIZLINKED_APP_URL + "/auth/generate/otp";
        public static final String GET_COMPANY_PROFILE = BIZLINKED_APP_URL + "/company/profile";
        public static final String UPLOAD_PROFILE = BIZLINKED_APP_URL + "/company/register";


        public static final String imageDownloadURL = BIZLINKED_APP_URL + "/image/download/";
        public static final String imageUploadURL = BIZLINKED_APP_URL + "/image/upload/";
        public static final String registerURL = BIZLINKED_APP_URL + "/company/register";
        public static final String linksSearchURL = BIZLINKED_APP_URL + "/company/search";
        public static final String productsSearchURL = BIZLINKED_APP_URL + "/product/search";
        public static final String productCategoryListURL = BIZLINKED_APP_URL + "/product-category/list";
        public static final String productListURL = BIZLINKED_APP_URL + "/product/list";
        public static final String creditBalanceURL = BIZLINKED_APP_URL + "/order-credit/balance";
        public static final String purchasePackageURL = BIZLINKED_APP_URL + "/package-transaction/purchase";
        public static final String orderApproveURL = BIZLINKED_APP_URL + "/order/approve";
        public static final String logoutURL = BIZLINKED_APP_URL + "/auth/logout";




        //sync URLs
        public static final String productSyncURL = BIZLINKED_APP_URL + "/product/sync/";
        public static final String categorySyncURL = BIZLINKED_APP_URL + "/product-category/sync/";
        public static final String productImageSyncURL = BIZLINKED_APP_URL + "/product-image/sync/";
        public static final String imageSyncURL = BIZLINKED_APP_URL + "/image/sync/";
        public static final String businessCategorySyncURL = BIZLINKED_APP_URL + "/business-category/sync/";
        public static final String citySyncURL = BIZLINKED_APP_URL + "/city/sync/";
        public static final String profileSyncURL = BIZLINKED_APP_URL + "/company/sync/";
        public static final String linkSyncURL = BIZLINKED_APP_URL + "/link/sync/";
        public static final String companyPackageSyncURL = BIZLINKED_APP_URL + "/company-package/sync/";
        public static final String orderSyncURL = BIZLINKED_APP_URL + "/order/sync/";
        public static final String orderStatusSyncURL = BIZLINKED_APP_URL + "/order-status/sync/";


    }


    public class ENVIRONMENT {
        public static final String STAGING = "Staging";
        public static final String DEBUG = "Debug";
        public static final String PRODUCTION = "Production";
    }

    public class AUTH_TOKEN_TYPES {
        public static final String BASIC = "Basic";
        public static final String BEARER = "Bearer";
    }


    public class CATEGORY_TYPE {
        public static final String ADD = "add";
        public static final String EDIT = "edit";
    }

    public class BOTTOM_MENU {
        public static final String LINKS = "CustomerSupplierListFragment";
        public static final String PRODUCTS = "ProductCategoryListFragment";
        public static final String ADD_OR_EDIT_CATEGORY_FRAGMENT = "AddOrEditCategoryFragment";
        public static final String ADD_OR_EDIT_PRODUCT_FRAGMENT = "AddOrEditProductFragment";
        public static final String ORDERS = "OrderListFragment";
        public static final String WALLET = "WalletFragment";
        public static final String SETTING = "SettingFragment";
        public static final String COMPANY_DETAIL = "CompanyDetailFragment";
        public static final String PROFILE = "ProfileFragment";
        public static final String COMPANY_PACKAGE = "CompanyPackageFragment";
        public static final String INVITATIONS = "InvitationsFragment";
    }

    public class RECYCLER_VIEW {
        public static final String LAYOUT_TYPE_LINEAR = "linear";
        public static final String LAYOUT_TYPE_GRID = "grid";
    }
}
