package com.example.android.newsapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Timo on 09.07.2017.
 */

public class ArticleAdapter extends ArrayAdapter<Article> {

    public ArticleAdapter(Activity context, ArrayList<Article> articles) {
        super(context, 0, articles);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }

        com.example.android.newsapp.Article currentArticle = getItem(position);

        TextView sectionView = (TextView) listItemView.findViewById(R.id.section);
        String section = currentArticle.getSection();
        sectionView.setText(section);

        TextView titleView = (TextView) listItemView.findViewById(R.id.title);
        String title = currentArticle.getTitle();
        titleView.setText(title);

        TextView authorsView = (TextView) listItemView.findViewById(R.id.authors);
        String authors = currentArticle.getAuthors();
        authorsView.setText(authors);

        TextView dateView = (TextView) listItemView.findViewById(R.id.publishedDate);

        String startDateString = currentArticle.getDate();

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = format.parse(startDateString);
            String datetime = targetFormat.format(date);
            dateView.setText(datetime);
        } catch (java.text.ParseException e) {
        }

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("mailto:"));

        // Return the list item view that is now showing the appropriate data
        return listItemView;
    }



}
