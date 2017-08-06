package com.example.android.newsapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Timo on 09.07.2017.
 */

public class ArticleActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Article>> {
    /**
     * Constant value for the loader ID
     */

    private LoaderManager.LoaderCallbacks<List<Article>> mCallbacks;

    private static final int ARTICLE_LOADER_ID = 1;

    public static final String LOG_TAG = ArticleActivity.class.getName();

    /** URL for article Data from the guardian  */
    private static final String REQUEST_URL =
            "http://content.guardianapis.com/search?order-by=newest&show-tags=contributor&q=Frankfurt&api-key=test";

    private String REQUEST_URL_FINAL;

    private ArticleAdapter mAdapter;

    private TextView mEmptyStateTextView;

    ConnectivityManager cm = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.article_activity);

        //set callbacks from context
        mCallbacks = this;

        // Get search string from MainActivity
        //String searchString = getIntent().getStringExtra("searchString");
        //replace spaces with %20 for URL
        //searchString = searchString.replace(" ", "%20");
        // add searchString to URL
        REQUEST_URL_FINAL = REQUEST_URL; //+ searchString;

        // Find a reference to the {@link ListView} in the layout
        ListView articleListView = (ListView) findViewById(R.id.list);

        // Create a new adapter that takes an empty list of articles as input
        mAdapter = new ArticleAdapter(this, new ArrayList<Article>());
        articleListView.setAdapter(mAdapter);

        mEmptyStateTextView = (TextView) findViewById(R.id.emptyView);
        articleListView.setEmptyView(mEmptyStateTextView);

        Button updateView = (Button) findViewById(R.id.update);

        cm = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if (isConnected) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();

            // Initialize the loader
            loaderManager.initLoader(ARTICLE_LOADER_ID, null, mCallbacks);

            // Set an item click listener on the ListView, which sends an intent to a web browser
            // to open google articles or a website with more information about the selected article.
            articleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                    // Find the current article that was clicked on
                    Article currentArticle = mAdapter.getItem(position);

                    // Convert the String URL into a URI object (to pass into the Intent constructor)
                    Uri articleUri = Uri.parse(currentArticle.getUrl());

                    // Create a new intent to view the article URI
                    Intent websiteIntent = new Intent(Intent.ACTION_VIEW, articleUri);

                    // Send the intent to launch a new activity
                    startActivity(websiteIntent);
                }
            });
        }
        else {
            mEmptyStateTextView.setText(R.string.no_connection);
            findViewById(R.id.ProgressBar).setVisibility(View.INVISIBLE);
        }

        updateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                boolean isConnected = activeNetwork != null &&
                        activeNetwork.isConnectedOrConnecting();

                if (isConnected) {
                    LoaderManager loaderManager = getLoaderManager();
                    // Restart the loader
                    loaderManager.destroyLoader(ARTICLE_LOADER_ID);
                    loaderManager.restartLoader(ARTICLE_LOADER_ID, null, mCallbacks);
                }
                else {
                    mAdapter.clear();
                    mEmptyStateTextView.setText(R.string.no_connection);
                    findViewById(R.id.ProgressBar).setVisibility(View.INVISIBLE);
                }

            }
        });

    }

    @Override
    public Loader<List<Article>> onCreateLoader(int i, Bundle bundle) {
        return new ArticleLoader(this, REQUEST_URL_FINAL);
    }

    @Override
    public void onLoadFinished(Loader<List<Article>> loader, List<Article> articles) {

        View ProgressBar = findViewById(R.id.ProgressBar);
        ProgressBar.setVisibility(View.INVISIBLE);

        // Clear the adapter of previous article data
        mAdapter.clear();

        // If there is a valid list of {@link Article}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (articles != null && !articles.isEmpty()) {
            mAdapter.addAll(articles);
        }

        mEmptyStateTextView.setText(R.string.no_articles);

    }

    @Override
    public void onLoaderReset(Loader<List<Article>> loader) {
        mAdapter.clear();
        View ProgressBar = findViewById(R.id.ProgressBar);
        ProgressBar.setVisibility(View.INVISIBLE);
        mEmptyStateTextView.setText("");
    }

}
