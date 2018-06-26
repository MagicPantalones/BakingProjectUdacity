package io.magics.baking.ui;

import android.annotation.SuppressLint;
import android.graphics.BitmapFactory;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.magics.baking.R;
import io.magics.baking.models.Ingredient;
import io.magics.baking.models.Step;
import io.magics.baking.utils.BakingUtils;
import timber.log.Timber;


public class StepDetailFragment extends Fragment {

    private static final String ARG_STEP = "step";
    private static final String ARG_INGREDIENTS = "ingredients";

    private static final String USER_AGENT = "baking-app";

    @BindView(R.id.recipe_video_player)
    PlayerView playerView;
    @BindView(R.id.detail_ingredient_grid)
    GridView gridView;
    @BindView(R.id.step_description_header)
    TextView stepHeaderTv;
    @BindView(R.id.step_long_description)
    TextView stepDescriptionTv;
    @BindView(R.id.no_description_image)
    ImageView noDescriptionIv;
    @BindView(R.id.no_video_placeholder)
    ImageView noVideoPlaceholder;

    private Step step;
    private List<Ingredient> ingredients;
    private SimpleExoPlayer player;
    private PlayerListener playerListener;
    Unbinder unbinder;

    private boolean playWhenReady = false;
    private int currentWindow;
    private long playbackPos;
    private float currentVolume;

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
        playerListener = new PlayerListener();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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
            player.prepare(buildMediaSource(movieUri), true, false);
        }
    }

    private MediaSource buildMediaSource(Uri uri) {
        return new ExtractorMediaSource.Factory(new DefaultHttpDataSourceFactory(USER_AGENT))
                .createMediaSource(uri);
    }

    @Override
    public void onPause() {
        if (Util.SDK_INT <= 23) releasePlayer();
        super.onPause();
    }

    @Override
    public void onStop() {
        if (Util.SDK_INT > 23) releasePlayer();
        super.onStop();
    }

    public void toggleMute(View v) {
        if (player == null) return;

        if (player.getVolume() != 0f) {
            currentVolume = player.getVolume();
            player.setVolume(0f);
        } else {
            player.setVolume(currentVolume);
        }
    }

    private void releasePlayer() {
        if (player != null) {
            playbackPos = player.getContentPosition();
            currentWindow = player.getCurrentWindowIndex();
            playWhenReady = player.getPlayWhenReady();
            currentVolume = player.getVolume();
            player.removeListener(playerListener);
            player.release();
            player = null;
            playerView.setPlayer(null);
        }
    }

    @Override
    public void onDestroyView() {
        BakingUtils.dispose(unbinder);
        super.onDestroyView();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (player != null) {
            player.setPlayWhenReady(isVisibleToUser);
        }
    }

    private class PlayerListener extends SimpleExoPlayer.DefaultEventListener {

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            String state;
            switch (playbackState) {
                case Player.STATE_IDLE:
                    state = "ExoPlayer.STATE_IDLE      -";
                    break;
                case Player.STATE_BUFFERING:
                    state = "ExoPlayer.STATE_BUFFERING -";
                    break;
                case Player.STATE_READY:
                    state = "ExoPlayer.STATE_READY     -";
                    break;
                case Player.STATE_ENDED:
                    state = "ExoPlayer.STATE_ENDED     -";
                    break;
                default:
                    state = "UNKNOWN_STATE             -";
                    break;
            }
            Timber.w(state);
        }

        @Override
        public void onPlayerError(ExoPlaybackException error) {
            if (error.type == ExoPlaybackException.TYPE_SOURCE) {
                if (error.getSourceException().getCause() instanceof UnknownHostException) {
                    Timber.e("No Internet!!! :D");
                } else if (error.getSourceException().getCause() instanceof MalformedURLException) {
                    Timber.e("No URL to connect to D:");
                }
            }
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
