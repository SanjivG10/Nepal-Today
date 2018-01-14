package com.techniary.nepaltoday;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import java.util.ArrayList;

public class NewsActivity extends AppCompatActivity {

    private static final String UNIQUE_URL="https://newsapi.org/v2/top-headlines?sources=bbc-news&apiKey=425d4e63857c49e9aafa39d3588ac450";

    private ListView newsView;
    private ArrayList<News> newsArrayList;
    private NewsViewAdapter newsViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        newsView = (ListView) findViewById(R.id.listViewForNews);
        newsArrayList = new ArrayList<>();
        newsViewAdapter = new NewsViewAdapter(this, new ArrayList<News>());
        newsView.setAdapter(newsViewAdapter);

        NewsAsyncTask getNewsTask = new NewsAsyncTask();
        getNewsTask.execute(UNIQUE_URL);


    }


    private class NewsAsyncTask extends AsyncTask<String, Void, ArrayList<News>> {


        @Override
        protected void onPostExecute(ArrayList<News> news) {
            super.onPostExecute(news);
            newsViewAdapter.clear();
            if (news != null && !news.isEmpty()) {
                newsViewAdapter.addAll(news);
            }

        }

        @Override
        protected ArrayList<News> doInBackground(String... urls) {

            if (urls.length < 1 || urls[0] == null) {
                return null;
            }

            System.out.println("IAMHERE");

            ArrayList<News> result = GetNews.fetchNewsData(urls[0]);
            return result;
        }
    }



}
