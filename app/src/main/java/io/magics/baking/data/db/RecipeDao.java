package io.magics.baking.data.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import io.magics.baking.models.Recipe;
import io.magics.baking.models.Step;

@Dao
public interface RecipeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Recipe recipe);

    @Query("SELECT * FROM recipes_table ORDER BY recipeId ASC")
    List<Recipe> getAllRecipes();

    @Query("SELECT steps FROM recipes_table where recipeId = :id")
    List<Step> getIngredientStep(int id);
}
