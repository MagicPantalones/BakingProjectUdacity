package io.magics.baking;

import android.content.Context;
import android.content.res.AssetManager;

public class Dataprovider {

    private final Context context;
    private RecipeViewModel viewModel;

    public Dataprovider(Context context, RecipeViewModel viewModel) {
        this.context = context;
        this.viewModel = viewModel;
    }

    public void init(){
        if (viewModel.hasData()) return;

    }

    private void readJsonFile(){
        AssetManager assetManager = context.getAssets();
        String uri = null;

        
    }

}
