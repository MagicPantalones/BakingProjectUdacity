package io.magics.baking.ui;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class RecipeGridWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RecipeRemoteViewFactory(this.getApplicationContext(), intent);
    }

}

