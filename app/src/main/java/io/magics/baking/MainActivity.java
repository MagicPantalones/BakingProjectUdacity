package io.magics.baking;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

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



    }
}
