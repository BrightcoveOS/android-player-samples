package com.brightcove.player.samples.audioonly.java;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.brightcove.player.model.Video;
import com.brightcove.player.view.BrightcoveExoPlayerVideoView;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import coil3.SingletonImageLoader;
import coil3.request.ImageRequest;
import coil3.target.ImageViewTarget;


/**
 * RecyclerView adapter that lists the audio tracks in the playlist; tapping a row
 * switches the {@link BrightcoveExoPlayerVideoView} to that track.
 */
public class VideoListAdapter extends RecyclerView.Adapter<VideoListAdapter.ViewHolder> {

    private static final String TAG = VideoListAdapter.class.getSimpleName();
    private final BrightcoveExoPlayerVideoView brightcoveVideoView;
    private final List<Video> videoList = new ArrayList<>();

    public VideoListAdapter(BrightcoveExoPlayerVideoView videoView) {
        brightcoveVideoView = videoView;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Video video = videoList.get(position);

        if (video != null) {
            holder.videoTitleTextView.setText(video.getStringProperty(Video.Fields.NAME));
            holder.videoDescriptionTextView.setText(video.getStringProperty(Video.Fields.DESCRIPTION));

            URI imageUri = video.getStillImageUri();
            if (imageUri == null) {
                holder.videoThumbnailView.setImageResource(R.drawable.cover_art_default);
                Log.v(TAG, "Null thumbnail:" + video.getId());
            } else {
                ImageRequest request = new ImageRequest.Builder(holder.context)
                        .data(imageUri.toASCIIString())
                        .target(new ImageViewTarget(holder.videoThumbnailView))
                        .build();
                SingletonImageLoader.get(holder.context).enqueue(request);
            }

            holder.video = video;
            holder.itemLayout.setOnClickListener((v) -> {
                try {
                    brightcoveVideoView.stopPlayback();
                    brightcoveVideoView.setCurrentIndex(holder.getAbsoluteAdapterPosition());
                    brightcoveVideoView.start();
                } catch (Exception e) {
                    Log.e(TAG, "Error loading media:" + video.getId(), e);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public void setVideoList(@Nullable List<Video> trackList) {
        this.videoList.clear();
        this.videoList.addAll(trackList);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final Context context;
        public final ImageView videoThumbnailView;
        public final TextView videoTitleTextView;
        public final TextView videoDescriptionTextView;
        public LinearLayout itemLayout;
        public Video video;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            context = itemView.getContext();
            videoThumbnailView = itemView.findViewById(R.id.thumbnailImageView);
            videoTitleTextView = itemView.findViewById(R.id.titleTextView);
            videoDescriptionTextView = itemView.findViewById(R.id.descriptionTextView);

            itemLayout = itemView.findViewById(R.id.linerarLayoutItem);
        }

    }
}
