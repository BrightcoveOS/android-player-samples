package com.brightcove.player.samples.cast.basic;

import android.annotation.SuppressLint;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.brightcove.player.model.Video;
import com.squareup.picasso.Picasso;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.brightcove.player.samples.cast.basic.VideoPlayerActivity.PROPS_SHORT_DESCRIPTION;

public class VideoListAdapter extends RecyclerView.Adapter<VideoListAdapter.ViewHolder> {

    /**
     * The current list of videos.
     */
    private List<Video> videoList;

    /**
     * A map of video identifiers and position of video in the list.
     */
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private Map<String, Integer> indexMap = new HashMap<>();

    private final ItemClickListener clickListener;
    public interface ItemClickListener {
        void itemClicked(View view, Video video, int position);
    }

    VideoListAdapter(ItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_item_view, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final Video video = videoList.get(position);
        holder.videoTitleText.setText(video.getName());

        Object descriptionObj = video.getProperties().get(PROPS_SHORT_DESCRIPTION);
        if (descriptionObj instanceof String) {
            holder.videoDescriptionText.setText((String) descriptionObj);
        }

        int duration = video.getDuration();
        if (duration > 0) {
            holder.videoDurationText.setText(millisecondsToString(duration));
            holder.videoDurationText.setVisibility(View.VISIBLE);
        } else {
            holder.videoDurationText.setText(null);
            holder.videoDurationText.setVisibility(View.GONE);
        }

        URI imageUri = video.getStillImageUri();
        if (imageUri == null) {
            holder.videoThumbnailImage.setImageResource(R.drawable.movie);
        } else {
            Picasso.with(holder.itemView.getContext()).load(imageUri.toASCIIString()).into(holder.videoThumbnailImage);
        }

        holder.videoThumbnailImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickListener.itemClicked(view, video, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return videoList == null ? 0 : videoList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        /**
         * Reference to the thumbnail image view.
         */
        final ImageView videoThumbnailImage;
        /**
         * Reference to the video title view.
         */
        final TextView videoTitleText;
        /**
         * Reference to the video status information text view.
         */
        final TextView videoDescriptionText;
        /**
         * Reference to the video duration view.
         */
        final TextView videoDurationText;

        ViewHolder(View itemView) {
            super(itemView);
            videoThumbnailImage = itemView.findViewById(R.id.video_thumbnail_image);
            videoTitleText = itemView.findViewById(R.id.video_title_text);
            videoDurationText = itemView.findViewById(R.id.video_duration_text);
            videoDescriptionText = itemView.findViewById(R.id.video_description_text);

        }
    }

    /**
     * Sets the list of videos to be shown. If this value is null, then the list will be empty.
     *
     * @param videoList list of {@link Video} objects.
     */
    void setVideoList(@Nullable List<Video> videoList) {
        this.videoList = videoList;
        buildIndexMap();
    }

    /**
     * Build the index map.
     */
    private void buildIndexMap() {
        indexMap.clear();
        if (videoList != null) {
            int index = 0;
            for (Video video : videoList) {
                indexMap.put(video.getId(), index++);
            }
        }
        notifyDataSetChanged();
    }

    /**
     * Converts the given duration into a time span string.
     *
     * @param duration elapsed time as number of milliseconds.
     * @return the formatted time span string.
     */
    @NonNull
    private static String millisecondsToString(long duration) {
        final TimeUnit scale = TimeUnit.MILLISECONDS;

        StringBuilder builder = new StringBuilder();
        long days = scale.toDays(duration);
        duration -= TimeUnit.DAYS.toMillis(days);
        if (days > 0) {
            builder.append(days);
            builder.append(days > 1 ? " days " : " day ");
        }

        long hours = scale.toHours(duration);
        duration -= TimeUnit.HOURS.toMillis(hours);
        if (hours > 0) {
            builder.append(String.format(Locale.getDefault(),"%02d:", hours));
        }

        long minutes = scale.toMinutes(duration);
        duration -= TimeUnit.MINUTES.toMillis(minutes);

        long seconds = scale.toSeconds(duration);
        builder.append(String.format(Locale.getDefault(),"%02d:%02d", minutes, seconds));

        return builder.toString();
    }
}
