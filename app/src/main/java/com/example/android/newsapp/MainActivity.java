package com.example.android.newsapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<Article>> {

    public static final String NEWS_URL = "https://content.guardianapis.com/search?q=fashion|makeup|lifestyle|sport&show-fields=thumbnail&show-tags=contributor&api-key=562c24cc-e65d-48fb-85e1-f3f1e9e70f72";
    public static final int ARTICLE_LOADER_ID = 1;
    ArticleAdapter articleAdapter;
    TextView emptyText;
    ProgressBar progressBar;
    boolean isConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emptyText = findViewById(R.id.empty_text);
        progressBar = findViewById(R.id.progress_bar);

        articleAdapter = new ArticleAdapter(this, new ArrayList<Article>());
        ListView listView = findViewById(R.id.list);
        listView.setAdapter(articleAdapter);

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        listView.setEmptyView(emptyText);

        LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(ARTICLE_LOADER_ID, null, this);

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
        return  new ArticleLoader(this, NEWS_URL);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Article>> loader, ArrayList<Article> articles) {
        articleAdapter.clear();
        if (articles != null && !articles.isEmpty()) {
            articleAdapter.addAll(articles);
        }else
            emptyText.setText(R.string.no_articles_message);
        progressBar.setVisibility(View.INVISIBLE);
        if(!isConnected){
            emptyText.setText(R.string.no_internet_connection_message);
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Article>> loader) {
        articleAdapter.clear();
    }

}
