package com.example.android.newsapp;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;



public class ArticleAdapter extends ArrayAdapter<Article> {

     ArticleAdapter(Activity context, ArrayList<Article> articleList) {
        super(context, 0, articleList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View articleView = convertView;
        if (articleView == null) {
            //inflate a view if one doesn't already exist
            articleView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }
        Article article = getItem(position);

        TextView title = articleView.findViewById(R.id.article_title);
        TextView author = articleView.findViewById(R.id.author_name);
        TextView date = articleView.findViewById(R.id.date);


        title.setText(article.getmTitle());
        author.setText(R.string.written_by + article.getmAuthor());
        date.setText(article.getmDate());

        return articleView;
    }
}
