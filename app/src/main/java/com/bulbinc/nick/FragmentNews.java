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
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bulbinc.nick.Values.BandValues;

import org.json.JSONException;


public class FragmentNews extends Fragment {

    private NetworkChangeReceiver ncreceiver;
    private database_guy dbg;
    private Api api;
    private ListView news_list_view;
    private OnFragmentInteractionListener mListener;
    private BandValues nemesis_values;
    private View empty_view;
    SharedPreferences settings;
    TextView loading_text;
    ProgressBar loading;
    public boolean attached = false;
    public FragmentNews() {
        // Required empty public constructor
    }
    public static FragmentNews newInstance(String param1, String param2) {
        FragmentNews fragment = new FragmentNews();
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbg = new database_guy(getActivity().getApplicationContext().getDatabasePath("nemesis_local_db").toString(), "news");
        settings = this.getActivity().getSharedPreferences("LEL", 0);
        //this is to store the last visited fragment of the user
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("fragment_name", "News");
        editor.commit();
        nemesis_values = new BandValues();

    }
    private View fragmentView_latest_news;
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
     Bundle savedInstanceState) {
        fragmentView_latest_news = inflater.inflate(R.layout.fragment_news, container, false);
        empty_view = fragmentView_latest_news.findViewById(R.id.error);

        news_list_view = (ListView) fragmentView_latest_news.findViewById(R.id.listView_news);
        loading = (ProgressBar) fragmentView_latest_news.findViewById(R.id.progressBar_newsList);
        loading_text = (TextView) fragmentView_latest_news.findViewById(R.id.loading_textview);
        loading_text.setVisibility(View.VISIBLE);
        loading_text.setText("loading...");
        loading.setVisibility(View.VISIBLE);
        //if this is not the first run, meaning the database already contains some News and we are just getting them and showing nthem here.
        NewsAdapter newsAdapter = new NewsAdapter(getActivity(), R.layout.news_layout_unit);
        newsAdapter = dbg.load(newsAdapter);
        news_list_view.setEmptyView(empty_view);
            news_list_view.setAdapter(newsAdapter);
            news_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                News n = (News) parent.getItemAtPosition(position);
                Intent intent = new Intent(getActivity(), ScrollingActivity_news.class);
                intent.putExtra("news_id", n.id);
                intent.putExtra("news_title", n.title);
                intent.putExtra("news_body", n.body);
                intent.putExtra("news_date", n.date);
                intent.putExtra("image_url", n.url);
                intent.putExtra("local_image_url", n.local_url);


                    startActivity(intent);

                }
            });
            loading_text.setText("");
        if(attached){
            load();
        }

        ncreceiver = new NetworkChangeReceiver(); // Create the receiver
        getActivity().registerReceiver(ncreceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE")); // Register receiver

        return fragmentView_latest_news;
    }

    public void load(){
        loading_text.setText("updating...");


        Handler myHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:

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
                                    if (Integer.valueOf(dbg.count) == 0) {

                                        final Snackbar sb = Snackbar.make(getView(), "Sorry! We couldn't update.", Snackbar.LENGTH_LONG).setActionTextColor(Color.WHITE);

                                        sb.setAction("Dismiss", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                sb.dismiss();
                                            }
                                        });
                                        sb.show();


                                    } else {
                                        loading_text.setText("Sorry! We couldn't connect");
                                        loading.setVisibility(View.INVISIBLE);

                                        final Snackbar sb = Snackbar.make(getView(), "Sorry! We couldn't update.", Snackbar.LENGTH_LONG).setActionTextColor(Color.WHITE);

                                        sb.setAction("Dismiss", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                sb.dismiss();
                                            }
                                        });
                                        sb.show();

                                    }
                                }

                            }else{
                                dbg.save(api.get_json());

                                if(attached) {
                                    NewsAdapter newsAdapter2 = new NewsAdapter(getActivity(), R.layout.news_layout_unit);
                                    newsAdapter2 = dbg.load(newsAdapter2);
                                    news_list_view.setAdapter(newsAdapter2);
                                    loading_text.setVisibility(View.INVISIBLE);
                                    loading_text.setText("");
                                    loading.setVisibility(View.INVISIBLE);
                                    final Snackbar sb =  Snackbar.make(getView(), "News updated!", Snackbar.LENGTH_LONG).setActionTextColor(Color.WHITE);
                                    sb.setAction("Dismiss", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                        sb.dismiss();
                                        }
                                    });
                                    sb.show();
                                }
                                Handler myHandler2 = new Handler() {
                                    @Override
                                    public void handleMessage(Message msg) {
                                    switch (msg.what) {
                                        case 0:
                                            if(attached){
                                                NewsAdapter newsAdapter2 = new NewsAdapter(getActivity(), R.layout.news_layout_unit);
                                                newsAdapter2 = dbg.load(newsAdapter2);
                                                news_list_view.setAdapter(newsAdapter2);
                                            }
                                            break;
                                        default:
                                            break;
                                    }
                                    }
                                };
                                dbg.setActivity(getActivity());
                                dbg.cache("image_url", "local_image_url", api.get_json(), myHandler2);
                            }
                        }else{
                            if(attached) {
                                loading_text.setVisibility(View.INVISIBLE);
                                loading_text.setText("");
                                loading.setVisibility(View.INVISIBLE);
                                final Snackbar sb = Snackbar.make(getView(), "News is up to date!", Snackbar.LENGTH_LONG).setActionTextColor(Color.WHITE);

                                sb.setAction("Dismiss", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        sb.dismiss();
                                    }
                                });
                                sb.show();
                            }
                        }
                        break;
                    default:
                        break;
                }
            }
        };
        if(dbg.count == 0){
            api = new Api(myHandler,getResources().getString(R.string.api_base), getResources().getString(R.string.api_table_news), null, null, null);
        }else{
            api = new Api(myHandler, getResources().getString(R.string.api_base), getResources().getString(R.string.api_table_news), dbg.getLastInsertedId(), null, null);
        }
    }
     public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction_news(uri);
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
        mListener = null;
        api.cancle_api();
        attached = false;

    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction_news(Uri uri);
    }

    public class NetworkChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive( final Context context, Intent intent )
        {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService( Context.CONNECTIVITY_SERVICE );

            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            if(activeNetworkInfo != null && activeNetworkInfo.isConnected()){

                if(attached){
                    load();
                }
            }
        }
    }
}

