package io.magics.baking.ui;


import android.content.Context;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.magics.baking.R;
import io.magics.baking.models.Ingredient;
import io.magics.baking.models.Recipe;
import io.magics.baking.models.Step;
import io.magics.baking.utils.BakingUtils;

public class RecipeIngredientsFragment extends Fragment {

    private static final String ARG_RECIPE = "recipe";
    private static final int MAX_LIST_LINES_COLLAPSED = 3;
    private static final String KEY_MANAGER_STATE = "manager_state";

    @BindView(R.id.steps_ingredients_list_collapsed)
    ListView listViewCollapsed;
    @Nullable @BindView(R.id.steps_ingredients_list_expanded)
    ListView listViewExpanded;
    @Nullable @BindView(R.id.steps_ingredients_toggle)
    ImageView listToggleButton;
    @BindView(R.id.ingredients_list_card_wrapper)
    CardView listWrapper;
    @Nullable @BindView(R.id.collapse_expand_header)
    View expandedListHeader;
    @BindView(R.id.steps_list)
    RecyclerView stepsRecycler;

    private Recipe recipe;
    private ArrayAdapter<String> collapsedAdapter;
    private ArrayAdapter<String> expandedAdapter;
    private AnimatedVectorDrawable toggleAnimation;
    private StepListListener stepListListener;

    Unbinder unbinder;


    public RecipeIngredientsFragment() {
        // Required empty public constructor
    }


    public static RecipeIngredientsFragment newInstance(Recipe recipe) {
        RecipeIngredientsFragment fragment = new RecipeIngredientsFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_RECIPE, recipe);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            recipe = getArguments().getParcelable(ARG_RECIPE);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_ingredients_list, container, false);
        unbinder = ButterKnife.bind(this, root);

        return root;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setLists();


        if (listViewExpanded != null) {
            listViewCollapsed.setAdapter(collapsedAdapter);
            listViewExpanded.setAdapter(expandedAdapter);

            listToggleButton.setOnClickListener(v -> {
                boolean isShown = listViewExpanded.isShown();
                listViewExpanded.setVisibility(isShown ? View.GONE : View.VISIBLE);
                expandedListHeader.setVisibility(isShown ? View.GONE : View.VISIBLE);
                if (!isShown) {
                    toggleAnimation =
                            (AnimatedVectorDrawable) listToggleButton.getDrawable();
                    toggleAnimation.mutate();
                    toggleAnimation.start();
                } else {
                    listToggleButton.setImageResource(R.drawable.show_more_anim);
                }
            });
        } else {
            listViewCollapsed.setAdapter(expandedAdapter);
        }

        RecipeStepAdapter adapter = new RecipeStepAdapter(stepListListener);
        stepsRecycler.setAdapter(adapter);
        adapter.setSteps(recipe.getSteps());

        if (savedInstanceState != null) {
            RecyclerView.LayoutManager manager = stepsRecycler.getLayoutManager();
            manager.onRestoreInstanceState(savedInstanceState.getParcelable(KEY_MANAGER_STATE));
        }

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        RecyclerView.LayoutManager manager = stepsRecycler.getLayoutManager();
        outState.putParcelable(KEY_MANAGER_STATE, manager.onSaveInstanceState());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof StepListListener) stepListListener = (StepListListener) context;
    }

    @Override
    public void onDetach() {
        BakingUtils.dispose(unbinder, toggleAnimation);
        stepListListener = null;
        super.onDetach();
    }

    @SuppressWarnings("ConstantConditions")
    private void setLists(){

        List<String> allIngredients = new ArrayList<>();
        List<String> collapsedIngredients = new ArrayList<>();

        for (int i = 0; i < recipe.getIngredients().size(); i++) {
            Ingredient ingredient = recipe.getIngredients().get(i);

            if (i < MAX_LIST_LINES_COLLAPSED && listToggleButton != null) {
                collapsedIngredients.add(BakingUtils.formatIngredientText(getContext(), ingredient));
            } else {
                allIngredients.add(BakingUtils.formatIngredientText(getContext(), ingredient));
            }
        }

        if (listViewExpanded != null) {
            collapsedAdapter = new ArrayAdapter<>(getContext(), R.layout.simple_ingredient_list_item,
                    collapsedIngredients);
        }

        expandedAdapter = new ArrayAdapter<>(getContext(), R.layout.simple_ingredient_list_item,
                allIngredients);

    }

    public interface StepListListener {
        void onStepClicked(Recipe recipe, int pos);
    }



    class RecipeStepAdapter extends RecyclerView.Adapter<RecipeStepAdapter.RecipeStepViewHolder> {

        List<Step> steps;
        StepListListener stepListListener;

        RecipeStepAdapter(StepListListener listener) {
            steps = new ArrayList<>();
            stepListListener = listener;
        }

        @NonNull
        @Override
        public RecipeStepViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.view_holder_steps_list, parent, false);
            return new RecipeStepViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull RecipeStepViewHolder holder, int position) {
            Step step = steps.get(position);

            if (position == 0) {
                holder.shortDescriptionTv.setText(step.getShortDescription());
            }
            else {
                holder.shortDescriptionTv.setText(String.format(
                        getString(R.string.steps_format_short),
                        position,
                        step.getShortDescription()));
            }

            if (position + 1 == steps.size()) {
                RecyclerView.LayoutParams params =
                        (RecyclerView.LayoutParams) holder.itemView.getLayoutParams();

                params.setMargins(
                        params.leftMargin,
                        params.topMargin,
                        params.rightMargin,
                        params.topMargin * 2);
                holder.itemView.setLayoutParams(params);
            }

            holder.itemView.setOnClickListener(v -> {
                BakingUtils.setUniqueTransitionName(holder.itemView, step);
                if (stepListListener != null) {
                    stepListListener.onStepClicked(recipe, position);
                }
            });
        }

        @Override
        public int getItemCount() {
            if (steps == null) return 0;
            return steps.size();
        }

        void setSteps(List<Step> steps) {
            this.steps = steps;
            notifyDataSetChanged();
        }

        class RecipeStepViewHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.steps_short_description_vh)
            TextView shortDescriptionTv;

            RecipeStepViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }


        }

    }

}