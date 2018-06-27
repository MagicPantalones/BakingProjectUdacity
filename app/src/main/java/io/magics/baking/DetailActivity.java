package io.magics.baking;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.view.View;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.magics.baking.models.Recipe;
import io.magics.baking.models.Step;
import io.magics.baking.ui.RecipeIngredientsFragment;
import io.magics.baking.ui.StepDetailFragment;
import io.magics.baking.ui.ViewPagerFragment;
import io.magics.baking.utils.BakingUtils;
import timber.log.Timber;

public class DetailActivity extends AppCompatActivity implements
        RecipeIngredientsFragment.StepListListener{

    private static final String KEY_RECIPE = "recipe";

    private static final int CONTAINER_OVERVIEW = R.id.recipe_overview_container;
    private static final int CONTAINER_STEP = R.id.step_container;

    @BindView(R.id.baking_toolbar_detail)
    Toolbar toolbar;

    Recipe recipe;

    Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        unbinder = ButterKnife.bind(this);

        Timber.plant(new Timber.DebugTree());

        setSupportActionBar(toolbar);

        //noinspection ConstantConditions
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        if (getIntent().getExtras() != null) {
            recipe = getIntent().getExtras().getParcelable(KEY_RECIPE);
        } else if (savedInstanceState != null) {
            recipe = savedInstanceState.getParcelable(KEY_RECIPE);
        }

        RecipeIngredientsFragment fragOverview = RecipeIngredientsFragment.newInstance(recipe);

        Step step = recipe.getSteps().get(0);
        StepDetailFragment fragStep = StepDetailFragment.newInstance(step,
                BakingUtils.getStepIngredients(step, recipe));

        getSupportFragmentManager().beginTransaction()
                .add(CONTAINER_OVERVIEW, fragOverview)
                .add(CONTAINER_STEP, fragStep)
                .commit();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_RECIPE, recipe);
    }

    @Override
    protected void onDestroy() {
        BakingUtils.dispose(unbinder);
        super.onDestroy();
    }

    @Override
    public void onStepClicked(View view, Recipe recipe, int pos) {

            Step step = recipe.getSteps().get(pos);
            StepDetailFragment fragment = StepDetailFragment.newInstance(step,
                    BakingUtils.getStepIngredients(step, recipe));
            fragment.setUserVisibleHint(true);
            fragment.setEnterTransition(new Fade());

            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .addToBackStack(null)
                    .replace(CONTAINER_STEP, fragment)
                    .commit();
        }
}
