package io.magics.baking;

import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;


import com.google.android.exoplayer2.ui.PlayerView;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import io.magics.baking.data.DataProvider;
import io.magics.baking.models.Recipe;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;

@RunWith(AndroidJUnit4.class)
public class RecipeListTest {

    private List<Recipe> recipeList;

    @Rule
    public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setUp() {
        recipeList = DataProvider.oneShot(activityTestRule.getActivity());
    }

    @Test
    public void recipeList_CheckNames() {

        for (Recipe recipe : recipeList) {
            if (recipe.getId() == 3) {
                onView(withId(R.id.recipe_list_recycler)).perform(RecyclerViewActions
                        .scrollToPosition(3));
            }
            onView(withId(R.id.recipe_list_recycler))
                    .check(matches(hasDescendant(withText(recipe.getName()))));
        }
    }

    @Test
    public void recipeList_OnClick() {
        onView(withId(R.id.recipe_list_recycler)).perform(RecyclerViewActions
                .actionOnItemAtPosition(0, click()));
        onView(withId(R.id.steps_ingredients_toggle)).perform(click());
        onView(withId(R.id.steps_list))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(allOf(instanceOf(PlayerView.class), withId(R.id.recipe_video_player),
                isCompletelyDisplayed()))
                .check(new VideoPlaybackAssertion(true));
    }

}

