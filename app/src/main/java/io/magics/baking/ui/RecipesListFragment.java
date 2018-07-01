package io.magics.baking.ui;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.magics.baking.MainActivity;
import io.magics.baking.R;
import io.magics.baking.data.RecipeViewModel;
import io.magics.baking.models.Recipe;
import io.magics.baking.utils.GlideApp;


public class RecipesListFragment extends Fragment {

    private static final String ARG_TWO_PANE = "two_pane";

    private boolean twoPane;

    @BindView(R.id.recipe_list_recycler)
    RecyclerView recipeListRecycler;

    Unbinder unbinder;
    RecipeAdapter adapter;
    RecipeViewModel viewModel;
    Observer<List<Recipe>> recipeObserver;

    public RecipesListFragment() {
        // Required empty public constructor
    }

    public static RecipesListFragment newInstance(boolean twoPane) {
        RecipesListFragment frag = new RecipesListFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_TWO_PANE, twoPane);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            twoPane = getArguments().getBoolean(ARG_TWO_PANE);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_recipe_list, container, false);
        unbinder = ButterKnife.bind(this, root);
        adapter = new RecipeAdapter(((MainActivity) getActivity()));


        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



        //noinspection ConstantConditions
        viewModel = ViewModelProviders.of(getActivity()).get(RecipeViewModel.class);

        if (twoPane){
            recipeListRecycler.setLayoutManager(new GridLayoutManager(getContext(), 3));
        } else {
            recipeListRecycler.setLayoutManager(new LinearLayoutManager(getContext(),
                    LinearLayoutManager.VERTICAL, false));
        }

        recipeListRecycler.setAdapter(adapter);

        recipeObserver = recipes -> adapter.addRecipes(recipes);

        viewModel.registerObserver(getActivity(), recipeObserver);


    }

    @Override
    public void onDestroy() {
        viewModel.unregisterObserver(recipeObserver);
        super.onDestroy();
    }

    public interface RecipeListener {
        void onRecipeClick(Recipe recipe);
    }

    class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeListViewHolder>{

        List<Recipe> recipeList;
        RecipeListener recipeListener;
        RecipeAdapter(RecipeListener recipeListener) {
            recipeList = new ArrayList<>();
            this.recipeListener = recipeListener;
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

            holder.itemView.setOnClickListener(v ->
                    recipeListener.onRecipeClick(recipe));

            GlideApp.with(holder.recipeIv)
                    .load(recipe.getImage())
                    .fallback(R.drawable.baking_logo_image_large)
                    .placeholder(R.drawable.baking_logo_image_large)
                    .error(R.drawable.baking_logo_image_large)
                    .into(holder.recipeIv);


        }

        @Override
        public int getItemCount() {
            if (recipeList == null)return 0;
            return recipeList.size();
        }

        void addRecipes(List<Recipe> recipes) {
            recipeList = recipes;
            notifyDataSetChanged();
        }



        class RecipeListViewHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.recipe_name_vh)
            TextView recipeNameTv;
            @BindView(R.id.recipe_servings_vh)
            TextView servingsTv;
            @BindView(R.id.recipe_image_vh)
            ImageView recipeIv;
            @BindView(R.id.view_holder_card_wrapper)
            CardView viewHolderWrapper;

            RecipeListViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }

    }
}
