package com.bulbinc.nick;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by erfan on 4/29/2016.
 */
public class NewsAdapter extends ArrayAdapter<News> {
    Context mContext;

    /**
     * Adapter View layout
     */
    int mLayoutResourceId;

    public NewsAdapter(Context context, int layoutResourceId) {
        super(context, layoutResourceId);

        mContext = context;
        mLayoutResourceId = layoutResourceId;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        final News currentItem = getItem(position);

        if (row == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(R.layout.news_layout_unit, parent, false);
        }

        row.setTag(currentItem);
        final TextView the_unit_news_titlw = (TextView) row.findViewById(R.id.textView_news_list_title);
        final TextView the_unit_news_body = (TextView) row.findViewById(R.id.textView_news_list_body);
        final TextView the_unit_news_date = (TextView) row.findViewById(R.id.textView_news_list_date);
        final ImageView image_news = (ImageView) row.findViewById(R.id.news_image) ;
        the_unit_news_titlw.setText(currentItem.title);
        the_unit_news_date.setText(refine_date(currentItem.date));

        String temp = currentItem.body;
        int limit = 50;
        if(temp.length()>limit){
            temp = currentItem.body.substring(0,limit);
            temp = temp + " ...";
        }

        the_unit_news_body.setText(temp);
        if(!currentItem.local_url.equals("na")){

            try {
                File f=new File(currentItem.local_url);
                Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
                image_news.setImageBitmap(b);
            }
            catch (FileNotFoundException e)
            {

            }

        }

        return row;
    }
    public String refine_date(String date){
        String xx[] = date.split(" ");
        date = xx[0];

        Date date_Date ;// = new Date(date);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String date_dekhanor_moto= "1st Jan,2012";
        try {
            date_Date = df.parse(date);
            SimpleDateFormat dfDate_date = new SimpleDateFormat("dd");
            String datee = dfDate_date.format(date_Date);

            SimpleDateFormat dfDate_rest_of_date = new SimpleDateFormat("MMM, yyyy");
            String datee_rest = dfDate_rest_of_date.format(date_Date);

            date_dekhanor_moto = datee + getDateSuffix(Integer.valueOf(datee)) + " " + datee_rest;

        } catch (ParseException e) {
            Log.v("parse exception", e.getMessage());
        } catch (Exception e ){
            Log.v("Exception in date", e.getMessage());
        }
        return date_dekhanor_moto;
    }


    public String getDateSuffix( int day) {
        switch (day) {
            case 1: case 21: case 31:
                return ("st");

            case 2: case 22:
                return ("nd");

            case 3: case 23:
                return ("rd");

            default:
                return ("th");
        }
    }
}