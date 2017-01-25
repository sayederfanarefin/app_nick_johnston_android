package com.bulbinc.nick;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ScrollingActivity_news extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling_news);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_title);

        SharedPreferences settings = getSharedPreferences("LEL", 0);

        String email  = settings.getString("user_email", "na");

        if(email.equals("na")){
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

        final Bundle b = getIntent().getExtras();

        ImageView iv = (ImageView) findViewById(R.id. image_news_scrolling_activity);


        toolbar.setTitle(b.getString("news_title"));

        TextView news_bodt = (TextView) findViewById(R.id.news_display_scrolling_body);

        news_bodt.setText(refine_date(b.getString("news_date")) + "\n\n"+ b.getString("news_body"));

        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);

                sendIntent.putExtra(Intent.EXTRA_TEXT, "Title: "+ b.getString("news_title") + "\n\nNews: "+ refine_date(b.getString("news_date")) + "\n"+ b.getString("news_body") + "\n" + filter_url(b.getString("image_url")));
                sendIntent.setType("text/plain");

                //sendIntent.putExtra(Intent.EXTRA_STREAM, filter_url(b.getString("image_url")));
                //sendIntent.setType("image/jpeg");

                startActivity(sendIntent);

                Snackbar.make(view, "Choose how you want to share.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).setDuration(1000).show();
            }
        });

        String local_url = b.getString("local_image_url");
        if(!local_url.equals("na")){

            try {
                File f=new File(local_url);
                Bitmap bb = BitmapFactory.decodeStream(new FileInputStream(f));
                iv.setImageBitmap(bb);
            }
            catch (FileNotFoundException e)
            {

            }

        }


    }
    private String filter_url(String url){
        url = url.replace("\\", "");
        return url;
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }
}
