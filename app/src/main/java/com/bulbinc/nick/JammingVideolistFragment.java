package com.bulbinc.nick;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bulbinc.nick.Values.DeveloperKey;

import org.json.JSONException;


public class JammingVideolistFragment extends Fragment {
    NetworkChangeReceiver ncreceiver;
    database_guy dbg;
    Api api;
    private ListView jamming_VideoList;
    private View jammin_fragment_view;
    TextView loading_text;
    ProgressBar loading;
    public YoutubeVideosAdapter adapter;
    String YOUR_DEVELOPER_KEY;
    SharedPreferences settings;
    private View empty_view;
    private OnFragmentInteractionListener mListener;

    public boolean attached = false;
    public JammingVideolistFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settings = this.getActivity().getSharedPreferences("LEL", 0);
        DeveloperKey d = new DeveloperKey();
        YOUR_DEVELOPER_KEY = d.DEVELOPER_KEY;


        SharedPreferences.Editor editor = settings.edit();
        editor.putString("fragment_name", "jamming");
        editor.commit();
        dbg = new database_guy(getActivity().getApplicationContext().getDatabasePath("nemesis_local_db").toString(), "youtube_videos");

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        jammin_fragment_view = inflater.inflate(R.layout.fragment_videolist, container, false);
        empty_view = jammin_fragment_view.findViewById(R.id.error_videos_fragment);

        loading = (ProgressBar) jammin_fragment_view.findViewById(R.id.progressBar_jamList);
        loading_text = (TextView) jammin_fragment_view.findViewById(R.id.loading_jamming__textview);
        jamming_VideoList = (ListView) jammin_fragment_view.findViewById(R.id.jamming_video_listView);
        jamming_VideoList.setEmptyView(empty_view);
        loading_text.setVisibility(View.VISIBLE);
        loading_text.setText("loading...");
        loading.setVisibility(View.VISIBLE);

        jamming_VideoList.setAdapter(dbg.load(getActivity()));

        jamming_VideoList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                if(isNetworkAvailable()){
                    VideoEntry n = (VideoEntry) parent.getItemAtPosition(position);

                    Intent intent = new Intent(getActivity(), FullScreenYoutubePlayer.class);

                    intent.putExtra("jam_video_id", n.videoId);
                    startActivity(intent);

                }else{
                    final Snackbar sb =  Snackbar.make(getView(), "No internet connection. Please try again later.", Snackbar.LENGTH_LONG).setActionTextColor(Color.WHITE);

                    sb.setAction("Dismiss", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            sb.dismiss();
                        }
                    });
                    sb.show();
                }
            }
        });

        loading_text.setText("");
        load();


         ncreceiver = new NetworkChangeReceiver(); // Create the receiver
        getActivity().registerReceiver(ncreceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE")); // Register receiver


        return jammin_fragment_view;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction_jam(uri);
        }
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        attached = true;
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }
    @Override
    public void onDetach() {
        super.onDetach();
        attached = false;
        mListener = null;
        api.cancle_api();
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction_jam(Uri uri);
    }
    public void load(){
        loading_text.setText("updating...");


        Handler myHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        // calling to this function from other pleaces
                        // The notice call method of doing things
                        if(api.get_json()!=null){

                            boolean error = false;
                            try {

                                if(api.get_json().get("Exception").equals("-666")){
                                    error = true;
                                }
                            } catch (JSONException e) {

                            }
                            if(error){
                                if(attached) {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {

                                            loading_text.setText("Sorry! We couldn't connect");
                                            loading.setVisibility(View.INVISIBLE);

                                            final Snackbar sb = Snackbar.make(getView(), "Sorry! We couldn't connect.", Snackbar.LENGTH_LONG).setActionTextColor(Color.WHITE);

                                            sb.setAction("Dismiss", new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    sb.dismiss();
                                                }
                                            });
                                            sb.show();
                                        }
                                    });
                                }
                            }else{
                                dbg.save(api.get_json());
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        if(attached) {
                                            jamming_VideoList.setAdapter(dbg.load(getActivity()));
                                            loading_text.setVisibility(View.INVISIBLE);
                                            loading_text.setText("");
                                            loading.setVisibility(View.INVISIBLE);
                                            Context context = getActivity().getApplicationContext();


                                            final Snackbar sb = Snackbar.make(getView(), "Videos are updated!", Snackbar.LENGTH_LONG).setActionTextColor(Color.WHITE);

                                            sb.setAction("Dismiss", new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    sb.dismiss();
                                                }
                                            });
                                            sb.show();
                                        }
                                    }
                                });
                            }
                        }else{
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    loading_text.setVisibility(View.INVISIBLE);
                                    loading_text.setText("");
                                    loading.setVisibility(View.INVISIBLE);
                                    final Snackbar sb =  Snackbar.make(getView(), "Videos are up to date!", Snackbar.LENGTH_LONG).setActionTextColor(Color.WHITE);

                                    sb.setAction("Dismiss", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            sb.dismiss();
                                        }
                                    });
                                    sb.show();
                                }
                            });
                        }
                        break;
                    default:
                        break;
                }
            }
        };

        if(dbg.count == 0){
            api = new Api(myHandler,getResources().getString(R.string.api_base), getResources().getString(R.string.api_table_video), null, null, null);
        }else{
            api = new Api(myHandler, getResources().getString(R.string.api_base), getResources().getString(R.string.api_table_video), dbg.getLastInsertedId(), null, null);
        }
    }
    public class NetworkChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive( final Context context, Intent intent )
        {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService( Context.CONNECTIVITY_SERVICE );

            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            if(activeNetworkInfo != null && activeNetworkInfo.isConnected() && attached){
                load();
            }
        }
    }
}


