package com.app.bizlinked.webhelpers;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.v4.content.ContextCompat;
import android.webkit.MimeTypeMap;

import com.app.bizlinked.R;
import com.app.bizlinked.activities.baseClass.BaseActivity;
import com.app.bizlinked.helpers.common.KeyboardHelper;
import com.app.bizlinked.helpers.common.NetworkUtils;
import com.app.bizlinked.helpers.common.Utils;
import com.app.bizlinked.webServices.WebService;
import com.app.bizlinked.webServices.WebServiceFactory;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WebApiRequest {

    private static WebService apiService;
    private static Activity currentActivity;
    private static WebApiRequest ourInstance = new WebApiRequest();

    public static WebApiRequest getInstance(Activity activity, boolean isShow) {
        apiService = WebServiceFactory.getInstance(activity);
        currentActivity = activity;
        if (isShow) {
            if (currentActivity instanceof BaseActivity) {
                ((BaseActivity) currentActivity).onLoadingStarted();
                KeyboardHelper.hideSoftKeyboard((currentActivity));
            }
        }
        return ourInstance;
    }

    private WebApiRequest() {}




    public interface APIRequestDataCallBack {
        void onSuccess(JsonElement response);
        void onError(String response);
        void onNoNetwork();
    }

    public interface ImageRequestDataCallBack {
        void onSuccess(ResponseBody response);
        void onError(String response);
        void onNoNetwork();
    }


    private void showErrorMessage(String parseErrorMessage) {
        Utils.showSnackBar(currentActivity, currentActivity.getCurrentFocus(),
                parseErrorMessage, ContextCompat.getColor(currentActivity, R.color.red));
    }

    private String parseErrorMessage(JSONObject jsonObject) {

        if(jsonObject != null){
            if(jsonObject.has("message")){
                try {
                    return jsonObject.getString("message");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else if(jsonObject.has("error_description")){
                try {
                    return jsonObject.getString("error_description");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
        return currentActivity.getString(R.string.something_wrong);
    }


    public void getAccessTokenFromServer(HashMap<String, Object> params, final APIRequestDataCallBack apiRequestDataCallBack) {

        if (!NetworkUtils.isNetworkAvailable(currentActivity) || !NetworkUtils.isOnline(currentActivity)) {

            if (currentActivity instanceof BaseActivity)
                ((BaseActivity) currentActivity).onLoadingFinished();

            showErrorMessage(currentActivity.getResources().getString(R.string.no_network_available));
            apiRequestDataCallBack.onNoNetwork();
            return;
        }

        Call<JsonElement> call = apiService.getAccessToken(params);
        call.enqueue(new Callback<JsonElement>() {

            @Override
            public void onResponse(@NotNull Call<JsonElement> pCall, @NotNull Response<JsonElement> response) {

                if (currentActivity instanceof BaseActivity) {
                    ((BaseActivity) currentActivity).onLoadingFinished();
                }

                if (response != null && response.body() != null) {
                    apiRequestDataCallBack.onSuccess(response.body());
                } else {
                    try {
                        JSONObject jsonObject = new JSONObject(response.errorBody().string() + "");
                        apiRequestDataCallBack.onError(response.errorBody().string());
                        showErrorMessage(parseErrorMessage(jsonObject));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(final Call<JsonElement> pCall, Throwable throwable) {
                if (currentActivity instanceof BaseActivity)
                    ((BaseActivity) currentActivity).onLoadingFinished();
                throwable.printStackTrace();
                apiRequestDataCallBack.onError(null);
            }
        });

    }



    public void generateOTPFromServer(HashMap<String, Object> params, final APIRequestDataCallBack apiRequestDataCallBack) {

        if (!NetworkUtils.isNetworkAvailable(currentActivity) || !NetworkUtils.isOnline(currentActivity)) {

            if (currentActivity instanceof BaseActivity)
                ((BaseActivity) currentActivity).onLoadingFinished();

            showErrorMessage(currentActivity.getResources().getString(R.string.no_network_available));
            apiRequestDataCallBack.onNoNetwork();
            return;
        }

        Call<JsonElement> call = apiService.generateOTP(params);
        call.enqueue(new Callback<JsonElement>() {

            @Override
            public void onResponse(@NotNull Call<JsonElement> pCall, @NotNull Response<JsonElement> response) {

                if (currentActivity instanceof BaseActivity) {
                    ((BaseActivity) currentActivity).onLoadingFinished();
                }

                if (response != null && response.body() != null) {
                    apiRequestDataCallBack.onSuccess(response.body());
                } else {
                    try {
                        JSONObject jsonObject = new JSONObject(response.errorBody().string() + "");
                        apiRequestDataCallBack.onError(response.errorBody().string());
                        showErrorMessage(parseErrorMessage(jsonObject));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(final Call<JsonElement> pCall, Throwable throwable) {
                if (currentActivity instanceof BaseActivity)
                    ((BaseActivity) currentActivity).onLoadingFinished();
                throwable.printStackTrace();
                apiRequestDataCallBack.onError(null);
            }
        });

    }


    public void verifyOTPFromServer(HashMap<String, Object> params, final APIRequestDataCallBack apiRequestDataCallBack) {

        if (!NetworkUtils.isNetworkAvailable(currentActivity) || !NetworkUtils.isOnline(currentActivity)) {

            if (currentActivity instanceof BaseActivity)
                ((BaseActivity) currentActivity).onLoadingFinished();

            showErrorMessage(currentActivity.getResources().getString(R.string.no_network_available));
            apiRequestDataCallBack.onNoNetwork();
            return;
        }

        Call<JsonElement> call = apiService.verifyOTP(params);
        call.enqueue(new Callback<JsonElement>() {

            @Override
            public void onResponse(@NotNull Call<JsonElement> pCall, @NotNull Response<JsonElement> response) {

                if (currentActivity instanceof BaseActivity) {
                    ((BaseActivity) currentActivity).onLoadingFinished();
                }

                if (response != null && response.body() != null) {
                    apiRequestDataCallBack.onSuccess(response.body());
                } else {
                    try {
                        JSONObject jsonObject = new JSONObject(response.errorBody().string() + "");
                        apiRequestDataCallBack.onError(response.errorBody().string());
                        showErrorMessage(parseErrorMessage(jsonObject));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(final Call<JsonElement> pCall, Throwable throwable) {
                if (currentActivity instanceof BaseActivity)
                    ((BaseActivity) currentActivity).onLoadingFinished();
                throwable.printStackTrace();
                apiRequestDataCallBack.onError(null);
            }
        });

    }

    public void uploadProfileToServer(HashMap<String, Object> params, final APIRequestDataCallBack apiRequestDataCallBack) {

        if (!NetworkUtils.isNetworkAvailable(currentActivity) || !NetworkUtils.isOnline(currentActivity)) {

            if (currentActivity instanceof BaseActivity)
                ((BaseActivity) currentActivity).onLoadingFinished();

            showErrorMessage(currentActivity.getResources().getString(R.string.no_network_available));
            apiRequestDataCallBack.onNoNetwork();
            return;
        }

        Call<JsonElement> call = apiService.uploadProfile(params);
        call.enqueue(new Callback<JsonElement>() {

            @Override
            public void onResponse(@NotNull Call<JsonElement> pCall, @NotNull Response<JsonElement> response) {

                if (currentActivity instanceof BaseActivity) {
                    ((BaseActivity) currentActivity).onLoadingFinished();
                }

                if (response != null && response.body() != null) {
                    apiRequestDataCallBack.onSuccess(response.body());
                } else {
                    try {
                        JSONObject jsonObject = new JSONObject(response.errorBody().string() + "");
                        apiRequestDataCallBack.onError(response.errorBody().string());
                        showErrorMessage(parseErrorMessage(jsonObject));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(final Call<JsonElement> pCall, Throwable throwable) {
                if (currentActivity instanceof BaseActivity)
                    ((BaseActivity) currentActivity).onLoadingFinished();
                throwable.printStackTrace();
                apiRequestDataCallBack.onError(null);
            }
        });

    }


    public void getCompanyProfileFromServer(HashMap<String, Object> params, final APIRequestDataCallBack apiRequestDataCallBack) {

        if (!NetworkUtils.isNetworkAvailable(currentActivity) || !NetworkUtils.isOnline(currentActivity)) {

            if (currentActivity instanceof BaseActivity)
                ((BaseActivity) currentActivity).onLoadingFinished();

            showErrorMessage(currentActivity.getResources().getString(R.string.no_network_available));
            apiRequestDataCallBack.onNoNetwork();
            return;
        }

        Call<JsonElement> call = apiService.getCompanyProfile(params);
        call.enqueue(new Callback<JsonElement>() {

            @Override
            public void onResponse(@NotNull Call<JsonElement> pCall, @NotNull Response<JsonElement> response) {

                if (currentActivity instanceof BaseActivity) {
                    ((BaseActivity) currentActivity).onLoadingFinished();
                }

                if (response != null && response.body() != null) {
                    apiRequestDataCallBack.onSuccess(response.body());
                } else {
                    try {

                        JSONObject jsonObject = new JSONObject(response.errorBody().string() + "");

                        if(jsonObject.has("status") && (jsonObject.getInt("status") == 404)){
                            apiRequestDataCallBack.onSuccess(null);
                        }else {
                            apiRequestDataCallBack.onError(response.errorBody().string());
                            showErrorMessage(parseErrorMessage(jsonObject));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        apiRequestDataCallBack.onError(null);
                    } catch (IOException e) {
                        e.printStackTrace();
                        apiRequestDataCallBack.onError(null);
                    }
                }
            }

            @Override
            public void onFailure(final Call<JsonElement> pCall, Throwable throwable) {
                if (currentActivity instanceof BaseActivity)
                    ((BaseActivity) currentActivity).onLoadingFinished();
                throwable.printStackTrace();
                apiRequestDataCallBack.onError(null);
            }
        });

    }

    public void getCreditBalanceFromServer(String url, HashMap<String, Object> params, final APIRequestDataCallBack apiRequestDataCallBack) {

        if (!NetworkUtils.isNetworkAvailable(currentActivity) || !NetworkUtils.isOnline(currentActivity)) {

            if (currentActivity instanceof BaseActivity)
                ((BaseActivity) currentActivity).onLoadingFinished();

            showErrorMessage(currentActivity.getResources().getString(R.string.no_network_available));
            apiRequestDataCallBack.onNoNetwork();
            return;
        }

        Call<JsonElement> call = apiService.getCreditBalance(url, params);
        call.enqueue(new Callback<JsonElement>() {

            @Override
            public void onResponse(@NotNull Call<JsonElement> pCall, @NotNull Response<JsonElement> response) {

                if (currentActivity instanceof BaseActivity) {
                    ((BaseActivity) currentActivity).onLoadingFinished();
                }

                if (response != null && response.body() != null) {
                    apiRequestDataCallBack.onSuccess(response.body());
                } else {
                    try {
                        JSONObject jsonObject = new JSONObject(response.errorBody().string() + "");
                        apiRequestDataCallBack.onError(response.errorBody().string());
                        showErrorMessage(parseErrorMessage(jsonObject));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(final Call<JsonElement> pCall, Throwable throwable) {
                if (currentActivity instanceof BaseActivity)
                    ((BaseActivity) currentActivity).onLoadingFinished();
                throwable.printStackTrace();
                apiRequestDataCallBack.onError(null);
            }
        });

    }

    public void markOrderApproveOrRejectFromServer(String url, HashMap<String, Object> params, final APIRequestDataCallBack apiRequestDataCallBack) {

        if (!NetworkUtils.isNetworkAvailable(currentActivity) || !NetworkUtils.isOnline(currentActivity)) {

            if (currentActivity instanceof BaseActivity)
                ((BaseActivity) currentActivity).onLoadingFinished();

            showErrorMessage(currentActivity.getResources().getString(R.string.no_network_available));
            apiRequestDataCallBack.onNoNetwork();
            return;
        }

        Call<JsonElement> call = apiService.markOrderApproveOrReject(url, params);
        call.enqueue(new Callback<JsonElement>() {

            @Override
            public void onResponse(@NotNull Call<JsonElement> pCall, @NotNull Response<JsonElement> response) {

                if (currentActivity instanceof BaseActivity) {
                    ((BaseActivity) currentActivity).onLoadingFinished();
                }

                if (response != null && response.body() != null) {
                    apiRequestDataCallBack.onSuccess(response.body());
                } else {
                    try {
                        JSONObject jsonObject = new JSONObject(response.errorBody().string() + "");
                        apiRequestDataCallBack.onError(response.errorBody().string());
                        showErrorMessage(parseErrorMessage(jsonObject));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        try {
                            apiRequestDataCallBack.onError(response.errorBody().string());
                        } catch (IOException ex) {
                            ex.printStackTrace();
                            apiRequestDataCallBack.onError(null);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        try {
                            apiRequestDataCallBack.onError(response.errorBody().string());
                        } catch (IOException ex) {
                            ex.printStackTrace();
                            apiRequestDataCallBack.onError(null);
                        }
                    }
                }
            }

            @Override
            public void onFailure(final Call<JsonElement> pCall, Throwable throwable) {
                if (currentActivity instanceof BaseActivity)
                    ((BaseActivity) currentActivity).onLoadingFinished();
                throwable.printStackTrace();
                apiRequestDataCallBack.onError(null);
            }
        });

    }


    public void getLinkSearchFromServer(String url, HashMap<String, Object> params, final APIRequestDataCallBack apiRequestDataCallBack) {

        if (!NetworkUtils.isNetworkAvailable(currentActivity) || !NetworkUtils.isOnline(currentActivity)) {

            if (currentActivity instanceof BaseActivity)
                ((BaseActivity) currentActivity).onLoadingFinished();

            showErrorMessage(currentActivity.getResources().getString(R.string.no_network_available));
            apiRequestDataCallBack.onNoNetwork();
            return;
        }

        Call<JsonElement> call = apiService.getLinkSearch(url, params);
        call.enqueue(new Callback<JsonElement>() {

            @Override
            public void onResponse(@NotNull Call<JsonElement> pCall, @NotNull Response<JsonElement> response) {

                if (currentActivity instanceof BaseActivity) {
                    ((BaseActivity) currentActivity).onLoadingFinished();
                }

                if (response != null && response.body() != null) {
                    apiRequestDataCallBack.onSuccess(response.body());
                } else {
                    try {
                        JSONObject jsonObject = new JSONObject(response.errorBody().string() + "");
                        apiRequestDataCallBack.onError(response.errorBody().string());
                        showErrorMessage(parseErrorMessage(jsonObject));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(final Call<JsonElement> pCall, Throwable throwable) {
                if (currentActivity instanceof BaseActivity)
                    ((BaseActivity) currentActivity).onLoadingFinished();
                throwable.printStackTrace();
                apiRequestDataCallBack.onError(null);
            }
        });

    }

    public void getCompanyCategoryListFromServer(String url, HashMap<String, Object> params, final APIRequestDataCallBack apiRequestDataCallBack) {

        if (!NetworkUtils.isNetworkAvailable(currentActivity) || !NetworkUtils.isOnline(currentActivity)) {

            if (currentActivity instanceof BaseActivity)
                ((BaseActivity) currentActivity).onLoadingFinished();

            showErrorMessage(currentActivity.getResources().getString(R.string.no_network_available));
            apiRequestDataCallBack.onNoNetwork();
            return;
        }

        Call<JsonElement> call = apiService.getCompanyCategoryList(url, params);
        call.enqueue(new Callback<JsonElement>() {

            @Override
            public void onResponse(@NotNull Call<JsonElement> pCall, @NotNull Response<JsonElement> response) {

                if (currentActivity instanceof BaseActivity) {
                    ((BaseActivity) currentActivity).onLoadingFinished();
                }

                if (response != null && response.body() != null) {
                    apiRequestDataCallBack.onSuccess(response.body());
                } else {
                    try {
                        JSONObject jsonObject = new JSONObject(response.errorBody().string() + "");
                        apiRequestDataCallBack.onError(response.errorBody().string());
                        showErrorMessage(parseErrorMessage(jsonObject));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(final Call<JsonElement> pCall, Throwable throwable) {
                if (currentActivity instanceof BaseActivity)
                    ((BaseActivity) currentActivity).onLoadingFinished();
                throwable.printStackTrace();
                apiRequestDataCallBack.onError(null);
            }
        });

    }

    public void getCompanyProductListFromServer(String url, HashMap<String, Object> params, final APIRequestDataCallBack apiRequestDataCallBack) {

        if (!NetworkUtils.isNetworkAvailable(currentActivity) || !NetworkUtils.isOnline(currentActivity)) {

            if (currentActivity instanceof BaseActivity)
                ((BaseActivity) currentActivity).onLoadingFinished();

            showErrorMessage(currentActivity.getResources().getString(R.string.no_network_available));
            apiRequestDataCallBack.onNoNetwork();
            return;
        }

        Call<JsonElement> call = apiService.getCompanyProductList(url, params);
        call.enqueue(new Callback<JsonElement>() {

            @Override
            public void onResponse(@NotNull Call<JsonElement> pCall, @NotNull Response<JsonElement> response) {

                if (currentActivity instanceof BaseActivity) {
                    ((BaseActivity) currentActivity).onLoadingFinished();
                }

                if (response != null && response.body() != null) {
                    apiRequestDataCallBack.onSuccess(response.body());
                } else {
                    try {
                        JSONObject jsonObject = new JSONObject(response.errorBody().string() + "");
                        apiRequestDataCallBack.onError(response.errorBody().string());
                        showErrorMessage(parseErrorMessage(jsonObject));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(final Call<JsonElement> pCall, Throwable throwable) {
                if (currentActivity instanceof BaseActivity)
                    ((BaseActivity) currentActivity).onLoadingFinished();
                throwable.printStackTrace();
                apiRequestDataCallBack.onError(null);
            }
        });

    }

    public void logoutFromServer(String url, HashMap<String, Object> params, final APIRequestDataCallBack apiRequestDataCallBack) {

        if (!NetworkUtils.isNetworkAvailable(currentActivity) || !NetworkUtils.isOnline(currentActivity)) {

            if (currentActivity instanceof BaseActivity)
                ((BaseActivity) currentActivity).onLoadingFinished();

            showErrorMessage(currentActivity.getResources().getString(R.string.no_network_available));
            apiRequestDataCallBack.onNoNetwork();
            return;
        }

        Call<ResponseBody> call = apiService.logout(url, params);
        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(@NotNull Call<ResponseBody> pCall, @NotNull Response<ResponseBody> response) {

                if (currentActivity instanceof BaseActivity) {
                    ((BaseActivity) currentActivity).onLoadingFinished();
                }

                if (response.code() == 200) {
                    apiRequestDataCallBack.onSuccess(null);
                } else {
                    try {
                        JSONObject jsonObject = new JSONObject(response.errorBody().string() + "");
                        apiRequestDataCallBack.onError(response.errorBody().string());
                        showErrorMessage(parseErrorMessage(jsonObject));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(final Call<ResponseBody> pCall, Throwable throwable) {
                if (currentActivity instanceof BaseActivity)
                    ((BaseActivity) currentActivity).onLoadingFinished();
                throwable.printStackTrace();
                apiRequestDataCallBack.onError(null);
            }
        });

    }

    public void purchasePackageFromServer(String url, HashMap<String, Object> params, final APIRequestDataCallBack apiRequestDataCallBack) {

        if (!NetworkUtils.isNetworkAvailable(currentActivity) || !NetworkUtils.isOnline(currentActivity)) {

            if (currentActivity instanceof BaseActivity)
                ((BaseActivity) currentActivity).onLoadingFinished();

            showErrorMessage(currentActivity.getResources().getString(R.string.no_network_available));
            apiRequestDataCallBack.onNoNetwork();
            return;
        }

        Call<JsonElement> call = apiService.purchasePackage(url, params);
        call.enqueue(new Callback<JsonElement>() {

            @Override
            public void onResponse(@NotNull Call<JsonElement> pCall, @NotNull Response<JsonElement> response) {

                if (currentActivity instanceof BaseActivity) {
                    ((BaseActivity) currentActivity).onLoadingFinished();
                }

                if (response != null && response.body() != null) {
                    apiRequestDataCallBack.onSuccess(response.body());
                } else {
                    try {
                        JSONObject jsonObject = new JSONObject(response.errorBody().string() + "");
                        apiRequestDataCallBack.onError(response.errorBody().string());
                        showErrorMessage(parseErrorMessage(jsonObject));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(final Call<JsonElement> pCall, Throwable throwable) {
                if (currentActivity instanceof BaseActivity)
                    ((BaseActivity) currentActivity).onLoadingFinished();
                throwable.printStackTrace();
                apiRequestDataCallBack.onError(null);
            }
        });

    }


    public void syncRequestToServer(String url, HashMap<String, Object> params, final APIRequestDataCallBack apiRequestDataCallBack) {

        if (!NetworkUtils.isNetworkAvailable(currentActivity) || !NetworkUtils.isOnline(currentActivity)) {

//            if (currentActivity instanceof BaseActivity)
//                ((BaseActivity) currentActivity).onLoadingFinished();
//
//            showErrorMessage(currentActivity.getResources().getString(R.string.no_network_available));
            apiRequestDataCallBack.onNoNetwork();
            return;
        }

        Call<JsonElement> call = apiService.syncRequest(url, params);
        call.enqueue(new Callback<JsonElement>() {

            @Override
            public void onResponse(@NotNull Call<JsonElement> pCall, @NotNull Response<JsonElement> response) {

                if (currentActivity instanceof BaseActivity) {
                    ((BaseActivity) currentActivity).onLoadingFinished();
                }

                if (response != null && response.body() != null) {
                    apiRequestDataCallBack.onSuccess(response.body());
                } else {
                    try {
//                        JSONObject jsonObject = new JSONObject(response.errorBody().string() + "");
                        apiRequestDataCallBack.onError(response.errorBody().string());
//                        showErrorMessage(parseErrorMessage(jsonObject));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(final Call<JsonElement> pCall, Throwable throwable) {
                if (currentActivity instanceof BaseActivity)
                    ((BaseActivity) currentActivity).onLoadingFinished();
                throwable.printStackTrace();
                apiRequestDataCallBack.onError(null);
            }
        });

    }


    public void downloadImageRequest(String url, HashMap<String, Object> params, final ImageRequestDataCallBack imageRequestDataCallBack) {

        if (!NetworkUtils.isNetworkAvailable(currentActivity) || !NetworkUtils.isOnline(currentActivity)) {

//            if (currentActivity instanceof BaseActivity)
//                ((BaseActivity) currentActivity).onLoadingFinished();
//
//            showErrorMessage(currentActivity.getResources().getString(R.string.no_network_available));
            imageRequestDataCallBack.onNoNetwork();
            return;
        }

//        Call<JsonElement> call = apiService.downloadImageRequest(url, params);
        Call<ResponseBody> call = apiService.downloadImageRequest(url);
        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(@NotNull Call<ResponseBody> pCall, @NotNull Response<ResponseBody> response) {

                if (currentActivity instanceof BaseActivity) {
                    ((BaseActivity) currentActivity).onLoadingFinished();
                }

                if (response != null && response.body() != null) {
                    imageRequestDataCallBack.onSuccess(response.body());
                } else {
                    try {
//                        JSONObject jsonObject = new JSONObject(response.errorBody().string() + "");
                        imageRequestDataCallBack.onError(response.errorBody().string());
//                        showErrorMessage(parseErrorMessage(jsonObject));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(final Call<ResponseBody> pCall, Throwable throwable) {
                if (currentActivity instanceof BaseActivity)
                    ((BaseActivity) currentActivity).onLoadingFinished();
                throwable.printStackTrace();
                imageRequestDataCallBack.onError(null);
            }
        });

    }

    public void uploadImageRequest(String url, byte[] fileByteArray, final WebApiRequest.ImageRequestDataCallBack imageRequestDataCallBack) {

        if (!NetworkUtils.isNetworkAvailable(currentActivity) || !NetworkUtils.isOnline(currentActivity)) {

//            if (currentActivity instanceof BaseActivity)
//                ((BaseActivity) currentActivity).onLoadingFinished();
//
//            showErrorMessage(currentActivity.getResources().getString(R.string.no_network_available));
            imageRequestDataCallBack.onNoNetwork();
            return;
        }


        MediaType mediaType = MediaType.parse("image/*");
        RequestBody requestFile = RequestBody.create(mediaType, fileByteArray);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", "file", requestFile);

//        MultipartBody.Part body;
//        body = toMultiPartFile("file", fileByteArray);
        Call<ResponseBody> call = apiService.uploadImageRequest(url, body);
        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(@NotNull Call<ResponseBody> pCall, @NotNull Response<ResponseBody> response) {

                if (currentActivity instanceof BaseActivity) {
                    ((BaseActivity) currentActivity).onLoadingFinished();
                }

                if (response != null && response.body() != null) {
                    imageRequestDataCallBack.onSuccess(response.body());
                } else {
//                    try {
//                        JSONObject jsonObject = new JSONObject(response.errorBody().string() + "");
                    imageRequestDataCallBack.onError(null);
//                        showErrorMessage(parseErrorMessage(jsonObject));
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
                }
            }

            @Override
            public void onFailure(final Call<ResponseBody> pCall, Throwable throwable) {
                if (currentActivity instanceof BaseActivity)
                    ((BaseActivity) currentActivity).onLoadingFinished();
                throwable.printStackTrace();
                imageRequestDataCallBack.onError(null);
            }
        });

    }




    public static RequestBody getStringRequestBody(String value) {
        return RequestBody.create(MediaType.parse("text/plain"), value);
    }


    public static MultipartBody.Part toMultiPartFile(String name, byte[] byteArray) {

        RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), byteArray);

        return MultipartBody.Part.createFormData(name,
                null, // filename, this is optional
                reqFile);
    }

    public RequestBody getImageRequestBody(File value) {
        return RequestBody.create(MediaType.parse(getMimeType(value.getAbsolutePath())), value);
    }

    // url = file path or whatever suitable URL you want.
    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

}
