package com.app.bizlinked.webServices;

import android.graphics.Bitmap;

import com.app.bizlinked.BuildConfig;
import com.app.bizlinked.constant.AppConstant;
import com.google.gson.JsonElement;

import java.util.HashMap;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.QueryMap;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public interface WebService {


    //Get Access Token From Server
    @FormUrlEncoded
    @POST(AppConstant.ServerAPICalls.GET_ACCESS_TOKEN)
    Call<JsonElement> getAccessToken(
            @FieldMap HashMap<String, Object> params
    );

    //Generate OTP from Server
    @PUT(AppConstant.ServerAPICalls.GENERATE_OTP)
    Call<JsonElement> generateOTP(
            @Body HashMap<String, Object> params
    );

    //Verify OTP from Server
    @FormUrlEncoded
    @POST(AppConstant.ServerAPICalls.VERIFY_OTP)
    Call<JsonElement> verifyOTP(
            @FieldMap HashMap<String, Object> params
    );

    //GET/CHECK COMPANY PROFILE
    @GET(AppConstant.ServerAPICalls.GET_COMPANY_PROFILE)
    Call<JsonElement> getCompanyProfile(
            @QueryMap HashMap<String, Object> params
    );

    //Upload Profile To Server Server
    @POST(AppConstant.ServerAPICalls.UPLOAD_PROFILE)
    Call<JsonElement> uploadProfile(
            @Body HashMap<String, Object> params
    );



    //Generate OTP from Server
    @POST
    Call<JsonElement> syncRequest(
            @Url String url,
            @Body HashMap<String, Object> params
    );


    @Multipart
    @POST
    Call<ResponseBody> uploadImageRequest(
            @Url String url,
            @Part MultipartBody.Part image
    );

    @GET
    Call<ResponseBody> downloadImageRequest(
            @Url String url
//            ,
//            @QueryMap HashMap<String, Object> data
    );

    @GET
    Call<JsonElement> getCreditBalance(
            @Url String url,
            @QueryMap HashMap<String, Object> data
    );


    //Purchase Package
    @POST
    Call<JsonElement> purchasePackage(
            @Url String url,
            @Body HashMap<String, Object> params
    );


    //logout
    @PUT
    Call<ResponseBody> logout(
            @Url String url,
            @Body HashMap<String, Object> params
    );


    //getLinkSearch
    @GET
    Call<JsonElement> getLinkSearch(
            @Url String url,
            @QueryMap HashMap<String, Object> params
    );


    //getCompanyCategoryList
    @GET
    Call<JsonElement> getCompanyCategoryList(
            @Url String url,
            @QueryMap HashMap<String, Object> params
    );

    //getCompanyProductList
    @GET
    Call<JsonElement> getCompanyProductList(
            @Url String url,
            @QueryMap HashMap<String, Object> params
    );

    @FormUrlEncoded
    @POST
    Call<JsonElement> refreshToken(
            @Url String url,
            @FieldMap HashMap<String, Object> params
    );

    @FormUrlEncoded
    @PUT
    Call<JsonElement> markOrderApproveOrReject(
            @Url String url,
            @FieldMap HashMap<String, Object> params
    );


//    @Headers({"Content-type: application/json"})
//    @POST(AppConstant.ServerAPICalls.SAVE_MEDICAL_PROFILE)
//    Call<JsonElement> saveMedicalProfile(
//            @Body HashMap<String, Object> params
//    );

    //Get Admin Token From Server
//    @PUT(BuildConfig.AUTH_URL)
//    Call<JsonElement> updateMedicalProfile(
//            @Body HashMap<String, Object> params
//    );

//    //Get All Blood Groups
//    @GET(AppConstant.ServerAPICalls.GET_BLOOD_GROUP)
//    Call<JsonElement> getAllBloodGroups(
//            @QueryMap HashMap<String, String> params
//    );


}
