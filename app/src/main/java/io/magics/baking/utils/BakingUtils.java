package io.magics.baking.utils;

import android.content.Context;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import butterknife.Unbinder;
import io.magics.baking.models.Ingredient;
import io.magics.baking.models.Recipe;
import io.magics.baking.models.Step;

public class BakingUtils {

    private BakingUtils() {
    }

    public static void dispose(Object... objects) {
        for (Object object : objects) {
            if (object instanceof Unbinder) ((Unbinder) object).unbind();
            else if (object instanceof AnimatedVectorDrawable &&
                    ((AnimatedVectorDrawable) object).isRunning()) {
                ((AnimatedVectorDrawable) object).stop();
            }
        }
    }

    public static void setUniqueTransitionName(View view, Step step) {
        String transitionName = step.getShortDescription();
        view.setTransitionName(transitionName != null && transitionName.length() != 0 ?
                step.getShortDescription() : "stepId" + step.getId());
    }

    public static String formatMeasure(String unit, boolean plural) {

        if (unit == null) return null;

        unit = unit.toLowerCase();

        switch (unit) {
            case "unit":
                return null;
            case "tblsp":
                unit = plural ? "tablespoons" : "tablespoon";
                break;
            case "tsp":
                unit = plural ? "teaspoons" : "teaspoon";
                break;
            case "g":
                unit = plural ? "grams" : "gram";
                break;
            case "k":
                unit = plural ? "kilograms" : "kilogram";
                break;
            case "cup":
                unit = plural ? "cups" : "cup";
                break;
            default:
                return unit;
        }

        return unit;

    }

    public static List<Ingredient> getStepIngredients(Step step, Recipe recipe) {
        List<Ingredient> ingredients = recipe.getIngredients();
        List<Ingredient> stepIngredients = new ArrayList<>();
        String description = step.getDescription().toLowerCase();
        String ignore =
                "add adding and or all melted cut baking temperature cold hot warm heated cool room";

        for (Ingredient ingredient : ingredients) {
            String fullIngredient = ingredient.getRecipeIngredient().toLowerCase();
            fullIngredient = fullIngredient.replaceAll("[^\\p{Alpha}]+", " ");
            fullIngredient = fullIngredient.trim();
            String[] words = fullIngredient.split("\\s");
            for (String word : words) {
                if (ignore.contains(word)) continue;
                if ((description.contains(word)
                        || description.contains(word + "es")
                        || description.contains(word + "s"))
                        && !stepIngredients.contains(ingredient)){

                    stepIngredients.add(ingredient);
                }
            }
        }
        return stepIngredients;
    }


    public static int dpToPx(Context context, float dp) {
        return Math.round(dp * context.getResources().getDisplayMetrics().density);
    }
}
