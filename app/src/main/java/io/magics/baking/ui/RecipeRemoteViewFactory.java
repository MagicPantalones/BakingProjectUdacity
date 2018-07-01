package io.magics.baking.ui;

import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;
import java.util.List;

import io.magics.baking.R;
import io.magics.baking.data.DataProvider;
import io.magics.baking.models.Ingredient;
import io.magics.baking.utils.BakingUtils;

public class RecipeRemoteViewFactory implements RemoteViewsService.RemoteViewsFactory {

    private static final String KEY_RECIPE = "recipe";

    Context context;
    List<Ingredient> ingredients;
    int recipeId;
    Intent intent;

    public RecipeRemoteViewFactory(Context appContext, Intent intent) {
        context = appContext;
        this.intent = intent;
    }

    @Override
    public void onCreate() {
        //Not needed
    }

    @Override
    public void onDataSetChanged() {
        if (intent.getIntExtra(KEY_RECIPE, -1) == -1) {
            ingredients = new ArrayList<>();
        } else {
            recipeId = intent.getIntExtra(KEY_RECIPE, -1);
            ingredients = DataProvider.oneShot(context).get(recipeId).getIngredients();
        }

    }

    @Override
    public void onDestroy() {
        //Not needed
    }

    @Override
    public int getCount() {
        if (ingredients == null) return 0;
        return ingredients.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {

        if (ingredients.isEmpty()) return null;

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.recipe_widget_grid);

        Ingredient ingredient = ingredients.get(position);
        String ingredientText = BakingUtils.formatIngredientText(context, ingredient);

        views.setTextViewText(R.id.ingredient_text_widget, ingredientText);

        Intent fillIntent = new Intent();
        fillIntent.putExtra(KEY_RECIPE, recipeId);
        views.setOnClickFillInIntent(R.id.ingredient_text_widget, fillIntent);
        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}
