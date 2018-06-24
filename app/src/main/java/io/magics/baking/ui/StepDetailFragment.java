package io.magics.baking.ui;

import android.annotation.SuppressLint;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.google.android.exoplayer2.ui.PlayerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.magics.baking.R;
import io.magics.baking.models.Ingredient;
import io.magics.baking.models.Step;
import io.magics.baking.utils.BakingUtils;


public class StepDetailFragment extends Fragment {

    private static final String ARG_STEP = "step";
    private static final String ARG_INGREDIENTS = "ingredients";

    @BindView(R.id.recipe_video_player)
    PlayerView playerView;
    @BindView(R.id.detail_ingredient_grid)
    GridView gridView;
    @BindView(R.id.step_description_header)
    TextView stepHeaderTv;
    @BindView(R.id.step_long_description)
    TextView stepDescriptionTv;


    private Step step;
    private List<Ingredient> ingredients;

    Unbinder unbinder;

    public StepDetailFragment() {
        // Required empty public constructor
    }

    public static StepDetailFragment newInstance(Step step, List<Ingredient> ingredients) {
        StepDetailFragment fragment = new StepDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_STEP, step);
        args.putParcelableArrayList(ARG_INGREDIENTS, (ArrayList<Ingredient>) ingredients);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            step = getArguments().getParcelable(ARG_STEP);
            ingredients = getArguments().getParcelableArrayList(ARG_INGREDIENTS);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_step_detail, container, false);
        unbinder = ButterKnife.bind(this, root);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (!ingredients.isEmpty()) gridView.setAdapter(new IngredientsGridAdapter());
        else gridView.setVisibility(View.GONE);

        playerView.setDefaultArtwork(BitmapFactory.decodeResource(getResources(),
                R.drawable.baking_logo_long));
        playerView.setUseArtwork(true);
        stepHeaderTv.setText(step.getShortDescription());
        stepDescriptionTv.setText(step.getDescription());
    }

    @Override
    public void onDestroyView() {
        BakingUtils.dispose(unbinder);
        super.onDestroyView();
    }


    class IngredientsGridAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return ingredients.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @SuppressLint("InflateParams")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            IngredientViewHolder viewHolder;

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.simple_view_holder_ingredient,
                        null);
                viewHolder = new IngredientViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else viewHolder = (IngredientViewHolder) convertView.getTag();

            viewHolder.setIngredientText(ingredients.get(position));

            return convertView;
        }

        class IngredientViewHolder {

            @BindView(R.id.ingredient_text)
            TextView ingredientText;



            IngredientViewHolder(View itemView) {
                ButterKnife.bind(this, itemView);
            }

            void setIngredientText(Ingredient ingredient) {
                Double quantity = ingredient.getQuantity();
                boolean plural = quantity > 1;
                String unit = BakingUtils.formatMeasure(ingredient.getMeasure(), plural);
                String ingredientDescription = ingredient.getRecipeIngredient();
                String stringQuantity = String.valueOf(quantity);

                if (stringQuantity.matches("[\\p{Digit}]+\\.0")){
                    stringQuantity = stringQuantity.substring(0, stringQuantity.indexOf('.'));
                }

                ingredientDescription = ingredientDescription
                        .substring(0, 1).toUpperCase()
                        + ingredientDescription.substring(1);

                if (unit != null) {
                    unit = unit.substring(0, 1).toUpperCase() +
                            unit.substring(1);
                    ingredientText.setText(String.format(getString(
                            R.string.ingredient_format_two_lines),
                            stringQuantity, unit, ingredientDescription));
                } else {
                    ingredientText.setText(String.format(getString(
                            R.string.ingredient_format_no_unit),
                            stringQuantity, ingredientDescription));
                }

            }
        }
    }


}
