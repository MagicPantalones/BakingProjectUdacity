package io.magics.baking.data;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;

import java.util.List;

import io.magics.baking.models.Recipe;

public class RecipeViewModel extends ViewModel {

    private MutableLiveData<List<Recipe>> recipesLiveData = new MutableLiveData<>();


    public boolean hasData() {
        return recipesLiveData.getValue() != null && !recipesLiveData.getValue().isEmpty();
    }

    public void addRecipies(List<Recipe> recipes) {
        recipesLiveData.setValue(recipes);
    }

    public void registerObserver(LifecycleOwner lifecycleOwner, Observer<List<Recipe>> observer){
        recipesLiveData.observe(lifecycleOwner, observer);
    }

    public void unregisterObserver(Observer<List<Recipe>> observer) {
        recipesLiveData.removeObserver(observer);
    }

}
