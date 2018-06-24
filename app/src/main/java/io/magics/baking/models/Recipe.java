package io.magics.baking.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Recipe implements Parcelable{

    private double id;
    private String name;
    private List<Ingredient> ingredients = new ArrayList<>();
    private List<Step> steps = new ArrayList<>();
    private double servings;
    private String image;

    public static final Parcelable.Creator<Recipe> CREATOR = new Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel source) {
            return new Recipe(source);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };

    public Recipe(Parcel in) {
        this.id = (double) in.readValue(double.class.getClassLoader());
        this.name = (String) in.readValue(String.class.getClassLoader());
        in.readList(this.ingredients, Ingredient.class.getClassLoader());
        in.readList(this.steps, Step.class.getClassLoader());
        this.servings = (double) in.readValue(double.class.getClassLoader());
        this.image = (String) in.readValue(String.class.getClassLoader());
    }

    public Recipe() {}

    public double getId() { return id; }
    public String getName() { return name; }
    public List<Ingredient> getIngredients() { return ingredients; }
    public List<Step> getSteps() { return steps; }
    public double getServings() { return servings; }
    public String getImage() { return image; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(name);
        dest.writeList(ingredients);
        dest.writeList(steps);
        dest.writeValue(servings);
        dest.writeValue(image);
    }
}
