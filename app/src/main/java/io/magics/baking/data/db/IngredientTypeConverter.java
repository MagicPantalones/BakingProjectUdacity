package io.magics.baking.data.db;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import io.magics.baking.models.Ingredient;

public class IngredientTypeConverter {

    static Gson gson = new GsonBuilder().serializeNulls().create();

    @TypeConverter
    public static List<Ingredient> stringToIngredientList(String data) {
        if (data == null) {
            return Collections.emptyList();
        }

        Type ingredientType = new TypeToken<List<Ingredient>>() {}.getType();

        return gson.fromJson(data, ingredientType);
    }

    @TypeConverter
    public static String ingredientListToString(List<Ingredient> data) {
        return gson.toJson(data);
    }
}
