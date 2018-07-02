package io.magics.baking.data;

import android.content.Context;
import android.content.res.AssetManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;


import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.magics.baking.models.Recipe;
import io.magics.baking.utils.BakingUtils;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import timber.log.Timber;

public class DataProvider {

    private static final int CONNECTION_TIMEOUT = 10;
    private static final String BASE_URL =
            "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/";

    private RecipeViewModel viewModel;

    private Disposable apiDisposable;

    interface RecipeApi {
        @GET("baking.json")
        Observable<List<Recipe>> getRecipes();

        @GET("baking.json")
        List<Recipe> getOneShotRecipes();
    }

    public DataProvider(RecipeViewModel viewModel) {
        this.viewModel = viewModel;
    }

    public void init() {
        if (viewModel.hasData()) return;
        apiDisposable = getRecipes();
    }

    public void dispose() {
        BakingUtils.dispose(apiDisposable);
    }

    private Disposable getRecipes() {
        return callApi().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleSuccessCall, DataProvider::handleErrorCall);
    }

    private static Observable<List<Recipe>> callApi() {
        return getRetrofitClient().create(RecipeApi.class)
                .getRecipes();
    }


    private void handleSuccessCall(List<Recipe> recipes){
        if (recipes != null && !recipes.isEmpty()) {
            viewModel.addRecipies(recipes);
            Timber.w("Recipes loaded, first: %s", recipes.get(0).getName());
        }
    }

    private static void handleErrorCall(Throwable e) {
        Timber.e(e, "Error while reading JSON file");
    }

    public static boolean isInternetConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        //noinspection ConstantConditions
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private static Retrofit getRetrofitClient() {
        Gson gson = new GsonBuilder().serializeNulls().create();
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build();
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClient)
                .build();
    }

    public static Observable<List<Recipe>> oneShot() {
        return callApi();
    }
}
