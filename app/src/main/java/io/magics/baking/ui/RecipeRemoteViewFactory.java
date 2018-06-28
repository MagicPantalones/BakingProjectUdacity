package io.magics.baking.ui;

import android.content.Context;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.List;

import io.magics.baking.MainActivity;
import io.magics.baking.R;
import io.magics.baking.data.DataProvider;
import io.magics.baking.models.Ingredient;
import io.magics.baking.models.Recipe;
import io.magics.baking.utils.BakingUtils;

public class RecipeRemoteViewFactory implements RemoteViewsService.RemoteViewsFactory {

    Context context;
    List<Ingredient> ingredients;
    Recipe recipe;

    public RecipeRemoteViewFactory(Context appContext, Recipe recipe) {
        context = appContext;
        this.recipe = recipe;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        ingredients = recipe.getIngredients();
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
        Ingredient ingredient = ingredients.get(position);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.baking_app_widget);
        String ingredientText = BakingUtils.formatIngredientText(context, ingredient);

        views.setTextViewText(R.id.ingredient_text_widget, ingredientText);

        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 0;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}
