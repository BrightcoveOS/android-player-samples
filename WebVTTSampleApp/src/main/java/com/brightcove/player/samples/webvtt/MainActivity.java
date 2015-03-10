package com.brightcove.player.samples.webvtt;

import android.media.MediaFormat;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.brightcove.player.view.BrightcovePlayer;
import com.brightcove.player.view.BrightcoveVideoView;

/**
 * This activity demonstrates how to play a video with closed
 * captions in multiple languages.
 */
public class MainActivity extends BrightcovePlayer {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        brightcoveVideoView = (BrightcoveVideoView) findViewById(R.id.brightcove_video_view);
        super.onCreate(savedInstanceState);

        brightcoveVideoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.sintel_trailer));

        MediaFormat mediaFormat = MediaFormat.createSubtitleFormat("text/vtt", "de");
        brightcoveVideoView.addSubtitleSource(getResources().openRawResource(R.raw.sintel_trailer_de), mediaFormat);
        mediaFormat = MediaFormat.createSubtitleFormat("text/vtt", "en");
        brightcoveVideoView.addSubtitleSource(getResources().openRawResource(R.raw.sintel_trailer_en), mediaFormat);
        mediaFormat = MediaFormat.createSubtitleFormat("text/vtt", "es");
        brightcoveVideoView.addSubtitleSource(getResources().openRawResource(R.raw.sintel_trailer_es), mediaFormat);
        mediaFormat = MediaFormat.createSubtitleFormat("text/vtt", "fr");
        brightcoveVideoView.addSubtitleSource(getResources().openRawResource(R.raw.sintel_trailer_fr), mediaFormat);
        mediaFormat = MediaFormat.createSubtitleFormat("text/vtt", "it");
        brightcoveVideoView.addSubtitleSource(getResources().openRawResource(R.raw.sintel_trailer_it), mediaFormat);
        mediaFormat = MediaFormat.createSubtitleFormat("text/vtt", "nl");
        brightcoveVideoView.addSubtitleSource(getResources().openRawResource(R.raw.sintel_trailer_nl), mediaFormat);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_cc_settings:
                showClosedCaptioningDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
