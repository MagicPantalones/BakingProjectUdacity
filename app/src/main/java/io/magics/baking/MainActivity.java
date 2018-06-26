package io.magics.baking;

import android.arch.lifecycle.ViewModelProviders;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.magics.baking.data.DataProvider;
import io.magics.baking.data.RecipeViewModel;
import io.magics.baking.models.Recipe;
import io.magics.baking.ui.RecipeIngredientsFragment;
import io.magics.baking.ui.RecipesListFragment;
import io.magics.baking.ui.ViewPagerFragment;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements RecipesListFragment.RecipeListener,
        RecipeIngredientsFragment.StepListListener, ViewPagerFragment.RecipePagerListener {

    //Todo Transitions
    //Todo Widget
    //Todo OrientationLayout & TabletLayout

    private static final int FRAG_CONTAINER = R.id.container_main;

    private static int stepIndex;

    @BindView(R.id.baking_toolbar_main)
    Toolbar toolbar;

    Unbinder mainUnbinder;
    DataProvider dataProvider;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainUnbinder = ButterKnife.bind(this);
        RecipeViewModel viewModel = ViewModelProviders.of(this).get(RecipeViewModel.class);
        Timber.plant(new Timber.DebugTree());

        setSupportActionBar(toolbar);

        //noinspection ConstantConditions
        getSupportActionBar().setDisplayShowTitleEnabled(false);



        if (dataProvider == null) {
            dataProvider = new DataProvider(this, viewModel);
        }

        dataProvider.init();

        Fragment fragment = getSupportFragmentManager().findFragmentById(FRAG_CONTAINER);

        if (fragment == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(FRAG_CONTAINER, RecipesListFragment.newInstance())
                    .commit();
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
    public void onRecipeClick(Recipe recipe) {
        RecipeIngredientsFragment fragment = RecipeIngredientsFragment.newInstance(recipe);
        getSupportFragmentManager().beginTransaction()
                .replace(FRAG_CONTAINER, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onStepClicked(View view, Recipe recipe, int pos) {
        setStepIndex(pos);

        ViewPagerFragment fragment = ViewPagerFragment.newInstance(recipe);


        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .addSharedElement(view, view.getTransitionName())
                .addToBackStack(null)
                .replace(FRAG_CONTAINER, fragment)
                .commit();

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
