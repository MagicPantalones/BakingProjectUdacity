package io.magics.baking.ui;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.annotation.GlideModule;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.magics.baking.R;
import io.magics.baking.data.RecipeViewModel;
import io.magics.baking.models.Recipe;
import io.magics.baking.utils.GlideApp;


public class RecipeListFragment extends Fragment {

    @BindView(R.id.recipe_list_recycler)
    RecyclerView recipeListRecycler;

    Unbinder unbinder;
    RecipeAdapter adapter;


    public RecipeListFragment() {
        // Required empty public constructor
    }

    public static RecipeListFragment newInstance() {
        return new RecipeListFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_recipe_list, container, false);
        unbinder = ButterKnife.bind(this, root);
        adapter = new RecipeAdapter();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecipeViewModel viewModel = ViewModelProviders.of(this).get(RecipeViewModel.class);

        Observer<List<Recipe>> recipeObserver = new Observer<List<Recipe>>() {
            @Override
            public void onChanged(@Nullable List<Recipe> recipes) {
                adapter.addRecipes(recipes);
            }
        };

        viewModel.registerObserver(getActivity(), recipeObserver);

    }

    class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeListViewHolder>{

        List<Recipe> recipeList;

        RecipeAdapter() {
            recipeList = new ArrayList<>();
        }

        @NonNull
        @Override
        public RecipeListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.view_holder_recipe_list, parent, false);
            return new RecipeListViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecipeListViewHolder holder, int position) {
            Recipe recipe = recipeList.get(position);

            holder.recipeNameTv.setText(recipe.getName());
            holder.servingsTv.setText(String.valueOf(recipe.getServings()));

            GlideApp.with(holder.recipeIv)
                    .load(recipe.getImage())
                    .into(holder.recipeIv);


        }

        @Override
        public int getItemCount() {
            return 0;
        }

        public void addRecipes(List<Recipe> recipes) {
            recipeList = recipes;
        }



        class RecipeListViewHolder extends RecyclerView.ViewHolder{

            @BindView(R.id.recipe_name_vh)
            TextView recipeNameTv;
            @BindView(R.id.recipe_servings_vh)
            TextView servingsTv;
            @BindView(R.id.recipe_image_vh)
            ImageView recipeIv;

            RecipeListViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }

    }
}
