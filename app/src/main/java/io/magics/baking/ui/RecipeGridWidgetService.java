package io.magics.baking.ui;

import android.content.Intent;
import android.widget.RemoteViewsService;

import io.magics.baking.models.Recipe;

public class RecipeGridWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        Recipe recipe = intent.getParcelableExtra("recipe");
        return new RecipeRemoteViewFactory(this.getApplicationContext(), recipe);
    }

}

