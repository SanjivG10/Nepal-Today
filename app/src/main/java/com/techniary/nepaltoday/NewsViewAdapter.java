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
 * Created by sanjiv on 1/8/18.
 */

public class NewsViewAdapter extends ArrayAdapter<News> {
    public NewsViewAdapter(@NonNull Context context, ArrayList<News> news) {
        super(context,0, news);
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        News news = getItem(position);
        if(convertView ==null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.each_news_layout, parent, false);
        }

        TextView newsTitle = (TextView) convertView.findViewById(R.id.newsTitle);
        newsTitle.setText(news.getTitle());

        TextView newsDescription = (TextView) convertView.findViewById(R.id.newsDescription);
        newsDescription.setText(news.getDescription());

        final ImageView newsImage = (ImageView) convertView.findViewById(R.id.newsImage);
        final News newNews = news;

        Picasso.with(getContext()).load(news.getImageUrl()).networkPolicy(NetworkPolicy.OFFLINE).into(newsImage, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError() {
                Picasso.with(getContext()).load(newNews.getImageUrl()).into(newsImage);
            }
        });

        return convertView;
    }
}
