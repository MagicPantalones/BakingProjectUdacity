package io.magics.baking.data.db;


import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

@ContentProvider(authority = RecipeProvider.AUTHORITY, database = RecipeDatabase.class)
public class RecipeProvider {

    public static final String AUTHORITY = "io.magics.baking.data.db.RecipeProvider";

    @TableEndpoint(table = RecipeDatabase.RECIPES_TABLE)
    public static class Recipes {
        @ContentUri(
                path = "recipes",
                type = "vnd.android.cursor.dir/recipes",
                defaultSort = RecipeContract.COLUMN_RECIPE_ID + " ASC")
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/recipes");
    }

}

