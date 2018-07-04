package io.magics.baking.data.db;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;
@Database(version = RecipeDatabase.DB_VERSION)
public class RecipeDatabase {

    public static final int DB_VERSION = 1;

    @Table(RecipeContract.class)
    public static final String RECIPES_TABLE = "recipes_table";
}
