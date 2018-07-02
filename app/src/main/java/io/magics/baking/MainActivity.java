package io.magics.baking;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.view.View;
import android.view.WindowManager;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.magics.baking.data.DataProvider;
import io.magics.baking.data.RecipeIntentService;
import io.magics.baking.data.RecipeViewModel;
import io.magics.baking.models.Recipe;
import io.magics.baking.ui.RecipeIngredientsFragment;
import io.magics.baking.ui.RecipesListFragment;
import io.magics.baking.ui.ViewPagerFragment;
import io.magics.baking.utils.BakingUtils;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements RecipesListFragment.RecipeListener,
        RecipeIngredientsFragment.StepListListener, ViewPagerFragment.RecipePagerListener {


    private static final int FRAG_CONTAINER = R.id.container_main;
    private static final String KEY_RECIPE = "recipe";
    private static final String KEY_STEP_INDEX = "step_index";

    private static int stepIndex;

    @BindView(R.id.baking_toolbar_main)
    Toolbar toolbar;
    @Nullable @BindView(R.id.screen_size_checker)
    View screenSizeCheckView;

    Unbinder mainUnbinder;
    DataProvider dataProvider;
    boolean twoPane;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainUnbinder = ButterKnife.bind(this);
        RecipeViewModel viewModel = ViewModelProviders.of(this).get(RecipeViewModel.class);
        Timber.plant(new Timber.DebugTree());

        //Used a custom toolbar to get the wanted toolbar style
        setSupportActionBar(toolbar);

        //noinspection ConstantConditions
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //Made a simple DataProvider class.
        if (dataProvider == null) {
            dataProvider = new DataProvider(viewModel);
        }

        twoPane = screenSizeCheckView != null;

        if (DataProvider.isInternetConnected(this)) {
            dataProvider.init();
        }

        Fragment fragment = getSupportFragmentManager().findFragmentById(FRAG_CONTAINER);

        if (fragment == null) {
            RecipesListFragment frag = RecipesListFragment.newInstance(twoPane);

            frag.setEnterTransition(new Fade());
            frag.setReenterTransition(new Fade());

            getSupportFragmentManager().beginTransaction()
                    .add(FRAG_CONTAINER, frag)
                    .commit();
        }

        if (getIntent().getParcelableExtra(KEY_RECIPE) != null) {
            Recipe recipe = getIntent().getParcelableExtra(KEY_RECIPE);
            onRecipeClick(recipe);
        }

        if (savedInstanceState != null) {
            setStepIndex(savedInstanceState.getInt(KEY_STEP_INDEX, 0));
        }

    }


    @Override
    public void onBackPressed() {

        int fragCount = getSupportFragmentManager().getBackStackEntryCount();

        if (fragCount == 0) {
            super.onBackPressed();
        } else {
            getSupportFragmentManager().popBackStack();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_STEP_INDEX, stepIndex);
    }

    @Override
    protected void onDestroy() {
        BakingUtils.dispose(mainUnbinder);
        dataProvider.dispose();
        super.onDestroy();
    }

    @Override
    public void onRecipeClick(Recipe recipe) {

        RecipeIntentService.startActionUpdateRecipeWidget(this,
                (int) recipe.getId() - 1);

        if (twoPane) {
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra(KEY_RECIPE, recipe);
            startActivity(intent);
        } else {
            RecipeIngredientsFragment fragment = RecipeIngredientsFragment.newInstance(recipe);
            fragment.setEnterTransition(new Fade());
            fragment.setReenterTransition(new Fade());
            getSupportFragmentManager().beginTransaction()
                    .replace(FRAG_CONTAINER, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    public void onStepClicked(View view, Recipe recipe, int pos) {

        if (!twoPane) {
            setStepIndex(pos);

            ViewPagerFragment fragment = ViewPagerFragment.newInstance(recipe);
            fragment.setEnterTransition(new Fade());
            fragment.setReenterTransition(new Fade());

            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .addToBackStack(null)
                    .replace(FRAG_CONTAINER, fragment)
                    .commit();
        }
    }

    public static int getStepIndex() { return stepIndex; }

    public static void setStepIndex(int newIndex) { stepIndex = newIndex; }

    @Override
    public void onLandscape() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    public void onPortrait() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.show();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
    }
}
