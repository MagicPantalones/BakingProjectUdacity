package io.magics.baking.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Ingredient implements Parcelable{

    private double quantity;
    private String measure;
    @SerializedName("ingredient")
    private String recipeIngredient;
    public static final Parcelable.Creator<Ingredient> CREATOR = new Creator<Ingredient>() {
        @Override
        public Ingredient createFromParcel(Parcel source) {
            return new Ingredient(source);
        }

        @Override
        public Ingredient[] newArray(int size) {
            return new Ingredient[size];
        }
    };

    @SuppressWarnings("WeakerAccess")
    public Ingredient(Parcel in) {
        this.quantity = (double) in.readValue(double.class.getClassLoader());
        this.measure = (String) in.readValue(String.class.getClassLoader());
        this.recipeIngredient = (String) in.readValue(String.class.getClassLoader());
    }

    public Ingredient() {}

    public double getQuantity() { return quantity; }
    public String getMeasure() { return measure; }
    public String getRecipeIngredient() { return recipeIngredient; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(quantity);
        dest.writeValue(measure);
        dest.writeValue(recipeIngredient);
    }
}
