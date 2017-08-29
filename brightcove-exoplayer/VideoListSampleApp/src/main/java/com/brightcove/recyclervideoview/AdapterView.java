package com.brightcove.recyclervideoview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.brightcove.player.event.Event;
import com.brightcove.player.event.EventEmitter;
import com.brightcove.player.event.EventListener;
import com.brightcove.player.event.EventType;
import com.brightcove.player.model.Video;
import com.brightcove.player.view.BrightcoveExoPlayerVideoView;
import com.brightcove.player.view.BrightcoveVideoView;

import java.util.ArrayList;
import java.util.List;


public class AdapterView extends RecyclerView.Adapter<AdapterView.ViewHolder> {

    private final List<Video> videoList = new ArrayList<>();

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //Get Video information
        Video video = videoList == null ? null : videoList.get(position);
        if (video != null) {
            holder.videoTitleText.setText(video.getStringProperty(Video.Fields.NAME));
            BrightcoveVideoView videoView = holder.videoView;
            videoView.clear();
            videoView.add(video);
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
        holder.videoView.start();
    }

    @Override
    public void onViewDetachedFromWindow(ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.videoView.stopPlayback();
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        int childCount = recyclerView.getChildCount();
        //We need to stop the player to avoid a potential memory leak.
        for (int i = 0; i < childCount; i++) {
            ViewHolder holder = (ViewHolder) recyclerView.findViewHolderForAdapterPosition(i);
            if (holder != null && holder.videoView != null) {
                holder.videoView.stopPlayback();
            }
        }
    }

    public void setVideoList(@Nullable List<Video> videoList) {
        this.videoList.clear();
        this.videoList.addAll(videoList);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        public final Context context;
        public final TextView videoTitleText;
        public final FrameLayout videoFrame;
        public final BrightcoveVideoView videoView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            context = itemView.getContext();
            videoFrame = (FrameLayout) itemView.findViewById(R.id.video_frame);
            videoTitleText = (TextView) itemView.findViewById(R.id.video_title_text);
            videoView = new BrightcoveExoPlayerVideoView(context);
            videoFrame.addView(videoView);
            videoView.finishInitialization();

            EventEmitter eventEmitter = videoView.getEventEmitter();
            eventEmitter.on(EventType.ENTER_FULL_SCREEN, new EventListener() {
                @Override
                public void processEvent(Event event) {
                    //You can set listeners on each Video View
                }
            });
        }
    }
}
