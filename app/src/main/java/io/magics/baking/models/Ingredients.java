package io.magics.baking.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Ingredients implements Parcelable{

    private double quantity;
    private String measure;
    private String ingredient;
    public static final Parcelable.Creator<Ingredients> CREATOR = new Creator<Ingredients>() {
        @Override
        public Ingredients createFromParcel(Parcel source) {
            return new Ingredients(source);
        }

        @Override
        public Ingredients[] newArray(int size) {
            return new Ingredients[size];
        }
    };

    public Ingredients(Parcel in) {
        this.quantity = (double) in.readValue(double.class.getClassLoader());
        this.measure = (String) in.readValue(String.class.getClassLoader());
        this.ingredient = (String) in.readValue(String.class.getClassLoader());
    }

    public Ingredients() {}

    public double getQuantity() { return quantity; }
    public String getMeasure() { return measure; }
    public String getIngredient() { return ingredient; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(quantity);
        dest.writeValue(measure);
        dest.writeValue(ingredient);
    }
}
