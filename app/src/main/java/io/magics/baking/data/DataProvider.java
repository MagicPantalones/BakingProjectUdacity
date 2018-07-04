package io.magics.baking.data;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;


import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.magics.baking.MainActivity;
import io.magics.baking.data.db.RecipeContract;
import io.magics.baking.data.db.RecipeProvider;
import io.magics.baking.models.Ingredient;
import io.magics.baking.models.Recipe;
import io.magics.baking.models.Step;
import io.magics.baking.utils.BakingUtils;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import timber.log.Timber;

import static io.magics.baking.data.db.RecipeProvider.Recipes.CONTENT_URI;

public class DataProvider {

    private static final int CONNECTION_TIMEOUT = 10;
    private static final String BASE_URL =
            "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/";

    public static final String[] RECIPE_PROJECTION = {
            RecipeContract.COLUMN_RECIPE_ID,
            RecipeContract.COLUMN_RECIPE_NAME,
            RecipeContract.COLUMN_INGREDIENTS,
            RecipeContract.COLUMN_RECIPE_STEPS,
            RecipeContract.COLUMN_SERVINGS,
            RecipeContract.COLUMN_IMAGE_URL
    };

    private final Context context;

    private RecipeViewModel viewModel;

    private Disposable apiDisposable;
    private Disposable bulkInsertDisposable;


    interface RecipeApi {
        @GET("baking.json")
        Observable<List<Recipe>> getRecipes();
    }

    public DataProvider(Context context, RecipeViewModel viewModel) {
        this.context = context;
        this.viewModel = viewModel;
    }

    public void init(){
        callDbForList();
    }

    public void dispose() {
        BakingUtils.dispose(apiDisposable, bulkInsertDisposable);
    }

    private Disposable getRecipes() {
        return getRetrofitClient().create(RecipeApi.class)
                .getRecipes()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleSuccessCall, DataProvider::handleErrorCall);
    }

    private void handleSuccessCall(List<Recipe> recipes){
        if (recipes != null && !recipes.isEmpty()) {
            viewModel.addRecipies(recipes);

            bulkInsertDisposable = Observable.just(context.getContentResolver()
                    .bulkInsert(CONTENT_URI, getContentValsForDB(recipes)))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(integer -> {}, DataProvider::handleErrorCall);


            Timber.w("Recipes loaded, first: %s", recipes.get(0).getName());
        } else handleErrorCall(new Throwable("Empty list from API"));
    }

    private ContentValues[] getContentValsForDB(List<Recipe> recipes) {
        ContentValues[] retList = new ContentValues[recipes.size()];

        for (Recipe recipe : recipes) {
            String ingredientJson = BakingUtils.listToJson(recipe.getIngredients());
            String stepJson = BakingUtils.listToJson(recipe.getSteps());

            ContentValues cv = new ContentValues();
            cv.put(RecipeContract.COLUMN_RECIPE_ID, recipe.getId());
            cv.put(RecipeContract.COLUMN_RECIPE_NAME, recipe.getName());
            cv.put(RecipeContract.COLUMN_INGREDIENTS, ingredientJson);
            cv.put(RecipeContract.COLUMN_RECIPE_STEPS, stepJson);
            cv.put(RecipeContract.COLUMN_SERVINGS, recipe.getServings());
            cv.put(RecipeContract.COLUMN_IMAGE_URL, recipe.getImage());

            retList[(int) recipe.getId() - 1] = cv;
        }

        return retList;
    }

    private static void handleErrorCall(Throwable e) {
        Timber.e(e, "DataProviderError");
    }

    private void callDbForList() {

        Cursor cursor = context.getContentResolver()
                .query(CONTENT_URI, RECIPE_PROJECTION,
                        null,
                        null,
                        null);

        List<Recipe> recipeList = getRecipeFromCursor(cursor);

        viewModel.addRecipies(recipeList);

        if (recipeList.isEmpty() && isInternetConnected(context)) apiDisposable = getRecipes();
    }

    private static List<Recipe> getRecipeFromCursor(Cursor cursor) {
        List<Recipe> recipeList = new ArrayList<>();

        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                Recipe r = new Recipe();
                String ingredientsJson = cursor.getString(RecipeContract.INDEX_INGREDIENTS);
                String stepsJson = cursor.getString(RecipeContract.INDEX_STEPS);
                r.setId(cursor.getDouble(RecipeContract.INDEX_RECIPE_ID));
                r.setName(cursor.getString(RecipeContract.INDEX_NAME));
                r.setIngredients(BakingUtils.jsonToList(Ingredient[].class, ingredientsJson));
                r.setSteps(BakingUtils.jsonToList(Step[].class, stepsJson));
                r.setServings(cursor.getDouble(RecipeContract.INDEX_SERVINGS));
                r.setImage(cursor.getString(RecipeContract.INDEX_IMAGE_URL));
                recipeList.add(r);
            }
            cursor.close();
        }

        return recipeList;
    }

    public static Recipe oneShot(Context context, int recipeId) {

        Cursor cursor = context.getContentResolver()
                .query(CONTENT_URI,
                        RECIPE_PROJECTION,
                        RecipeContract.COLUMN_RECIPE_ID + "=" + recipeId,
                        null,
                        null);
        List<Recipe> tempList = getRecipeFromCursor(cursor);

        return !tempList.isEmpty() ? tempList.get(0) : null;
    }

    private static boolean isInternetConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        //noinspection ConstantConditions
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public static List<Recipe> oneShotList(Context context) {
        Cursor cursor = context.getContentResolver()
                .query(CONTENT_URI, RECIPE_PROJECTION,
                        null,
                        null,
                        null);

        return getRecipeFromCursor(cursor);
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



}
