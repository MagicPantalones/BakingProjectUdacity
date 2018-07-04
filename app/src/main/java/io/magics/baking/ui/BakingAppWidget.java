package io.magics.baking.ui;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.RemoteViews;


import io.magics.baking.MainActivity;
import io.magics.baking.R;
import io.magics.baking.models.Recipe;

/**
 * Implementation of App Widget functionality.
 */
public class BakingAppWidget extends AppWidgetProvider {


    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId, Recipe recipe) {

        Intent appIntent = new Intent(context, MainActivity.class);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.baking_app_widget);
        PendingIntent pendingIntent;

        if (recipe != null) {
            String recipeName = recipe.getName();
            Intent intent = new Intent(context, RecipeGridWidgetService.class);
            int resId = (int) recipe.getId();
            intent.putExtra("recipe", resId);
            views.setRemoteAdapter(R.id.widget_ingredient_grid, intent);
            views.setTextViewText(R.id.widget_recipe_name, recipeName);
            appIntent.putExtra("recipe", resId);
            views.setEmptyView(R.id.widget_ingredient_grid, R.id.empty_view);

            pendingIntent = PendingIntent.getActivity(context, 0,
                    appIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            views.setPendingIntentTemplate(R.id.widget_ingredient_grid, pendingIntent);
        } else {
            views.setViewVisibility(R.id.widget_ingredient_grid, View.GONE);
            views.setViewVisibility(R.id.empty_view, View.VISIBLE);

            pendingIntent = PendingIntent.getActivity(context, 0,
                    appIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            views.setOnClickPendingIntent(R.id.empty_view, pendingIntent);
        }

        appWidgetManager.updateAppWidget(appWidgetId, views);

    }

    public static void updateAppWidgets(Context context, AppWidgetManager appWidgetManager,
                                        int[] appwidgetIds, Recipe recipe) {
        for (int appWidgetId : appwidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, recipe);
        }
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context,
                BakingAppWidget.class));
        updateAppWidgets(context, appWidgetManager, appWidgetIds, null);
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

