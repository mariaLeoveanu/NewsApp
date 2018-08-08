package com.example.android.newsapp;

import android.content.AsyncTaskLoader;
import android.content.Context;

public class ArticleLoader extends AsyncTaskLoader {

    private String mUrl;

    @Override
    public Object loadInBackground() {
        if (mUrl == null) {
            return null;
        }
        return Utils.fetchArticleData(mUrl);
    }

     ArticleLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }


}
