package com.brightcove.player.samples.audioonly;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.brightcove.player.edge.OfflineCallback;
import com.brightcove.player.logging.Log;
import com.brightcove.player.model.Video;
import com.brightcove.player.view.BrightcoveExoPlayerVideoView;

import java.util.ArrayList;
import java.util.List;


public class AdapterView extends RecyclerView.Adapter<AdapterView.ViewHolder> {

    private final List<Video> trackList = new ArrayList<>();
    private final String TAG = this.getClass().getSimpleName();
    private BrightcoveExoPlayerVideoView brightcoveVideoView;


    public AdapterView (BrightcoveExoPlayerVideoView videoView) {
        brightcoveVideoView = videoView;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //Get track information
        Video track = trackList == null ? null : trackList.get(position);

        if (track != null) {
            holder.trackTitleTextView.setText(track.getStringProperty(Video.Fields.NAME));
            try {
                holder.trackThumbnailView.setImageURI(Uri.parse(track.getPosterImage().toString()));
            } catch (NullPointerException nullPointerException){
                Log.v(TAG, "Couldn't load the poster for track:" + track.getId());
            }
            holder.track = track;
        }

        holder.trackThumbnailView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                try {
                    brightcoveVideoView.stopPlayback();
                    brightcoveVideoView.clear();
                    brightcoveVideoView.add(holder.track);
                    brightcoveVideoView.start();
                    return true;
                } catch (Exception e) {
                    Log.v(TAG, "Couldn't load track:" + track.getId());
                    return false;
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return trackList.size();
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
        this.trackList.clear();
        this.trackList.addAll(trackList);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final Context context;
        public final ImageView trackThumbnailView;
        public final TextView trackTitleTextView;
        public Video track;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            context = itemView.getContext();
            trackThumbnailView = (ImageView) itemView.findViewById(R.id.thumbnailImageView);
            trackTitleTextView = (TextView) itemView.findViewById(R.id.trackTitleTextView);
        }

    }
}
