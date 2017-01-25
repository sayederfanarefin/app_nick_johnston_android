package com.neonsofts.www.nemesis;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;

import com.google.android.gms.maps.model.MarkerOptions;
import com.neonsofts.www.nemesis.Values.BandValues;

import org.json.JSONException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class FragmentGoogleMap extends Fragment implements OnMapReadyCallback {

    public boolean attached = false;
    AsyncTask<Void, Void, Void> task;
    private MapView mapView;
    boolean isdatabasePopulated;
    SharedPreferences settings;
    BandValues nemesis_values;
    SQLiteDatabase db;
    database_guy dbg;
    Api api;
    private  com.google.android.gms.maps.GoogleMap  googleMap;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settings = this.getActivity().getSharedPreferences("LEL", 0);

        db = SQLiteDatabase.openDatabase(getActivity().getApplicationContext().getDatabasePath("nemesis_local_db").toString(), null, 0);
        isdatabasePopulated = settings.getBoolean("isPoluted", false);
        //this is to store the last visited fragment of the user
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("fragment_name", "Maps");
        editor.commit();
        dbg = new database_guy(getActivity().getApplicationContext().getDatabasePath("nemesis_local_db").toString(), "concerts");
        nemesis_values = new BandValues();

    }

    private int loadFromDb(){
        int concerts_displayed = 0;

        Calendar cc = Calendar.getInstance();
        SimpleDateFormat dfDate_day= new SimpleDateFormat("yyyy-MM-dd");
        String today="";
        Date date1;
        today=dfDate_day.format(cc.getTime());
        try {
             date1 = dfDate_day.parse(today);
            concert_unit cu[] = dbg.load_concerts();

            for (int x =0 ; x < cu.length; x++){

                Date date2;

                try {
                    date2 = dfDate_day.parse(cu[x].date_) ;

                    if (date1.equals(date2)) {
                        LatLng sydney = new LatLng(Double.valueOf(cu[x].concert_lat), Double.valueOf(cu[x].concert_lon));
                        googleMap.addMarker(new MarkerOptions().position(sydney).title(cu[x].concert_title).snippet("Today!").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                     }
                    if (date2.after(date1)) {
                        SimpleDateFormat dfDate_date = new SimpleDateFormat("dd");
                        String datee = dfDate_date.format(date2);

                        SimpleDateFormat dfDate_rest_of_date = new SimpleDateFormat("MMM, yyyy");
                        String datee_rest = dfDate_rest_of_date.format(date2);

                        String date_dekhanor_moto = datee + getDateSuffix(Integer.valueOf(datee)) + " " + datee_rest;

                        LatLng sydney = new LatLng(Double.valueOf(cu[x].concert_lat), Double.valueOf(cu[x].concert_lon));
                        googleMap.addMarker(new MarkerOptions().position(sydney).title(cu[x].concert_title).snippet(date_dekhanor_moto).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                        concerts_displayed++;
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            googleMap.setMinZoomPreference(11);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        /*
        LatLng sydney = new LatLng(-34, 151);
        googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney").snippet("digidi das!").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));


        LatLng sydney2 = new LatLng(-94, 111);
        googleMap.addMarker(new MarkerOptions().position(sydney2).title("Marker in Sydney2").snippet("digidi das!").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney2));
        */
        return concerts_displayed;
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
    private View fragmentView_map;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentView_map = inflater.inflate(R.layout.fragment_google_map, container,false);
           mapView = (MapView) fragmentView_map.findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);

        NetworkChangeReceiver ncreceiver = new NetworkChangeReceiver(); // Create the receiver
        getActivity().registerReceiver(ncreceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE")); // Register receiver



        return fragmentView_map;
    }

    @Override
    public void onMapReady(com.google.android.gms.maps.GoogleMap  map) {

        googleMap = map;

        load();
        loadFromDb();

    }

    public void load(){
        Handler myHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        // calling to this function from other pleaces
                        // The notice call method of doing things
                        if(api.get_json()!=null) {
                            if(attached){
                            boolean error = false;
                            try {

                                if (api.get_json().get("Exception").equals("-666")) {
                                    error = true;
                                }
                            } catch (JSONException e) {

                            }
                            if (error) {
                                if (Integer.valueOf(dbg.count) == 0) {
                                    final Snackbar sb = Snackbar.make(getView(), "Nothing to display!", Snackbar.LENGTH_LONG).setActionTextColor(Color.WHITE);
                                    sb.setAction("Dismiss", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            sb.dismiss();
                                        }
                                    });
                                    sb.show();
                                } else {

                                    final Snackbar sb = Snackbar.make(getView(), "Sorry! We couldn't update.", Snackbar.LENGTH_LONG).setActionTextColor(Color.WHITE);

                                    sb.setAction("Dismiss", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            sb.dismiss();
                                        }
                                    });
                                    sb.show();

                                }

                            } else {
                                dbg.save(api.get_json());


                                if (loadFromDb() == 0) {
                                    final Snackbar sb = Snackbar.make(getView(), "No upcoming gigs!", Snackbar.LENGTH_LONG).setActionTextColor(Color.WHITE);
                                    sb.setAction("Dismiss", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            sb.dismiss();
                                        }
                                    });
                                    sb.show();
                                } else {

                                    final Snackbar sb = Snackbar.make(getView(), "Gigs updated!", Snackbar.LENGTH_LONG).setActionTextColor(Color.WHITE);

                                    sb.setAction("Dismiss", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            sb.dismiss();
                                        }
                                    });
                                    sb.show();
                                }
                            }
                        }
                        }else{

                            final Snackbar sb =  Snackbar.make(getView(), "Gigs are up to date!", Snackbar.LENGTH_LONG).setActionTextColor(Color.WHITE);

                            sb.setAction("Dismiss", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    sb.dismiss();
                                }
                            });
                            sb.show();


                        }
                        break;
                    default:
                        break;
                }
            }
        };
        if(dbg.count == 0){
            api = new Api(myHandler,getResources().getString(R.string.api_base), getResources().getString(R.string.api_table_concert), null, null, null);
        }else{
            api = new Api(myHandler, getResources().getString(R.string.api_base), getResources().getString(R.string.api_table_concert), dbg.getLastInsertedId(), null, null);
        }
    }


    public class NetworkChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive( final Context context, Intent intent )
        {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService( Context.CONNECTIVITY_SERVICE );

            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            if(activeNetworkInfo != null && activeNetworkInfo.isConnected()&& attached){

                load();
            }
        }
    }
    @Override
    public void onDetach(){
        super.onDetach();
        attached = false;
    }


}
