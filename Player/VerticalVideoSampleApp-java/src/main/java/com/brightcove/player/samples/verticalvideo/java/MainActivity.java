package com.brightcove.player.samples.verticalvideo.java;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.brightcove.player.edge.Catalog;
import com.brightcove.player.edge.CatalogError;
import com.brightcove.player.edge.PlaylistListener;
import com.brightcove.player.event.EventEmitterImpl;
import com.brightcove.player.model.Playlist;

import java.util.List;

/**
 * A full-bleed vertical (TikTok / Reels style) paging video feed built on {@link ViewPager2}.
 *
 * Every page owns its own
 * {@link com.brightcove.player.view.BrightcoveExoPlayerVideoView}. {@link VerticalVideoAdapter}
 * is the single authority on which page is allowed to play: exactly one page is active at a
 * time, and a page that is scrolled away is paused and (once recycled) released. This is the
 * Android counterpart of the iOS {@code VerticalPlayer} sample.
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "VerticalVideo";
    private static final String KEY_ACTIVE_POSITION = "vertical_video_active_position";

    private ViewPager2 verticalPager;
    private ProgressBar loading;
    private VerticalVideoAdapter adapter;
    private Catalog catalog;
    private int restoredPosition;

    private final PlaylistListener playlistListener = new PlaylistListener() {
        @Override
        public void onPlaylist(@NonNull Playlist playlist) {
            onPlaylistLoaded(playlist);
        }

        @Override
        public void onError(@NonNull List<CatalogError> errors) {
            Log.e(TAG, "Failed to load playlist: " + errors);
            loading.setVisibility(View.GONE);
        }
    };

    private final ViewPager2.OnPageChangeCallback pageChangeCallback =
            new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    adapter.activate(position);
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vertical_video_sample);

        verticalPager = findViewById(R.id.vertical_pager);
        loading = findViewById(R.id.loading);
        adapter = new VerticalVideoAdapter();
        verticalPager.setAdapter(adapter);
        // Keep one page on each side attached so the neighboring poster is already visible
        // when a swipe starts. Adjacent pages never start playback — the adapter only
        // activates the selected page — so this pre-attaches UI, not decoders.
        verticalPager.setOffscreenPageLimit(1);
        verticalPager.registerOnPageChangeCallback(pageChangeCallback);

        // ViewPager2 cannot restore its own position here: the list is empty until the
        // Catalog request returns, so there is nothing to scroll to at restore time.
        restoredPosition = savedInstanceState != null
                ? savedInstanceState.getInt(KEY_ACTIVE_POSITION, 0) : 0;

        catalog = new Catalog.Builder(new EventEmitterImpl(), getString(R.string.sdk_demo_account))
                .setPolicy(getString(R.string.sdk_demo_policy))
                .build();

        catalog.findPlaylistByID(getString(R.string.sdk_demo_playlist_id), playlistListener);
    }

    private void onPlaylistLoaded(Playlist playlist) {
        List<com.brightcove.player.model.Video> videos = playlist.getVideos();
        if (videos == null || videos.isEmpty()) {
            Log.w(TAG, "Playlist is empty; nothing to play.");
            loading.setVisibility(View.GONE);
            return;
        }

        int target = Math.max(0, Math.min(restoredPosition, videos.size() - 1));
        // AsyncListDiffer installs the list asynchronously; activate only after it is
        // committed, otherwise itemCount is still 0 and activate() would bail out.
        adapter.submitList(videos, () -> {
            verticalPager.setCurrentItem(target, false);
            adapter.activate(target);
        });
        loading.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.resumeActive();
    }

    @Override
    protected void onPause() {
        // Never keep playing audio while the feed is not visible; the bridge does the
        // equivalent when a React Native screen loses focus.
        adapter.pauseActive();
        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt(KEY_ACTIVE_POSITION, verticalPager.getCurrentItem());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        verticalPager.unregisterOnPageChangeCallback(pageChangeCallback);
        verticalPager.setAdapter(null);
        adapter.deactivateAll();
        super.onDestroy();
    }
}
