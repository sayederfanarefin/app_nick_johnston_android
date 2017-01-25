package com.bulbinc.nick;


import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.bulbinc.nick.Values.BandValues;

public class MainActivity extends AppCompatActivity
    implements NavigationView.OnNavigationItemSelectedListener ,
    FragmentDiscography.OnFragmentInteractionListener,
    JammingVideolistFragment.OnFragmentInteractionListener,
    FragmentNews.OnFragmentInteractionListener,
    FragmentSettings.OnFragmentInteractionListener,
    FragmentDisclaimer.OnFragmentInteractionListener,
    FragmentMusic.OnFragmentInteractionListener,
        FragmentGetInTouch.OnFragmentInteractionListener{

    String paackageinfo, fb_page_id_app, fb_page_id_web,intagram_page_id;
    Boolean firstRun;
    SQLiteDatabase db;
    android.app.FragmentManager fragmentManager;
    String x = "FragmentDiscography";
    public NotificationManager notificationManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        BandValues bv = new BandValues();
        paackageinfo = bv.package_info;
        fb_page_id_app = bv.fb_page_id_app;
        fb_page_id_web = bv.fb_page_id_web;
        intagram_page_id = bv.page_insta;

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        SharedPreferences settings = getSharedPreferences("LEL", 0);
        firstRun = settings.getBoolean("isChecked", false);

        String email  = settings.getString("user_email", "na");

        SharedPreferences.Editor editor = settings.edit();


        if(email.equals("na")){
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
           
        }


        if(!firstRun){
            //databse dosent exists, so we need to create one
            db=openOrCreateDatabase("nemesis_local_db", Context.MODE_PRIVATE, null);
            db.execSQL("CREATE TABLE IF NOT EXISTS news(id VARCHAR UNIQUE ,news_title VARCHAR,news_body VARCHAR, date_ VARCHAR, image_url VARCHAR, local_image_url VARCHAR);");
            db.execSQL("CREATE TABLE IF NOT EXISTS youtube_videos(id VARCHAR UNIQUE ,video_name VARCHAR,video_link VARCHAR, date_ VARCHAR);");
            db.execSQL("CREATE TABLE IF NOT EXISTS album(id VARCHAR UNIQUE , album_title VARCHAR, album_art_url VARCHAR, album_year VARCHAR, date_ VARCHAR, album_art_local VARCHAR);");
            db.execSQL("CREATE TABLE IF NOT EXISTS music(title VARCHAR UNIQUE , album_id VARCHAR, track_no VARCHAR, url VARCHAR, id VARCHAR, date_ VARCHAR, source_local VARCHAR);");
            db.execSQL("CREATE TABLE IF NOT EXISTS concerts(id VARCHAR UNIQUE ,concert_title VARCHAR, concert_about VARCHAR, concert_lat VARCHAR, concert_lon VARCHAR, date_ VARCHAR);");

            //the database has been created, and we saved that information in the shared pref.
            editor.putBoolean("isChecked", true);
            editor.commit();
        }

        Fragment fragment = new FragmentDiscography();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.yo_content, fragment);
        ft.commit();
        getSupportActionBar().setTitle(R.string.menu_title_discography);


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);
        notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

    }


    public void onDestroy(){
        SharedPreferences settings = getSharedPreferences("LEL", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("fragment_name", "discography");
        editor.putString("media_player_state", "stopped");
        editor.commit();
        stopService();
        super.onDestroy();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.nav_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")

    @Override
    public void onPause(){
        super.onPause();
        SharedPreferences settings = getSharedPreferences("LEL", 0);
        String last_state = settings.getString("media_player_state","o");
        if(last_state.equals("playing")){
            startService();
        }
    }
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment = null;
        switch (id) {
            case R.id.nav_discography:
                getSupportActionBar().setTitle(R.string.menu_title_discography);
                fragment = new FragmentDiscography();


                break;

            case R.id.nav_jamming:
                getSupportActionBar().setTitle(R.string.menu_title_jamming);

                fragment = new JammingVideolistFragment();

                break;
            case R.id.nav_news:
                getSupportActionBar().setTitle(R.string.menu_title_news);
                fragment = new FragmentNews();

                break;
            case R.id.nav_music:
                getSupportActionBar().setTitle(R.string.menu_title_music);
                fragment = new FragmentMusic();

                break;
            case R.id.nav_live_concert:
                getSupportActionBar().setTitle(R.string.menu_title_live_concert);
                fragment = new FragmentGoogleMap();

                break;

            case R.id.nav_settings:
                getSupportActionBar().setTitle(R.string.menu_title_music_settings);
                fragment = new FragmentSettings();

                break;


            case R.id.nav_contact_us:
                getSupportActionBar().setTitle(R.string.menu_title_contact_us);
                fragment = new FragmentGetInTouch();

                break;


            case R.id.nav_disclaimer:
                getSupportActionBar().setTitle(R.string.menu_title_disclaimer);
                fragment = new FragmentDisclaimer();


                break;
            case R.id.nav_share_fb:
                try {
                    getApplicationContext().getPackageManager().getPackageInfo(paackageinfo, 0);
                     Intent fb_intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/"+ fb_page_id_app));
                    startActivity(fb_intent);
                } catch (Exception e) {
                    Intent fb_intent2 = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/"+ fb_page_id_web));
                    startActivity(fb_intent2);
                }

                break;
            case R.id.nav_share_instagram:
                try {
                    getApplicationContext().getPackageManager().getPackageInfo(paackageinfo, 0);
                    Intent fb_intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://instagram.com/_u/"+intagram_page_id));
                    startActivity(fb_intent);
                } catch (Exception e) {
                    Intent fb_intent2 = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/"+intagram_page_id));
                    startActivity(fb_intent2);
                }
                break;

        }

        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.yo_content, fragment);
            ft.commit();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    public void onFragmentInteraction_discography(Uri uri) {

    }

    @Override
    public void onFragmentInteraction_news(Uri uri) {

    }
    @Override
    public void onFragmentInteraction_jam(Uri uri) {

    }

    @Override
    public void onFragmentInteraction_settings(Uri uri) {

    }
    @Override
    public void onFragmentInteraction_disclaimer(Uri uri) {

    }

    public void startService() {
        Intent serviceIntent = new Intent(MainActivity.this, NotificationService.class);
        serviceIntent.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
        startService(serviceIntent);
    }
    public void stopService() {
        Intent serviceIntent = new Intent(MainActivity.this, NotificationService.class);
        serviceIntent.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
        stopService(serviceIntent);
    }
    @Override
    public void onResume(){
        super.onResume();
        stopService();
    }
    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (doubleBackToExitPressedOnce) {

                //super.onBackPressed();
                finish();
                System.exit(0);
                return;
            }

            this.doubleBackToExitPressedOnce = true;

            final Snackbar sb =  Snackbar.make(this.findViewById(android.R.id.content), "Please click BACK again to exit", Snackbar.LENGTH_LONG).setActionTextColor(Color.WHITE).setDuration(2000);

            //sb.setAction("", null);
            sb.show();

            //Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce=false;
                }
            }, 2000);
        }
    }
}
