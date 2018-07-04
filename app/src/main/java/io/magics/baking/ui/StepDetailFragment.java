package io.magics.baking.ui;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

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
    private static final String ARG_READY = "play_when_ready";

    private static final String KEY_CURR_WINDOW = "currentWindow";
    private static final String KEY_PLAY_POSITION = "playbackPosition";
    private static final String KEY_CURR_VOLUME = "currentVolume";
    private static final String KEY_READY = "playWhenReady";
    private static final String KEY_STEP = "step";

    private static final String USER_AGENT = "baking-app";

    @BindView(R.id.recipe_video_player)
    PlayerView playerView;
    @Nullable @BindView(R.id.detail_ingredient_grid)
    GridView gridView;
    @Nullable @BindView(R.id.step_description_header)
    TextView stepHeaderTv;
    @Nullable @BindView(R.id.step_long_description)
    TextView stepDescriptionTv;
    @Nullable @BindView(R.id.no_description_image)
    ImageView noDescriptionIv;
    @BindView(R.id.no_video_placeholder)
    ImageView noVideoPlaceholder;
    @BindView(R.id.volume_on_btn)
    ImageButton volumeOnBtn;
    @BindView(R.id.volume_off_btn)
    ImageButton volumeOffBtn;
    @Nullable @BindView(R.id.frame_separator)
    View frameSeparator;
    @BindView(R.id.volume_wrapper)
    ViewGroup volumeWrapper;
    @BindView(R.id.replay_btn)
    ImageButton replayBtn;
    @BindView(R.id.buffering_pb)
    ProgressBar bufferingBar;
    @BindView(R.id.replay_btn_background)
    View replayBtnBackground;

    private Step step;
    private List<Ingredient> ingredients;
    private SimpleExoPlayer player;
    private PlayerListener playerListener;
    Unbinder unbinder;

    private boolean playWhenReady;
    private int currentWindow;
    private long playbackPos;
    private float currentVolume = -1.0f;

    enum Volume {
        VOLUME_ON,
        VOLUME_OFF
    }

    public StepDetailFragment() {
        // Required empty public constructor
    }

    public static StepDetailFragment newInstance(Step step, List<Ingredient> ingredients,
                                                 boolean playWhenReady) {
        StepDetailFragment fragment = new StepDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_STEP, step);
        args.putParcelableArrayList(ARG_INGREDIENTS, (ArrayList<Ingredient>) ingredients);
        args.putBoolean(ARG_READY, playWhenReady);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            step = getArguments().getParcelable(ARG_STEP);
            ingredients = getArguments().getParcelableArrayList(ARG_INGREDIENTS);
            playWhenReady = getArguments().getBoolean(ARG_READY);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_step_detail, container, false);
        unbinder = ButterKnife.bind(this, root);
        playerListener = new PlayerListener();

        if (savedInstanceState != null && savedInstanceState.getParcelable(KEY_STEP) != null) {
            playWhenReady = savedInstanceState.getBoolean(KEY_READY);
            currentWindow = savedInstanceState.getInt(KEY_CURR_WINDOW);
            playbackPos = savedInstanceState.getLong(KEY_PLAY_POSITION);
            currentVolume = savedInstanceState.getFloat(KEY_CURR_VOLUME);
            step = savedInstanceState.getParcelable(KEY_STEP);
        }

        return root;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (stepDescriptionTv != null) {
            if (!ingredients.isEmpty()) gridView.setAdapter(new IngredientsGridAdapter());
            else gridView.setVisibility(View.GONE);

            if ((int) step.getId() == 0) {
                stepDescriptionTv.setVisibility(View.GONE);
                stepHeaderTv.setVisibility(View.GONE);
                noDescriptionIv.setVisibility(View.VISIBLE);
            }

            stepHeaderTv.setText(step.getShortDescription());
            stepDescriptionTv.setText(step.getDescription());
        }

        volumeOnBtn.setOnClickListener(v -> toggleMute(Volume.VOLUME_OFF));
        volumeOffBtn.setOnClickListener(v -> toggleMute(Volume.VOLUME_ON));
        replayBtn.setOnClickListener(v -> playAgain());

    }



    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) initializePlayer();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Util.SDK_INT <= 23 || player == null) initializePlayer();
    }

    private void initializePlayer() {

        if (player == null) {

            player = ExoPlayerFactory.newSimpleInstance(
                    new DefaultRenderersFactory(getContext()),
                    new DefaultTrackSelector(),
                    new DefaultLoadControl()
            );

            playerView.setPlayer(player);
            playerView.setControllerAutoShow(false);

            if (currentVolume == -1.0f) {
                currentVolume = 1.0f;
            }

            player.setVolume(currentVolume);
            player.setPlayWhenReady(playWhenReady);
            player.seekTo(currentWindow, playbackPos);

            Uri movieUri;
            if (step.getVideoURL().isEmpty()) {
                movieUri = Uri.parse(step.getThumbnailURL());
            } else {
                movieUri = Uri.parse(step.getVideoURL());
            }
            player.addListener(playerListener);
            player.prepare(buildMediaSource(movieUri), false, false);
        }
    }

    private MediaSource buildMediaSource(Uri uri) {
        return new ExtractorMediaSource.Factory(new DefaultHttpDataSourceFactory(USER_AGENT))
                .createMediaSource(uri);
    }

    @Override
    public void onPause() {
        registerPlayerState();
        if (Util.SDK_INT <= 23) releasePlayer();
        super.onPause();
    }

    @Override
    public void onStop() {
        if (Util.SDK_INT > 23) releasePlayer();
        super.onStop();
    }

    //If I initialize @param playWhenReady as true the video will autoPlay in the offscreen fragments
    public void setPlayWhenReady(boolean playWhenReady) {
        this.playWhenReady = playWhenReady;
        if (player != null) player.setPlayWhenReady(this.playWhenReady);
    }

    private void toggleMute(Volume volume) {
        if (player == null) return;

        if (volume == Volume.VOLUME_OFF) {
            currentVolume = player.getVolume();
            player.setVolume(0f);
            volumeOnBtn.setVisibility(View.INVISIBLE);
            volumeOffBtn.setVisibility(View.VISIBLE);
        } else {
            player.setVolume(currentVolume);
            volumeOnBtn.setVisibility(View.VISIBLE);
            volumeOffBtn.setVisibility(View.INVISIBLE);
        }
    }

    private void playAgain() {
        if (player != null) {
            playWhenReady = true;
            player.seekTo(0);
            player.setPlayWhenReady(true);
            replayBtn.setVisibility(View.GONE);
            replayBtnBackground.setVisibility(View.GONE);
        }
    }

    private void releasePlayer() {
        if (player != null) {
            player.removeListener(playerListener);
            player.release();
            player = null;
            playerView.setPlayer(null);
        }
    }

    private void registerPlayerState() {
        playWhenReady = player.getPlayWhenReady();
        currentWindow = player.getCurrentWindowIndex();
        playbackPos = player.getContentPosition();
        currentVolume = player.getVolume();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_STEP, step);
        outState.putBoolean(KEY_READY, player == null ? playWhenReady : player.getPlayWhenReady());
        outState.putInt(KEY_CURR_WINDOW, player == null ? currentWindow :
                player.getCurrentWindowIndex());
        outState.putLong(KEY_PLAY_POSITION, player == null ? playbackPos :
                player.getContentPosition());
        outState.putFloat(KEY_CURR_VOLUME, player == null ? currentVolume : player.getVolume());
    }

    @Override
    public void onDetach() {
        BakingUtils.dispose(unbinder);
        super.onDetach();
    }


    private class PlayerListener extends SimpleExoPlayer.DefaultEventListener {

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            switch (playbackState) {
                case Player.STATE_BUFFERING:
                    if (playWhenReady) {
                        bufferingBar.setVisibility(View.VISIBLE);
                    }
                    break;
                case Player.STATE_READY:
                    if (noVideoPlaceholder.getVisibility() == View.VISIBLE) {
                        noVideoPlaceholder.setVisibility(View.GONE);
                        volumeWrapper.setVisibility(View.VISIBLE);
                        if (frameSeparator != null) frameSeparator.setVisibility(View.GONE);
                    }
                    if (playWhenReady && bufferingBar.getVisibility() == View.VISIBLE) {
                        bufferingBar.setVisibility(View.GONE);
                    }
                    playerView.showController();
                    break;
                case Player.STATE_ENDED:
                    playerView.hideController();
                    replayBtn.setVisibility(View.VISIBLE);
                    replayBtnBackground.setVisibility(View.VISIBLE);
                    if (bufferingBar.getVisibility() == View.VISIBLE) {
                        bufferingBar.setVisibility(View.GONE);
                    }
                    break;
                default:
                    //Do nothing for other states.
                    break;
            }
        }

        @Override
        public void onPlayerError(ExoPlaybackException error) {
            if (error.type == ExoPlaybackException.TYPE_SOURCE) {
                    playerView.setUseController(false);
                    volumeWrapper.setVisibility(View.GONE);
                    playerView.setShutterBackgroundColor(getResources()
                            .getColor(android.R.color.white));
                    noVideoPlaceholder.setVisibility(View.VISIBLE);
                    if (frameSeparator != null) frameSeparator.setVisibility(View.VISIBLE);
                }
            if (bufferingBar.getVisibility() == View.VISIBLE) bufferingBar.setVisibility(View.GONE);
            super.onPlayerError(error);
        }
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
                ingredientText.setText(BakingUtils.formatIngredientText(getContext(), ingredient));
            }
        }
    }


}
