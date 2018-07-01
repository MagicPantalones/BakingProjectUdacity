package io.magics.baking.ui;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.magics.baking.MainActivity;
import io.magics.baking.R;
import io.magics.baking.models.Ingredient;
import io.magics.baking.models.Recipe;
import io.magics.baking.models.Step;
import io.magics.baking.utils.BakingUtils;
import io.magics.baking.utils.NestedFragViewPager;

public class ViewPagerFragment extends Fragment {

    private static final String ARG_RECIPE = "recipe";

    @BindView(R.id.steps_view_pager)
    NestedFragViewPager viewPager;
    @Nullable @BindView(R.id.view_pager_prev)
    TextView prevTv;
    @Nullable @BindView(R.id.view_pager_next)
    TextView nextTv;
    @Nullable @BindView(R.id.view_pager_introduction)
    TextView pageTv;

    private Recipe recipe;
    private RecipePagerListener listener;

    Unbinder unbinder;

    public ViewPagerFragment() {
        // Required empty public constructor
    }

    public static ViewPagerFragment newInstance(Recipe recipe) {
        ViewPagerFragment fragment = new ViewPagerFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_RECIPE, recipe);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            recipe = getArguments().getParcelable(ARG_RECIPE);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(
                R.layout.fragment_viewpager, container, false);
        unbinder = ButterKnife.bind(this, root);

        return root;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        prepareLayoutForPage();

        StepsViewPagerAdapter adapter = new StepsViewPagerAdapter(getChildFragmentManager(), recipe);

        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(MainActivity.getStepIndex());

        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                MainActivity.setStepIndex(position);
                prepareLayoutForPage();
            }
        });

        if (prevTv != null) {
            if (listener != null) listener.onPortrait();
            prevTv.setOnClickListener(v -> {
                if (prevTv.isShown()) {
                    viewPager.setCurrentItem(viewPager.getCurrentItem() - 1, true);
                }
            });

            nextTv.setOnClickListener(v -> {
                if (nextTv.isShown()) {
                    viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
                }
            });
        } else if (listener != null) listener.onLandscape();
    }

    @SuppressWarnings("ConstantConditions")
    private void prepareLayoutForPage() {

        int stepNum = MainActivity.getStepIndex();

        if (stepNum == 0 && pageTv != null) {
            pageTv.setText(getString(R.string.view_pager_intro));
            prevTv.setVisibility(View.INVISIBLE);
        }
        else if (pageTv != null){
            pageTv.setText(String.format(getString(R.string.view_pager_page), stepNum));
            if (!prevTv.isShown()) prevTv.setVisibility(View.VISIBLE);
            if (stepNum == recipe.getSteps().size() - 1) nextTv.setVisibility(View.INVISIBLE);
            else if (!nextTv.isShown()) nextTv.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof RecipePagerListener) listener = (RecipePagerListener) context;
    }

    @Override
    public void onDetach() {
        BakingUtils.dispose(unbinder);
        listener = null;
        super.onDetach();
    }

    public interface RecipePagerListener {
        void onLandscape();
        void onPortrait();
    }



    class StepsViewPagerAdapter extends FragmentStatePagerAdapter {

        List<Step> stepList;

        StepsViewPagerAdapter(FragmentManager fm, Recipe recipe) {
            super(fm);
            stepList = recipe.getSteps();
        }

        @Override
        public Fragment getItem(int position) {
            Step step = stepList.get(position);
            List<Ingredient> ingredients = BakingUtils.getStepIngredients(step, recipe);
            return StepDetailFragment.newInstance(step, ingredients);
        }

        @Override
        public int getCount() {
            return stepList.size();
        }
    }

}
