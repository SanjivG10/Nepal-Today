package com.techniary.nepaltoday;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;


public  class YoutubeTrendUtils {

    public static URL createURL(String url)
    {
        URL url_made = null;
        try
        {
        url_made = new URL(url);
        }

        catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url_made;
    }


    public static String makeHTTPREQUEST(URL url) throws IOException {
        String responseFromUrl = "";
        if (url == null) {
            return null;
        }
        HttpURLConnection httpURLConnection = null;
        InputStream inputStream = null;
        try {
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setConnectTimeout(10000);
            httpURLConnection.setReadTimeout(10000);
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();

            if (httpURLConnection.getResponseCode() == 200) {
                inputStream = httpURLConnection.getInputStream();
                responseFromUrl = gettingStreamAndBuilding(inputStream);

            }

        }
        catch (IOException e) {
            e.printStackTrace();

        }
        finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
            if (inputStream != null) {

                inputStream.close();
            }
        }
        return responseFromUrl;
    }

    private static String gettingStreamAndBuilding(InputStream inputStream) throws IOException {
        StringBuilder builder = new StringBuilder();
        if(inputStream!=null)
        {
            InputStreamReader reader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader breader = new BufferedReader(reader);
            String line = breader.readLine();
            while(line!=null)
            {
                builder.append(line);
                line = breader.readLine();
            }

        }

        return builder.toString();
    }

    private static ArrayList<YoutubeTrends> getJSON(String json)
    {
        if(TextUtils.isEmpty(json))
        {
            return null;
        }

        ArrayList<YoutubeTrends> temp = new ArrayList<>();
        try {

            JSONObject baseJSONObject = new JSONObject(json);
            JSONArray baseArray = baseJSONObject.getJSONArray("items");

            for(int i = 0; i<baseArray.length();i++)
            {
                JSONObject currentObject = baseArray.getJSONObject(i);
                String videoID = currentObject.getString("id");
                JSONObject snippetDetails = currentObject.getJSONObject("snippet");
                String publishedDate = snippetDetails.getString("publishedAt");
                String title = snippetDetails.getString("title");
                String channelID = snippetDetails.getString("channelTitle");
                Log.e("ALLTHINGS",publishedDate+" "+title+" "+ " "+channelID+" "+videoID);
                JSONObject thumbnails_obj = snippetDetails.getJSONObject("thumbnails");
                JSONObject default_value = thumbnails_obj.getJSONObject("default");
                String url_thumb = default_value.getString("url");
                YoutubeTrends trends_json = new YoutubeTrends(publishedDate,channelID,url_thumb,videoID,title);
                temp.add(trends_json);

            }
        } catch (JSONException e) {
                Log.e("EXCEPIONJSON","HAHAHA");

        }

        return temp;
    }

    public static ArrayList<YoutubeTrends> sumUpEverything(String url) {

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        URL request_url = createURL(url);
        String json = "";
        try {
            json = makeHTTPREQUEST(request_url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ArrayList<YoutubeTrends> x = getJSON(json);
        return x;
    }

}
