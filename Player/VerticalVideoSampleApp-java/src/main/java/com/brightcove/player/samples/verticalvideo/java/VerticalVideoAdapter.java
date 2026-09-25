package com.brightcove.player.samples.verticalvideo.java;

import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.brightcove.player.event.AbstractEvent;
import com.brightcove.player.event.Event;
import com.brightcove.player.event.EventType;
import com.brightcove.player.mediacontroller.BrightcoveMediaController;
import com.brightcove.player.model.Video;
import com.brightcove.player.view.BrightcoveExoPlayerVideoView;

import coil3.SingletonImageLoader;
import coil3.request.ImageRequest;
import coil3.request.ImageRequestsKt;
import coil3.target.ImageViewTarget;

/**
 * A vertical {@link ListAdapter} where each page hosts its own Brightcove player.
 *
 * <p>Playback ownership is explicit and lives in this adapter, not in the holder:
 *
 * <ul>
 *   <li>{@link #activate(int)} is the single entry point that decides which page may play.
 *       It is driven by {@link androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback}
 *       and is idempotent, so a late bind / selection race cannot start two players.</li>
 *   <li>Only the active page ever queues its {@link Video} into the SDK; adjacent pages just
 *       show their poster, so a swipe does not spin up decoders or spend bandwidth on the
 *       pages either side of the one being watched.</li>
 *   <li>A page that leaves the active slot has its player cleared and its listeners
 *       unregistered, and every recycled holder destroys its player, so offscreen pages do
 *       not leak decoders.</li>
 *   <li>A holder's playback listeners exist only while that holder is the active page, and
 *       each listener additionally ignores an {@link Event} that names a different
 *       {@link Video} than the one the holder is bound to, so a callback that resolves after
 *       the holder was rebound is ignored.</li>
 * </ul>
 *
 * <p>The aspect-fill / crop behavior iOS gets from {@code .resizeAspectFill} is applied here
 * through {@code renderView.zoomIn()} — the same call the React Native bridge uses for
 * {@code videoScalingMode="fill"}.
 */
public class VerticalVideoAdapter extends ListAdapter<Video, VerticalVideoAdapter.PageHolder> {

    private static final String TAG = "VerticalVideo";

    /** Currently attached holders by adapter position. ViewPager2 owns the RecyclerView. */
    private final SparseArray<PageHolder> attachedHolders = new SparseArray<>();

    /** The adapter position currently allowed to play, or {@link RecyclerView#NO_POSITION}. */
    private int activePosition = RecyclerView.NO_POSITION;

    /** The holder currently allowed to play. Kept directly so teardown can always release it. */
    private PageHolder activeHolder;

    /** Whether the feed is resumed; a paused feed must not autoplay when a page attaches. */
    private boolean isResumed = true;

    /** Whether the active page was playing when the host went to background. */
    private boolean wasPlayingBeforeBackground;

    public VerticalVideoAdapter() {
        super(DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public PageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_vertical_video, parent, false);
        return new PageHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PageHolder holder, int position) {
        attachedHolders.put(position, holder);
        holder.bind(getItem(position));
        reconcile(holder, position);
    }

    @Override
    public void onViewAttachedToWindow(@NonNull PageHolder holder) {
        super.onViewAttachedToWindow(holder);
        int position = holder.getBindingAdapterPosition();
        if (position != RecyclerView.NO_POSITION) {
            attachedHolders.put(position, holder);
        }
        reconcile(holder, position);
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull PageHolder holder) {
        super.onViewDetachedFromWindow(holder);
        int position = holder.getBindingAdapterPosition();
        removeHolder(holder, position);
        // An attached-but-offscreen page must not keep playing or hold the decoder.
        if (holder != activeHolder) {
            holder.deactivate();
        }
    }

    @Override
    public void onViewRecycled(@NonNull PageHolder holder) {
        super.onViewRecycled(holder);
        int position = holder.getBindingAdapterPosition();
        removeHolder(holder, position);
        if (holder == activeHolder) {
            activeHolder = null;
        }
        // A recycled holder can never be the visible page; always tear the player down.
        holder.release();
    }

    /**
     * Drops {@code holder} from the attached-holder map. When the adapter position is already
     * {@link RecyclerView#NO_POSITION} (the common case by recycle time) the map is scanned so
     * a stale holder cannot pin its decoder through the map.
     */
    private void removeHolder(PageHolder holder, int position) {
        if (position != RecyclerView.NO_POSITION && attachedHolders.get(position) == holder) {
            attachedHolders.remove(position);
            return;
        }
        for (int i = attachedHolders.size() - 1; i >= 0; i--) {
            if (attachedHolders.valueAt(i) == holder) {
                attachedHolders.removeAt(i);
            }
        }
    }

    /**
     * Declares {@code position} as the only page allowed to play. Safe to call with an
     * out-of-range position (e.g. before the list arrives) or repeatedly; the previous active
     * page is always deactivated first.
     */
    public void activate(int position) {
        if (position < 0 || position >= getItemCount()) {
            deactivateAll();
            return;
        }

        if (position != activePosition) {
            if (activeHolder != null) {
                activeHolder.deactivate();
                activeHolder = null;
            }
        }
        activePosition = position;

        PageHolder holder = attachedHolders.get(position);
        if (holder == null) {
            return;
        }
        activeHolder = holder;
        if (isResumed) {
            holder.play();
        } else {
            holder.deactivate();
        }
    }

    public void pauseActive() {
        isResumed = false;
        // Backgrounding is not a page change: preserve the queue and playhead so a
        // playing page can resume where it stopped. Also remember a user-paused page
        // so returning to the app does not start it behind their back.
        wasPlayingBeforeBackground = activeHolder != null && activeHolder.pauseForBackground();
    }

    public void resumeActive() {
        isResumed = true;
        if (wasPlayingBeforeBackground && activeHolder != null) {
            activeHolder.resumeFromBackground();
        }
        wasPlayingBeforeBackground = false;
    }

    public void deactivateAll() {
        if (activeHolder != null) {
            activeHolder.release();
            activeHolder = null;
        }
        activePosition = RecyclerView.NO_POSITION;
    }

    private void reconcile(PageHolder holder, int position) {
        if (position == activePosition && isResumed) {
            activeHolder = holder;
            holder.play();
        } else if (holder != activeHolder) {
            holder.deactivate();
        }
    }

    public class PageHolder extends RecyclerView.ViewHolder {

        private final BrightcoveExoPlayerVideoView videoView;
        private final ImageView poster;
        private final ImageView playIcon;
        private final TextView videoDescription;

        /** The video this holder is currently bound to; guards against stale callbacks. */
        private Video boundVideo;

        /** Whether {@link #boundVideo} has been queued into the SDK player. */
        private boolean queued;

        private int readyListener = -1;
        private int playListener = -1;
        private int pauseListener = -1;
        private int completeListener = -1;
        private int errorListener = -1;
        private boolean listenersRegistered;

        PageHolder(View itemView) {
            super(itemView);
            videoView = itemView.findViewById(R.id.brightcove_video_view);
            poster = itemView.findViewById(R.id.poster);
            playIcon = itemView.findViewById(R.id.play_icon);
            videoDescription = itemView.findViewById(R.id.video_description);

            // The video view is inflated from XML, so its onFinishInflate already ran
            // finishInitialization(); calling it again would double-register controllers.
            //
            // Suppress the SDK's default media controller (play/pause, seek bar, captions,
            // fullscreen). A TikTok-style feed uses tap-to-toggle plus the overlay defined
            // here, not the stock control bar; the fullscreen button in particular has no
            // meaning inside a paged full-screen layout.
            videoView.setMediaController((BrightcoveMediaController) null);

            itemView.setOnClickListener(v -> togglePlayPause());
        }

        void bind(Video video) {
            if (boundVideo != null && boundVideo.getId() != null
                    && boundVideo.getId().equals(video.getId())) {
                return;
            }

            boundVideo = video;
            queued = false;
            videoView.clear();

            videoDescription.setText(video.getName() != null
                    ? video.getName() : video.getDescription());
            cancelPosterAnimation();
            updatePlayIndicator(false);
            poster.setAlpha(1f);

            java.net.URI still = video.getPosterImage() != null
                    ? video.getPosterImage() : video.getStillImageUri();
            if (still != null) {
                ImageRequest.Builder builder = new ImageRequest.Builder(itemView.getContext())
                        .data(still.toString())
                        .target(new ImageViewTarget(poster));
                ImageRequestsKt.crossfade(builder, true);
                SingletonImageLoader.get(itemView.getContext()).enqueue(builder.build());
            }
            applyAspectFill();
        }

        /** Queue (once), register this page's listeners, and start playback. */
        void play() {
            Video video = boundVideo;
            if (video == null) {
                return;
            }
            if (!queued) {
                registerPlaybackListeners();
                // Clear first so the queue always holds exactly this page's video. Without
                // it, replay-after-completion would append a duplicate (ExoMediaPlayback.add
                // appends at the end of the playlist).
                videoView.clear();
                videoView.add(video);
                queued = true;
            }
            applyAspectFill();
            videoView.start();
        }

        /**
         * Pause for host backgrounding without discarding the queue or playhead, so the
         * page can resume where it stopped. Returns whether it was playing.
         */
        boolean pauseForBackground() {
            boolean wasPlaying = videoView.isPlaying();
            videoView.pause();
            updatePlayIndicator(false);
            return wasPlaying;
        }

        /** Resume a page that was playing before the host was backgrounded. */
        void resumeFromBackground() {
            if (!queued) {
                return;
            }
            applyAspectFill();
            videoView.start();
        }

        /** Stop playback and free the decoder while keeping the poster for a later re-play. */
        void deactivate() {
            videoView.pause();
            videoView.clear();
            unregisterPlaybackListeners();
            queued = false;
            cancelPosterAnimation();
            poster.setAlpha(1f);
            updatePlayIndicator(false);
        }

        /** Tear the native player down entirely; called only when the holder is recycled. */
        void release() {
            deactivate();
            if (videoView.getPlayback() != null) {
                videoView.getPlayback().destroyPlayer();
            }
            boundVideo = null;
        }

        private void togglePlayPause() {
            if (videoView.isPlaying()) {
                videoView.pause();
                updatePlayIndicator(false);
            } else {
                play();
            }
        }

        /** Show the play glyph when paused and hide it while playing. */
        private void updatePlayIndicator(boolean playing) {
            playIcon.setImageResource(R.drawable.ic_play_arrow_white_24dp);
            playIcon.setAlpha(playing ? 0f : 1f);
        }

        /**
         * Uses the render surface's zoom to crop the video to the viewport, matching iOS
         * {@code .resizeAspectFill}. This is the surface fit/crop switch, not the
         * pinch-gesture ZoomController.
         */
        private void applyAspectFill() {
            if (videoView.getRenderView() != null) {
                videoView.getRenderView().zoomIn();
            }
        }

        private void registerPlaybackListeners() {
            if (listenersRegistered) {
                return;
            }
            readyListener = videoView.getEventEmitter().on(EventType.READY_TO_PLAY,
                    e -> { if (isForBoundVideo(e)) hidePoster(); });
            playListener = videoView.getEventEmitter().on(EventType.DID_PLAY,
                    e -> { if (isForBoundVideo(e)) onDidPlay(); });
            pauseListener = videoView.getEventEmitter().on(EventType.DID_PAUSE,
                    e -> { if (isForBoundVideo(e)) onDidPause(); });
            completeListener = videoView.getEventEmitter().on(EventType.COMPLETED,
                    e -> { if (isForBoundVideo(e)) onCompleted(); });
            errorListener = videoView.getEventEmitter().on(EventType.ERROR,
                    e -> { if (isForBoundVideo(e)) onError(e); });
            listenersRegistered = true;
        }

        private void unregisterPlaybackListeners() {
            if (!listenersRegistered) {
                return;
            }
            off(EventType.READY_TO_PLAY, readyListener);
            off(EventType.DID_PLAY, playListener);
            off(EventType.DID_PAUSE, pauseListener);
            off(EventType.COMPLETED, completeListener);
            off(EventType.ERROR, errorListener);
            listenersRegistered = false;
        }

        private void off(String type, int token) {
            if (token != -1) {
                videoView.getEventEmitter().off(type, token);
            }
        }

        /**
         * True when {@code event} belongs to the video this holder is currently bound to. The
         * SDK dispatches events asynchronously, so a page can be rebound (or released) before a
         * previously emitted event is handled; those must not paint this page.
         */
        private boolean isForBoundVideo(Event event) {
            Video bound = boundVideo;
            if (bound == null) {
                return false;
            }
            Object eventVideo = event.getProperties().get(AbstractEvent.VIDEO);
            if (eventVideo instanceof Video) {
                Video v = (Video) eventVideo;
                return v == bound || (v.getId() != null && v.getId().equals(bound.getId()));
            }
            // Source-selection errors can arrive before currentVideo is assigned. Accept an
            // unscoped event only for the holder that currently owns playback and has queued
            // this video; deactivated holders have no listeners, so a stale event cannot pass.
            Video current = videoView.getPlayback() != null
                    ? videoView.getPlayback().getCurrentVideo() : null;
            if (current != null) {
                return current.getId() != null && current.getId().equals(bound.getId());
            }
            return this == activeHolder && queued;
        }

        private void cancelPosterAnimation() {
            poster.animate().cancel();
        }

        private void hidePoster() {
            poster.animate().alpha(0f).setDuration(250).start();
        }

        private void onDidPlay() {
            playIcon.setAlpha(0f);
            poster.setAlpha(0f);
        }

        private void onDidPause() {
            if (videoView.isPlaying()) {
                return;
            }
            playIcon.setAlpha(1f);
        }

        private void onCompleted() {
            // The queue is spent; re-queuing on the next tap restarts from the beginning.
            queued = false;
            cancelPosterAnimation();
            poster.setAlpha(1f);
            playIcon.setImageResource(R.drawable.ic_play_arrow_white_24dp);
            playIcon.setAlpha(1f);
        }

        private void onError(Event event) {
            Log.e(TAG, "Playback error on '"
                    + (boundVideo != null ? boundVideo.getName() : "<none>")
                    + "': " + event.getProperties());
            // Reset the queue so a tap re-adds the source and retries, instead of
            // start()ing against the failed source. Keep the poster visible.
            videoView.clear();
            queued = false;
            cancelPosterAnimation();
            poster.setAlpha(1f);
            updatePlayIndicator(false);
        }
    }

    private static final DiffUtil.ItemCallback<Video> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Video>() {
                @Override
                public boolean areItemsTheSame(@NonNull Video oldItem, @NonNull Video newItem) {
                    return oldItem.getId() != null && oldItem.getId().equals(newItem.getId());
                }

                @Override
                public boolean areContentsTheSame(@NonNull Video oldItem, @NonNull Video newItem) {
                    return java.util.Objects.equals(oldItem.getId(), newItem.getId())
                            && java.util.Objects.equals(oldItem.getName(), newItem.getName())
                            && java.util.Objects.equals(oldItem.getDescription(), newItem.getDescription())
                            && java.util.Objects.equals(oldItem.getPosterImage(), newItem.getPosterImage());
                }
            };
}
