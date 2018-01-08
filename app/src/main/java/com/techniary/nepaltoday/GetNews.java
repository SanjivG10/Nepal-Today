package com.techniary.nepaltoday;

import android.app.LoaderManager;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.sql.Array;
import java.util.ArrayList;
import java.util.List;


public class GetNews {

    private static final String UNIQUE_URL="https://newsapi.org/v2/top-headlines?sources=bbc-news&apiKey=425d4e63857c49e9aafa39d3588ac450";

    private static URL createUrl(String url)
    {
        URL xUrl  = null;
        try {
            xUrl = new URL(url);
        } catch (MalformedURLException e) {
            Log.e("error", "Problem building the URL ", e);
        }

        return xUrl;
    }

    private static String makeHttpRequest(URL url) throws IOException
    {
        String jsonResponse = "";

        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();


            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e("ErrorHEREATTHIsTIME", "Error response code: " + urlConnection.getResponseCode());
            }
        }
            catch (IOException e) {
                Log.e("ErrorHere", "Problem retrieving the earthquake JSON results.", e);
            }
            finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (inputStream != null) {

                    inputStream.close();
                }
            }
            return jsonResponse;

    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();


    }

        private static ArrayList<News> extractFeatureFromJson(String newsJSON) {
            if (TextUtils.isEmpty(newsJSON)) {
                return null;
            }

           ArrayList<News> newsArrayList = new ArrayList<>();

             try {
                JSONObject baseJsonResponse = new JSONObject(newsJSON);

                JSONArray newsArray = baseJsonResponse.getJSONArray("articles");

                 Log.e("CHECKING_VALUE",String.valueOf(newsArray.length()));

                for (int i = 0; i < newsArray.length(); i++) {

                    JSONObject currentNews = newsArray.getJSONObject(i);

                    String title = currentNews.getString("title");

                    String description = currentNews.getString("description");

                    String url = currentNews.getString("url");

                    String urlToImage = currentNews.getString("urlToImage");

                    String publishedDate = currentNews.getString("publishedAt");

                    News news = new News(title, description, url, urlToImage,publishedDate);
                    newsArrayList.add(news);
                }

            } catch (JSONException e) {

                 Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
            }

       return newsArrayList;
        }





    public static ArrayList<News> fetchNewsData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e("ErrorINARRAYLIST", "Problem making the HTTP request.", e);
        }

        ArrayList<News> news = extractFeatureFromJson(jsonResponse);

        return news;
    }





    }