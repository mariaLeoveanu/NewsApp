package com.example.android.newsapp;

 class Article {

    private String mTitle;
    private String mAuthor;
    private String mDate;
    private String mURL;

     Article( String title, String author, String date, String url) {

        mTitle = title;
        mAuthor = author;
        mDate = date.substring(0,10);
        mURL = url;
    }

     String getmTitle() {
        return mTitle;
    }
     String getmAuthor(){
        return mAuthor;
    }
     String getmDate(){
        return mDate;
    }
     String getmURL(){
        return mURL;
    }

}