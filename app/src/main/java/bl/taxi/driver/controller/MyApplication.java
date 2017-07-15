package bl.taxi.driver.controller;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import bl.taxi.driver.interfaces.RetrofitAPI;
import bl.taxi.driver.utils.Constants;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by DELL on 09/07/2017.
 **/

public class MyApplication extends Application {

    private static Retrofit instance;
    private static Retrofit cacheInstance;
    private static RetrofitAPI service;
    private static RetrofitAPI cacheService;
    private static Cache cache;

    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(this);
        setInstance();
        int cacheSize = 10 * 1024 * 1024; // 10 MB
        cache = new Cache(getCacheDir(), cacheSize);
    }

    private synchronized static void setInstance () {
        instance = new Retrofit.Builder()
                .baseUrl(Constants.Base_url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = instance.create(RetrofitAPI.class);
    }

    public static RetrofitAPI getService () {
        if (instance == null || service == null) {
            setInstance();
        }
        return service;
    }

    private synchronized static void setCacheInstance () {

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .cache(cache)
                .build();

        cacheInstance = new Retrofit.Builder()
                .baseUrl(Constants.Base_url)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        cacheService = cacheInstance.create(RetrofitAPI.class);
    }

    public static RetrofitAPI getCacheService () {
        if (cacheInstance == null) {
            setCacheInstance();
        }
        return cacheService;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}