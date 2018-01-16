package com.techniary.nepaltoday;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import java.io.IOException;
import java.util.ArrayList;

public class YoutubeVideoActivity extends YouTubeBaseActivity {

    private static final String YOUTUBE_TRENDING_URL="https://www.googleapis.com/youtube/v3/videos?part=snippet&chart=mostPopular&regionCode=NP&maxResults=10&key=AIzaSyB9ARJHdtJs4YxUdb2XnfpzkqLxGDQkDwU";
    private ListView videos_list;
    private YoutubeVideosAdapter madapter;
    private Toolbar mToolbar;
    private YouTubePlayer youTubeSGplayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube_video);

        mToolbar = (Toolbar) findViewById(R.id.youtubeToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(YoutubeVideoActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        videos_list = (ListView) findViewById(R.id.youtube_videos_list);
        madapter = new YoutubeVideosAdapter(getApplicationContext(),new ArrayList<YoutubeTrends>());
        videos_list.setAdapter(madapter);

        Log.e("TRYING","SHITMAN");

        AsyncTaskForYoutube asyncTaskForYoutube = new AsyncTaskForYoutube();
        asyncTaskForYoutube.execute(YOUTUBE_TRENDING_URL);


        videos_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                YouTubePlayer.PlaybackEventListener playbackEventListener;
                AlertDialog.Builder builder = new AlertDialog.Builder(YoutubeVideoActivity.this);
                YoutubeTrends trends = madapter.getItem(i);
                final String video = trends.getVideoID();
                View mview = getLayoutInflater().inflate(R.layout.youtube_alert_dialog,null);
                YouTubePlayerView mYoutubePlayer = mview.findViewById(R.id.youtubePlayerViewAlertDailog);
                mYoutubePlayer.initialize("AIzaSyB9ARJHdtJs4YxUdb2XnfpzkqLxGDQkDwU", new YouTubePlayer.OnInitializedListener() {
                    @Override
                    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                       youTubeSGplayer = youTubePlayer;
                       youTubePlayer.setShowFullscreenButton(false);
                       youTubeSGplayer.loadVideo(video);
                    }

                    @Override
                    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                        Toast.makeText(getApplicationContext(),"ERROR OCCURED",Toast.LENGTH_LONG).show();
                    }
                });
                builder.setView(mview);
                AlertDialog dialog = builder.create();
                dialog.show();
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        youTubeSGplayer.release();
                    }
                });
            }
        });

    }


    public class AsyncTaskForYoutube extends AsyncTask<String,Void,ArrayList<YoutubeTrends>>
    {


        @Override
        protected ArrayList<YoutubeTrends> doInBackground(String... strings) {

            if(strings.length<1 || strings[0] ==null)
            {
                return null;
            }
            ArrayList<YoutubeTrends> x = YoutubeTrendUtils.sumUpEverything(strings[0]);

            return x;
        }

        @Override
        protected void onPostExecute(ArrayList<YoutubeTrends> youtubeTrends) {
            super.onPostExecute(youtubeTrends);

            madapter.clear();
            if(youtubeTrends!=null && !youtubeTrends.isEmpty())
            {
                madapter.addAll(youtubeTrends);
            }

        }

    }
}
