package io.magics.baking;

import android.content.Context;
import android.content.res.AssetManager;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;


import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.List;

public class DataProvider {

    private final Context context;
    private RecipeViewModel viewModel;

    public DataProvider(Context context, RecipeViewModel viewModel) {
        this.context = context;
        this.viewModel = viewModel;
    }

    public void init(){
        if (viewModel.hasData()) return;
        readJsonFile();
    }

    private void readJsonFile(){
        AssetManager assetManager = context.getAssets();

        Gson gson = new GsonBuilder().serializeNulls().create();
        try {

            try (JsonReader reader = new JsonReader(new InputStreamReader(
                    assetManager.open("baking.json")))) {

                Type recipeType = new TypeToken<List<Recipe>>() {
                }.getType();

                List<Recipe> recipes = gson.fromJson(reader, recipeType);
                if (recipes == null || recipes.isEmpty()) {
                    Toast.makeText(context, "Error while reading JSON file", Toast.LENGTH_LONG)
                            .show();
                } else {
                    viewModel.addRecipies(recipes);
                }
            }

        } catch (IOException e) {
            Toast.makeText(context, "Could not read JSON file", Toast.LENGTH_LONG).show();
        }
    }

}
