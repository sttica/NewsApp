package com.example.android.newsapp;

/**
 * Created by Timo on 09.07.2017.
 */

public class Article {

    private String mTitle;
    private String mSection;
    private String mDate;
    private String mAuthors;
    private String mUrl;

    /**
     * Create a new Article object.
     *
     * @param title
     * @param section
     * @param date
     * @param authors
     * @param url

     */
    public Article (String title, String section, String date, String authors, String url) {
        mTitle = title;
        mSection = section;
        mDate = date;
        mAuthors = authors;
        mUrl = url;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getSection() {
        return mSection;
    }

    public String getDate() {
        return mDate;
    }

    public String getAuthors() {
        return mAuthors;
    }

    public String getUrl() {
        return mUrl;
    }

}

