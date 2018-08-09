package com.example.android.newsapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<Article>> {

    public static final String NEWS_URL = "https://content.guardianapis.com/search";
    public static final int ARTICLE_LOADER_ID = 1;
    ArticleAdapter articleAdapter;
    TextView emptyText;
    ProgressBar progressBar;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        emptyText = findViewById(R.id.empty_text);
        progressBar = findViewById(R.id.progress_bar);

        articleAdapter = new ArticleAdapter(this, new ArrayList<Article>());
        ListView listView = findViewById(R.id.list);
        listView.setAdapter(articleAdapter);
        listView.setEmptyView(emptyText);

        if (isConnected()) {
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(ARTICLE_LOADER_ID, null, this);
        } else {
            // Hide the progress bar.
            progressBar.setVisibility(View.INVISIBLE);
            emptyText.setText(getString(R.string.no_internet_connection_message));
            // inform the user that there is no connection
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Article currentArticle = articleAdapter.getItem(i);
                Uri webpage = Uri.parse(currentArticle.getmURL());
                Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
                intent.setData(webpage);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });

    }

    @Override
    public Loader<ArrayList<Article>> onCreateLoader(int i, Bundle bundle) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String topic = sharedPrefs.getString("topic","fashion|politics");
        Uri baseUri = Uri.parse(NEWS_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        uriBuilder.appendQueryParameter("?q", topic);
        uriBuilder.appendQueryParameter("show-tags", "contributor");
        uriBuilder.appendQueryParameter("api-key", "562c24cc-e65d-48fb-85e1-f3f1e9e70f72");


        return new ArticleLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Article>> loader, ArrayList<Article> articles) {
        articleAdapter.clear();
        if (articles != null && !articles.isEmpty()) {
            articleAdapter.addAll(articles);
        }else
            emptyText.setText(R.string.no_articles_message);
        progressBar.setVisibility(View.INVISIBLE);
        if(!isConnected()){
            emptyText.setText(R.string.no_internet_connection_message);
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Article>> loader) {
        articleAdapter.clear();
    }

     boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return  activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

}
