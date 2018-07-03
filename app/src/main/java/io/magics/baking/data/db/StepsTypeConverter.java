package io.magics.baking.data.db;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import io.magics.baking.models.Ingredient;
import io.magics.baking.models.Step;

public class StepsTypeConverter {

    static Gson gson = new GsonBuilder().serializeNulls().create();

    @TypeConverter
    public static List<Step> stringToStepList(String data) {
        if (data == null) {
            return Collections.emptyList();
        }

        Type stepType = new TypeToken<List<Step>>() {}.getType();

        return gson.fromJson(data, stepType);
    }

    @TypeConverter
    public static String stepListToString(List<Step> data) {
        return gson.toJson(data);
    }
}
