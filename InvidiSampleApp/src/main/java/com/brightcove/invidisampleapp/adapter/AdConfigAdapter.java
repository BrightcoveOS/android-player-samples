package com.brightcove.invidisampleapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.brightcove.invidisampleapp.R;
import com.brightcove.invidisampleapp.model.AdConfig;

import java.util.List;

public class AdConfigAdapter extends ArrayAdapter<AdConfig> {
    private final LayoutInflater mInflater;

    public AdConfigAdapter(@NonNull Context context, @NonNull List<AdConfig> adConfigs) {
        super(context, R.layout.item_ad_config, adConfigs);

        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getView(position, convertView, parent);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        ViewHolder mHolder;
        if (view == null) {
            view = mInflater.inflate(R.layout.item_ad_config, parent, false);

            mHolder = new ViewHolder();
            mHolder.nameTextView = view.findViewById(R.id.text_name);

            view.setTag(mHolder);
        } else {
            mHolder = (ViewHolder) view.getTag();
        }

        AdConfig adConfig = getItem(position);
        if (adConfig != null) {
            mHolder.nameTextView.setText(adConfig.getName());
        } else {
            mHolder.nameTextView.setText("");
        }

        return view;
    }

    static class ViewHolder {
        TextView nameTextView;
    }
}
