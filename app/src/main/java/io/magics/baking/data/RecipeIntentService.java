package io.magics.baking.data;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.Context;

import io.magics.baking.R;
import io.magics.baking.models.Recipe;
import io.magics.baking.ui.BakingAppWidget;


public class RecipeIntentService extends IntentService {
    private static final String ACTION_UPDATE_RECIPE_WIDGET =
            "io.magics.baking.data.action.UPDATE_RECIPE_WIDGET";

    private static final String EXTRA_RECIPE_ID = "io.magics.baking.data.extra.RECIPE_ID";

    public RecipeIntentService() {
        super("RecipeIntentService");
    }

    public static void startActionUpdateRecipeWidget(Context context, int recipeId) {
        Intent intent = new Intent(context, RecipeIntentService.class);
        intent.setAction(ACTION_UPDATE_RECIPE_WIDGET);
        intent.putExtra(EXTRA_RECIPE_ID, recipeId);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_UPDATE_RECIPE_WIDGET.equals(action)) {
                final int recipeId = intent.getIntExtra(EXTRA_RECIPE_ID, -1);
                handleActionUpdateRecipeWidget(recipeId);
            }
        }
    }

    private void handleActionUpdateRecipeWidget(int recipeId) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this,
                BakingAppWidget.class));
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_ingredient_grid);
        BakingAppWidget.updateAppWidgets(this, appWidgetManager, appWidgetIds, recipeId);
    }
}
