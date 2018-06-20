package io.magics.baking;

import android.arch.lifecycle.ViewModelProviders;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.magics.baking.data.DataProvider;
import io.magics.baking.data.RecipeViewModel;
import io.magics.baking.ui.RecipeListFragment;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int FRAG_CONTAINER = R.id.container_main;

    Unbinder mainUnbinder;
    DataProvider dataProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainUnbinder = ButterKnife.bind(this);
        RecipeViewModel viewModel = ViewModelProviders.of(this).get(RecipeViewModel.class);

        Timber.plant(new Timber.DebugTree());
        if (dataProvider == null) {
            dataProvider = new DataProvider(this, viewModel);
        }

        Fragment fragment = getSupportFragmentManager().findFragmentById(FRAG_CONTAINER);

        if (fragment == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(FRAG_CONTAINER, RecipeListFragment.newInstance())
                    .addToBackStack(null)
                    .commit();
        }

    }
}
