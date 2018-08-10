package com.example.android.newsapp;

import android.content.res.Resources;
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


final class Utils {


    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e("Utils", "error URL");
        }
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
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
                Log.e("Utils", "Error response code"+ urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e("Utils", "problem JSON");
        } finally {
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

    private static ArrayList<Article> extractIdFromJson(String articlesJson) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(articlesJson)) {
            return null;
        }
        ArrayList<Article> articles = new ArrayList<>();

        try {
            JSONObject baseJsonResponse = new JSONObject(articlesJson);
            JSONObject response = baseJsonResponse.getJSONObject("response");
            JSONArray resultsArray = response.getJSONArray("results");

            for (int i = 0; i < resultsArray.length(); i++) {
                JSONObject currentFeature = resultsArray.getJSONObject(i);

                String title = currentFeature.getString("webTitle");
                //some articles have the author name in the article title. to avoid doubling
                //information i remove it from the article name
                if(title.contains("|")){
                    int index = title.indexOf("|");
                    title = title.substring(0, index);
                }

                JSONArray tags = currentFeature.getJSONArray("tags");

                String writer;
                if (tags != null && tags.length() > 0) {
                    JSONObject firstObjectTags = tags.getJSONObject(0);
                    writer = firstObjectTags.getString("webTitle");
                } else
                    writer = "Unknown Author";

                String sectionName = currentFeature.getString("sectionName");
                String writerAndSection = writer + " in " + sectionName;
                String date = currentFeature.getString("webPublicationDate");
                String articleUrl = currentFeature.getString("webUrl");

                articles.add(new Article(title, writerAndSection, date, articleUrl));
            }

        } catch (JSONException e) {
            Log.e("Utils: ", "Problem retrieving the news JSON results.", e);
        }
        return articles;
    }

    static ArrayList<Article> fetchArticleData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e("Utils", "makeHttpRequest ERROR");
        }

        return extractIdFromJson(jsonResponse);
    }
}
