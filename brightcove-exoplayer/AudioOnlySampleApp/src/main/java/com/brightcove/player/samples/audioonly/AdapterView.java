package com.brightcove.player.samples.audioonly;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.brightcove.player.logging.Log;
import com.brightcove.player.model.Video;
import com.brightcove.player.view.BrightcoveExoPlayerVideoView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class AdapterView extends RecyclerView.Adapter<AdapterView.ViewHolder> {

    private final String TAG = this.getClass().getSimpleName();
    private final BrightcoveExoPlayerVideoView brightcoveVideoView;
    private final List<Video> videoList = new ArrayList<>();


    public AdapterView (BrightcoveExoPlayerVideoView videoView) {
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
        //Get track information
        Video video = videoList.get(position);

        if (video != null) {
            holder.videoTitleTextView.setText(video.getStringProperty(Video.Fields.NAME));
            try {
                holder.videoThumbnailView.setImageURI(Uri.parse(video.getPosterImage().toString()));
            } catch (NullPointerException nullPointerException){
                Log.v(TAG, "Couldn't load the poster for track:" + video.getId());
            }
            holder.video = video;

            holder.itemLayout.setOnTouchListener((v, event) -> {
                try {
                    brightcoveVideoView.stopPlayback();
                    brightcoveVideoView.setCurrentIndex(holder.getAbsoluteAdapterPosition());
                    brightcoveVideoView.start();
                    return true;
                } catch (Exception e) {
                    Log.v(TAG, "Couldn't load track:" + video.getId());
                    return false;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onViewAttachedToWindow(ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
    }

    @Override
    public void onViewDetachedFromWindow(ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
    }

    public void setVideoList(@Nullable List<Video> trackList) {
        this.videoList.clear();
        this.videoList.addAll(trackList);
        notifyDataSetChanged();
    }

    public void shuffleList(){
        Collections.shuffle(videoList);
        notifyDataSetChanged();
    }

    public void repeat(){

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final Context context;
        public final ImageView videoThumbnailView;
        public final TextView videoTitleTextView;
        public LinearLayout itemLayout;
        public Video video;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            context = itemView.getContext();
            videoThumbnailView = itemView.findViewById(R.id.thumbnailImageView);
            videoTitleTextView = itemView.findViewById(R.id.titleTextView);
            itemLayout = itemView.findViewById(R.id.linerarLayoutItem);
        }

    }
}
