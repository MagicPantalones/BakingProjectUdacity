package io.magics.baking.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import java.util.ArrayList;
import java.util.List;

import io.magics.baking.data.db.IngredientTypeConverter;
import io.magics.baking.data.db.StepsTypeConverter;

@Entity(tableName = "recipes_table")
public class Recipes {

    @PrimaryKey(autoGenerate = true)
    private int recipeId;

    @ColumnInfo(name = "recipe_id")
    private double id;

    @ColumnInfo(name = "recipe_name")
    private String name;

    @ColumnInfo(name = "ingredients")
    @TypeConverters(IngredientTypeConverter.class)
    private List<Ingredient> ingredients = new ArrayList<>();

    @ColumnInfo(name = "steps")
    @TypeConverters(StepsTypeConverter.class)
    private List<Step> steps = new ArrayList<>();

    @ColumnInfo(name = "servings")
    private double servings;

    @ColumnInfo(name = "image")
    private String image;

    public Recipes(double id, String name, List<Ingredient> ingredients, List<Step> steps,
                   double servings, String image) {
        this.id = id;
        this.name = name;
        this.ingredients = ingredients;
        this.steps = steps;
        this.servings = servings;
        this.image = image;

    }

    public int getRecipeId() { return recipeId; }

    public double getId() { return id; }

    public String getName() { return name; }

    public List<Ingredient> getIngredients() { return ingredients; }

    public List<Step> getSteps() { return steps; }

    public double getServings() { return servings; }

    public String getImage() { return image; }
}
