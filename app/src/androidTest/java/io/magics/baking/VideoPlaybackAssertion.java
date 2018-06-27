package io.magics.baking;

import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.ViewAssertion;
import android.view.View;

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;

import org.hamcrest.Matcher;

import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static com.google.android.exoplayer2.Player.STATE_BUFFERING;
import static com.google.android.exoplayer2.Player.STATE_READY;
import static org.hamcrest.Matchers.is;

//Got VideoPlaybackAssertion class to test ExoPlayer from:
//https://stackoverflow.com/questions/46016895/ambiguousviewmatcherexception-simpleexoplayerview-android
class VideoPlaybackAssertion implements ViewAssertion {

    private final Matcher<Boolean> matcher;

    public VideoPlaybackAssertion(Matcher<Boolean> matcher) {
        this.matcher = matcher;
    }

    public VideoPlaybackAssertion(Boolean expectedState) {
        this.matcher = is(expectedState);
    }

    @Override
    public void check(View view, NoMatchingViewException noViewFoundException) {
        if (noViewFoundException != null) {
            throw noViewFoundException;
        }

        PlayerView exoPlayerView = (PlayerView) view;
        SimpleExoPlayer exoPlayer = (SimpleExoPlayer) exoPlayerView.getPlayer();
        int state = exoPlayer.getPlaybackState();
        Boolean isPlaying;
        if ((state == STATE_BUFFERING) || (state == STATE_READY)) {
            isPlaying = true;
        } else {
            isPlaying = false;
        }
        assertThat(isPlaying, matcher);
    }

}