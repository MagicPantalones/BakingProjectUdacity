package io.magics.baking;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

public class RecipeViewModel extends ViewModel {

    MutableLiveData<List<Recipe>> recipesLiveData = new MutableLiveData<>();


    public boolean hasData() {
        return recipesLiveData.getValue() != null && !recipesLiveData.getValue().isEmpty();
    }

    public void addRecipies(List<Recipe> recipes) {
        recipesLiveData.setValue(recipes);
    }

}
