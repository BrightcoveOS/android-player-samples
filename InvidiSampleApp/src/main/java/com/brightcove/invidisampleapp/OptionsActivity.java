package com.brightcove.invidisampleapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.brightcove.invidisampleapp.adapter.AdConfigAdapter;
import com.brightcove.invidisampleapp.model.AdConfig;
import com.brightcove.invidisampleapp.model.PulseAccount;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ooyala.pulse.RequestSettings;

import java.io.IOException;
import java.io.InputStreamReader;

public class OptionsActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = OptionsActivity.class.getSimpleName();
    private static final String JSON_FILE = "PulseValidation.json";

    @NonNull
    Gson gson = new GsonBuilder().create();

    private Spinner mAdConfigSpinner;

    private Spinner mSeekModeSpinner;

    private PulseAccount mPulseAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            mPulseAccount = PulseAccount.createFromReader(
                    gson,
                    new InputStreamReader(getAssets().open(JSON_FILE)));
            Log.d(TAG, "PulseAccount: " + mPulseAccount);
        } catch (IOException e) {
            throw new IllegalStateException("Error loading AdConfigs", e);
        }

        AdConfigAdapter adConfigAdapter = new AdConfigAdapter(this, mPulseAccount.getAdConfigs());
        mAdConfigSpinner = findViewById(R.id.spinner_ad_config);
        mAdConfigSpinner.setAdapter(adConfigAdapter);

        ArrayAdapter<RequestSettings.SeekMode> seekModeAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                RequestSettings.SeekMode.values());
        mSeekModeSpinner = findViewById(R.id.spinner_seek_mode);
        mSeekModeSpinner.setAdapter(seekModeAdapter);

        Button mNextButton = findViewById(R.id.button_next);
        mNextButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_next) {
            AdConfig adConfig = (AdConfig) mAdConfigSpinner.getSelectedItem();
            RequestSettings.SeekMode seekMode = (RequestSettings.SeekMode) mSeekModeSpinner.getSelectedItem();

            Log.d(TAG, "AdConfig: " + adConfig);
            Log.d(TAG, "SeekMode: " + seekMode);

            Intent intent = PlayerActivity.createIntent(
                    this,
                    mPulseAccount.getPulseHost(),
                    adConfig,
                    seekMode);
            startActivity(intent);
        }
    }
}
