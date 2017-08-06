package com.example.android.newsapp;

/**
 * Created by Timo on 09.07.2017.
 */

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
import java.util.List;

import static com.example.android.newsapp.ArticleActivity.LOG_TAG;

public final class QueryUtils {

    private QueryUtils() {
    }

    /**
     * Return a list of {@link Article} objects that has been built up from
     * parsing a JSON response.
     */
    public static List<Article> extractFeatureFromJson(String jsonResponse) {

        // Create an empty ArrayList
        List<com.example.android.newsapp.Article> articles = new ArrayList<>();

        // Try to parse the JSON_RESPONSE.
        try {
            JSONObject jsonObj = new JSONObject(jsonResponse);
            JSONObject response = jsonObj.getJSONObject ("response");
            JSONArray results = response.getJSONArray("results");

            //Loop through each feature in the array
            for (int i = 0; i < results.length(); i++) {
                //Get features JSONObject at position i
                JSONObject currentArticle = results.getJSONObject(i);

                //Extract title
                String title = currentArticle.getString("webTitle");

                //Extract section
                String section = currentArticle.getString("sectionName");

                //Extract date
                String date = currentArticle.getString("webPublicationDate");

                //Extract author
                JSONArray contributors = currentArticle.getJSONArray("tags");
                String authors = "";
                for (int x = 0; x < contributors.length(); x++) {

                    JSONObject author = contributors.getJSONObject(x);

                    if (author.isNull("webTitle") == false) {
                        if (x > 0) {
                            authors = authors + ", ";
                        }
                        authors = authors + author.getString("webTitle");
                    }
                }

                //Extract url
                String url = currentArticle.getString("webUrl");

                //Create article java object
                com.example.android.newsapp.Article article = new com.example.android.newsapp.Article(title,section,date,authors,url);
                articles.add(article);
            }
        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the JSON results", e);
        }

        // Return the list of articles
        return articles;
    }


    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
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
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
                Log.e(LOG_TAG, "url: " + url);
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
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

    /**
     * Query news API
     */
    public static List<com.example.android.newsapp.Article> fetchArticleData(String requestUrl) {

        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link Article}s
        List<com.example.android.newsapp.Article> articles = extractFeatureFromJson(jsonResponse);

        // Return the list of {@link Article}s
        return articles;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

}
