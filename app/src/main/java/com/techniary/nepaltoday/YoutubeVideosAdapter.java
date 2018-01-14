package com.techniary.nepaltoday;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by sanjiv on 1/14/18.
 */

public class YoutubeVideosAdapter extends ArrayAdapter<YoutubeTrends> {

    public YoutubeVideosAdapter(@NonNull Context context, @NonNull ArrayList<YoutubeTrends> youtubeTrends) {
        super(context, 0, youtubeTrends);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable  View convertView, @NonNull ViewGroup parent) {
        final YoutubeTrends tempView = getItem(position);
        if(convertView==null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.each_youtube_trend_view,parent,false);
        }

        TextView videoTitle = convertView.findViewById(R.id.trendingVideoTitle);
        TextView channelTitle = convertView.findViewById(R.id.channelTitle);
        final ImageView thumbnail = convertView.findViewById(R.id.video_image_thumbnail);

        videoTitle.setText(tempView.getVideoTitle());
        channelTitle.setText(tempView.getChannelTitle());

        Picasso.with(getContext()).load(tempView.getThumbnail()).networkPolicy(NetworkPolicy.OFFLINE).into(thumbnail, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError() {
                Picasso.with(getContext()).load(tempView.getThumbnail()).into(thumbnail);
            }
        });

        return convertView;
    }
}
