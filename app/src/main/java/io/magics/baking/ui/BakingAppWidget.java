package io.magics.baking.ui;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import java.util.ArrayList;
import java.util.List;

import io.magics.baking.MainActivity;
import io.magics.baking.R;
import io.magics.baking.data.DataProvider;
import io.magics.baking.models.Recipe;

/**
 * Implementation of App Widget functionality.
 */
public class BakingAppWidget extends AppWidgetProvider {

    List<Recipe> recipeList = new ArrayList<>();
    int recipeId = 0;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId, Recipe recipe) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.baking_app_widget);
        Intent intent = new Intent(context, RecipeGridWidgetService.class);
        intent.putExtra("recipe", recipe);
        views.setRemoteAdapter(R.id.widget_ingredient_grid, intent);

        Intent appIntent = new Intent(context, MainActivity.class);
        appIntent.putExtra("recipe", recipe);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                appIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setPendingIntentTemplate(R.id.widget_ingredient_grid, pendingIntent);
        views.setEmptyView(R.id.widget_ingredient_grid, R.id.empty_view);
        appWidgetManager.updateAppWidget(appWidgetId, views);

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        recipeId = intent.getIntExtra("recipeId", 0);
        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        if (recipeList.isEmpty()) recipeList = DataProvider.oneShot(context);
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, recipeList.get(recipeId));
        }
    }

    @Override
    public void onEnabled(Context context) {

        recipeList = DataProvider.oneShot(context);

    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

