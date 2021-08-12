package com.app.bizlinked.webServices;


import android.app.Activity;

import com.app.bizlinked.BuildConfig;
import com.app.bizlinked.activities.MainActivity;
import com.app.bizlinked.activities.baseClass.BaseActivity;
import com.app.bizlinked.constant.AppConstant;
import com.app.bizlinked.helpers.common.Utils;
import com.app.bizlinked.helpers.preference.BasePreferenceHelper;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

import okhttp3.Authenticator;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WebServiceFactory {

    private static WebService webservice;

    public static WebService getInstance(final Activity activity) {

        final BasePreferenceHelper prefHelper = new BasePreferenceHelper(activity.getApplicationContext());

        if (webservice == null) {

            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            // set your desired log level
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            final OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.connectTimeout(30, TimeUnit.SECONDS);
            httpClient.readTimeout(30, TimeUnit.SECONDS);
            httpClient.writeTimeout(30, TimeUnit.SECONDS);

            Authenticator unAuthorizeAuthenticator =  new Authenticator() {
                @Nullable
                @Override
                public Request authenticate(Route route, Response response) throws IOException {

                    if(response.networkResponse().request().url().toString().contains("bizlinkedapi.herokuapp")
                            && response.code() ==  401){

                        // Refresh your access_token using a synchronous api request
                        HashMap<String, Object> params = new HashMap<>();
                        params.put("grant_type", "refresh_token");
                        params.put("refresh_token", prefHelper.getUserRefreshToken());

                        prefHelper.putUserTokenType(AppConstant.AUTH_TOKEN_TYPES.BASIC);

                        Call<JsonElement> syncCall = webservice.refreshToken(AppConstant.ServerAPICalls.BIZLINKED_AUTH_URL, params);
                        retrofit2.Response<JsonElement> serverResponse = syncCall.execute();

                        if (serverResponse != null
                                && serverResponse.body() != null
                                && !serverResponse.body().isJsonNull()
                                && serverResponse.body().isJsonObject()) {

                            JsonObject jsonObject = serverResponse.body().getAsJsonObject();
                            if (jsonObject.has("access_token")) {

                                prefHelper.putUserTokenType(jsonObject.get("token_type").getAsString());
                                prefHelper.putUserToken(jsonObject.get("access_token").getAsString());
                                prefHelper.putUserRefreshToken(jsonObject.get("refresh_token").getAsString());

                                // Add new header to rejected request and retry it
                                Request.Builder requestBuilder = response.request().newBuilder();
                                if(Utils.isEmptyOrNull(prefHelper.getUserTokenType()) || prefHelper.getUserTokenType().equalsIgnoreCase(AppConstant.AUTH_TOKEN_TYPES.BASIC)){
                                    requestBuilder.header("Authorization", String.format("%s %s", AppConstant.AUTH_TOKEN_TYPES.BASIC, BuildConfig.BASIC_TOKEN));
                                }else if (prefHelper.getUserTokenType().equalsIgnoreCase(AppConstant.AUTH_TOKEN_TYPES.BEARER) && !Utils.isEmptyOrNull(prefHelper.getUserToken())){
                                    requestBuilder.header("Authorization", String.format("%s %s", AppConstant.AUTH_TOKEN_TYPES.BEARER, prefHelper.getUserToken()));
                                }

                                return requestBuilder.build();


                            } else {

                                ((MainActivity) activity).afterLogoutTask();
                                return null;
                            }


                        } else {
                            ((MainActivity) activity).afterLogoutTask();
                            return null;
                        }
                    }
                    return null;
                }
            };




//            final String lang;
//            if (Utils.isEmptyOrNull(prefHelper.getStringPrefrence(BasePreferenceHelper.LANG_KEY))) {
//                lang = "en";
//            } else {
//                lang = prefHelper.getStringPrefrence(BasePreferenceHelper.LANG_KEY);
//            }

//             add your other interceptors …
//
            httpClient.addInterceptor(new Interceptor() {
                @Override
                public okhttp3.Response intercept(Chain chain) throws IOException {
                    Request original = chain.request();
                    // Request customization: add request headers
                    Request request = null;

                    Request.Builder requestBuilder = original.newBuilder();

                    if(Utils.isEmptyOrNull(prefHelper.getUserTokenType()) || prefHelper.getUserTokenType().equalsIgnoreCase(AppConstant.AUTH_TOKEN_TYPES.BASIC)){
                        requestBuilder.addHeader("Authorization", String.format("%s %s", AppConstant.AUTH_TOKEN_TYPES.BASIC, BuildConfig.BASIC_TOKEN));
                    }else if (prefHelper.getUserTokenType().equalsIgnoreCase(AppConstant.AUTH_TOKEN_TYPES.BEARER) && !Utils.isEmptyOrNull(prefHelper.getUserToken())){
                        requestBuilder.addHeader("Authorization", String.format("%s %s", AppConstant.AUTH_TOKEN_TYPES.BEARER, prefHelper.getUserToken()));
                    }
                    request = requestBuilder.build();

                    return chain.proceed(request);

                }
            });

            httpClient.addInterceptor(logging);


            //Refresh Token Work for Bizlinked API
            httpClient.authenticator(unAuthorizeAuthenticator);

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://localhost/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build())
                    .addConverterFactory(GsonConverterFactory.create(new Gson()))
                    .build();

            webservice = retrofit.create(WebService.class);
        }


        return webservice;
    }
}

