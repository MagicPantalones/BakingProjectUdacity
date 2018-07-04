package io.magics.baking.data.db;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.ConflictResolutionType;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.PrimaryKey;

public class RecipeContract {

    @DataType(DataType.Type.INTEGER)
    @PrimaryKey(onConflict = ConflictResolutionType.REPLACE)
    @AutoIncrement
    public static final String COLUMN_ID = "_id";

    @DataType(DataType.Type.REAL)
    public static final String COLUMN_RECIPE_ID = "recipe_id";

    @DataType(DataType.Type.TEXT)
    public static final String COLUMN_RECIPE_NAME = "recipe_name";

    @DataType(DataType.Type.TEXT)
    public static final String COLUMN_INGREDIENTS = "ingredients";

    @DataType(DataType.Type.TEXT)
    public static final String COLUMN_RECIPE_STEPS = "recipe_steps";

    @DataType(DataType.Type.REAL)
    public static final String COLUMN_SERVINGS = "servings";

    @DataType(DataType.Type.TEXT)
    public static final String COLUMN_IMAGE_URL = "image_url";

    public static final int INDEX_RECIPE_ID = 0;
    public static final int INDEX_NAME = 1;
    public static final int INDEX_INGREDIENTS = 2;
    public static final int INDEX_STEPS = 3;
    public static final int INDEX_SERVINGS = 4;
    public static final int INDEX_IMAGE_URL = 5;

}
