package com.bulbinc.nick;

import android.support.v4.app.Fragment;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.bulbinc.nick.Values.BandValues;

import org.json.JSONException;

import java.util.concurrent.TimeUnit;


public class FragmentMusic extends Fragment {
    public boolean attached = false;
    private MediaPlayerService player;
    boolean serviceBound = false;
    Handler h;
    Music current_music;
AlbumAdapter current_albumAdapter;
    Handler seekBarHandler = new Handler();
    Boolean isMediaPlayerDataSourceSet = false ;
    int playOrPause = 0;
    ListView albums_music_list_view;

    private OnFragmentInteractionListener mListener;
    database_guy dbg_music;
    database_guy dbg_album;
    Api api;
    private View empty_view;
    String last_album_id;
    BandValues nemesis_values;
    boolean isdatabasePopulated;
    SharedPreferences settings;
    TextView loading_text, mediaPlayer_duration, mediaPlayer_current_time, mediaPlayer_title;
    ProgressBar loading;
    ImageView  back_button, play,forward, backward;
    TextView album_title;
    SeekBar seekBar;
    public NotificationManager notificationManager;
    AdView mAdView;
    private BroadcastReceiver _refreshReceiver = new MediaPlayerControlReceiver();

    boolean music_selected = false;

    public FragmentMusic() {
        // Required empty public constructor
    }
    public static FragmentMusic newInstance(String param1, String param2) {
        FragmentMusic fragment = new FragmentMusic();
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         settings = this.getActivity().getSharedPreferences("LEL", 0);
        isdatabasePopulated = settings.getBoolean("isPoluted", false);
        dbg_music = new database_guy(getActivity().getApplicationContext().getDatabasePath("nemesis_local_db").toString(), "music");
        dbg_album = new database_guy(getActivity().getApplicationContext().getDatabasePath("nemesis_local_db").toString(), "album");
        //this is to store the last visited fragment of the user
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("fragment_name", "Music");
        editor.commit();
        nemesis_values = new BandValues();

        IntentFilter filter = new IntentFilter("mediaplayeractionfromnotification");
        getActivity().registerReceiver(_refreshReceiver, filter);

    }

    private View fragmentView_music_album;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
     Bundle savedInstanceState) {

        notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);

        fragmentView_music_album = inflater.inflate(R.layout.fragment_music, container, false);

        empty_view = fragmentView_music_album.findViewById(R.id.error_music_fragment);

        albums_music_list_view = (ListView) fragmentView_music_album.findViewById(R.id.listView_album_music);
        loading = (ProgressBar) fragmentView_music_album.findViewById(R.id.progressBar_newsList);
        loading_text = (TextView) fragmentView_music_album.findViewById(R.id.loading_textview);
        //expand_button = (ImageView) fragmentView_music_album.findViewById(R.id.expand_button);
        back_button = (ImageView) fragmentView_music_album.findViewById(R.id.back_button);
        album_title = (TextView) fragmentView_music_album.findViewById(R.id.album_title_text_view);
        mediaPlayer_title = (TextView) fragmentView_music_album.findViewById(R.id.music_title_displayer);
        mediaPlayer_duration = (TextView) fragmentView_music_album.findViewById(R.id.music_timetotal);
        mediaPlayer_current_time = (TextView) fragmentView_music_album.findViewById(R.id.music_timeleft);
        play = (ImageView) fragmentView_music_album.findViewById(R.id.playpause_button);
        forward = (ImageView) fragmentView_music_album.findViewById(R.id.forward_button);
        backward = (ImageView) fragmentView_music_album.findViewById(R.id.backward_button);
        seekBar = (SeekBar) fragmentView_music_album.findViewById(R.id.seekBar);

        mAdView = (AdView) fragmentView_music_album.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mAdView.loadAd(adRequest);


        loading_text.setVisibility(View.VISIBLE);
        loading_text.setText("loading...");
        loading.setVisibility(View.VISIBLE);
        back_button.setVisibility(View.INVISIBLE);

        AlbumAdapter albumAdapter = new AlbumAdapter(getActivity(), R.layout.album_layout_unit);
        albumAdapter = dbg_album.load(albumAdapter);
        albums_music_list_view.setEmptyView(empty_view);
        albums_music_list_view.setAdapter(albumAdapter);
        current_albumAdapter = albumAdapter;
        back_button.setVisibility(View.INVISIBLE);


        albums_music_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String type = parent.getItemAtPosition(position).getClass().toString();
                if(type.contains("Album")){
                    //album
                    Album a = (Album) parent.getItemAtPosition(position);

                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("album_art_local", a.album_art_local);
                    editor.commit();

                    MusicAdapter musicAdapter = new MusicAdapter(getActivity(), R.layout.music_layout_unit);
                    musicAdapter = dbg_music.load(musicAdapter, a.id);
                    albums_music_list_view.setAdapter(musicAdapter);
                    music_array = adapterToMusicArray(musicAdapter);
                    last_album_id = a.id;
                    music_selected = true;

                    sync_save_load_music(last_album_id);
                    back_button.setVisibility(View.VISIBLE);

                }else{
                    //music
                    final Music m = (Music) parent.getItemAtPosition(position);
                    playFromUrl(m);
                }
            }
        });

        loading_text.setText("");
        loading_text.setText("updating...");



        sync_save_load_album();

        NetworkChangeReceiver ncreceiver = new NetworkChangeReceiver(); // Create the receiver
        getActivity().registerReceiver(ncreceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE")); // Register receiver

        play.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if(playOrPause == 0){
                            hardPlay();
                        }else{
                            hardPause();
                        }
                    }

                });

            }
        });

        h = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        loading_text.setVisibility(View.INVISIBLE);
                        loading_text.setText("");
                        loading.setVisibility(View.INVISIBLE);

                        seekBar.setMax(player.getDuration());
                        mediaPlayer_duration.setText(milisecToTime(player.getDuration()));
                        seekBarHandler.postDelayed(UpdateSongTime, 100);
                        break;
                    default:
                        break;
                }
            }
        };

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser && isMediaPlayerDataSourceSet){
                    player.seekTo(progress);
                }else if(fromUser && !isMediaPlayerDataSourceSet){
                    seekBar.setProgress(0);
                }
            }
        });

        forward.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                hardForward();
            }
        });

        backward.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                hardBackward();
            }
        });


        return fragmentView_music_album;
    }

    private Runnable UpdateSongTime = new Runnable() {
        public void run() {
            int mCurrentPosition = player.getCurrentPosition();

            mediaPlayer_current_time.setText(milisecToTime(mCurrentPosition));
            seekBar.setProgress((int)mCurrentPosition);
            seekBarHandler.postDelayed(this, 100);
        }
    };
    private String milisecToTime(int mCurrentPosition){
        return String.format("%02d:%02d",

                TimeUnit.MILLISECONDS.toMinutes((long) mCurrentPosition),
                TimeUnit.MILLISECONDS.toSeconds((long) mCurrentPosition) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                toMinutes((long) mCurrentPosition)));

    }
    Music music_array[];

    private Music[] adapterToMusicArray(MusicAdapter musicAdapter){
        Music music_array2[] = new Music[musicAdapter.getCount()];
        int count = 0;
        for(int kk =0; kk < musicAdapter.getCount(); kk++){
            music_array2[count] = musicAdapter.getItem(kk);
            count++;
        }
        return music_array2;
    }
    private void sync_save_load_music(final String album_id){
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back_button.setVisibility(View.INVISIBLE);
                albums_music_list_view.setAdapter(current_albumAdapter);
            }
        });
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
                            } catch (JSONException e) {}
                            if(error){
                                if(Integer.valueOf(dbg_music.count) == 0){
                                    loading_text.setText("");
                                    loading.setVisibility(View.INVISIBLE);

                                }else {
                                    loading_text.setText("Waiting for network...");
                                    loading.setVisibility(View.VISIBLE);
                                    final Snackbar sb =  Snackbar.make(getView(), "Sorry! We couldn't update.", Snackbar.LENGTH_LONG).setActionTextColor(Color.WHITE);
                                    sb.setAction("Dismiss", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            sb.dismiss();
                                        }
                                    });
                                    sb.show();
                                }
                            }else{
                                dbg_music.save(api.get_json());
                                MusicAdapter musicAdapter = new MusicAdapter(getActivity(), R.layout.music_layout_unit);
                                musicAdapter = dbg_music.load(musicAdapter, album_id);
                                albums_music_list_view.setAdapter(musicAdapter);
                                music_array = adapterToMusicArray(musicAdapter);
                                loading_text.setVisibility(View.INVISIBLE);
                                loading_text.setText("");
                                loading.setVisibility(View.INVISIBLE);

                                final Snackbar sb =  Snackbar.make(getView(), "Music updated!", Snackbar.LENGTH_LONG).setActionTextColor(Color.WHITE);

                                sb.setAction("Dismiss", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        sb.dismiss();
                                    }
                                });
                                sb.show();

                                Handler myHandler2 = new Handler() {
                                    @Override
                                    public void handleMessage(Message msg) {
                                    switch (msg.what) {
                                        case 0:
                                            //load shit
                                            if(attached) {
                                                MusicAdapter musicAdapter = new MusicAdapter(getActivity(), R.layout.music_layout_unit);
                                                musicAdapter = dbg_music.load(musicAdapter, album_id);
                                                albums_music_list_view.setAdapter(musicAdapter);
                                                music_array = adapterToMusicArray(musicAdapter);
                                            }
                                            break;
                                        default:
                                            break;
                                    }
                                    }
                                };
                                dbg_music.setActivity(getActivity());
                                dbg_music.cache("url", "source_local", api.get_json(), myHandler2);
                            }
                        }else{
                            loading_text.setVisibility(View.INVISIBLE);
                            loading_text.setText("");
                            loading.setVisibility(View.INVISIBLE);
                            final Snackbar sb =  Snackbar.make(getView(), "Music is up to date!", Snackbar.LENGTH_LONG).setActionTextColor(Color.WHITE);

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
        if(dbg_music.count == 0){
            api = new Api(myHandler,getResources().getString(R.string.api_base), getResources().getString(R.string.api_table_music), null, null, null);
        }else{
            api = new Api(myHandler, getResources().getString(R.string.api_base), getResources().getString(R.string.api_table_music), dbg_album.getLastInsertedId(), null, null);
        }

    }

    private void sync_save_load_album(){

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
                        if(error && attached){

                            if(Integer.valueOf(dbg_album.count) == 0){
                                loading.setVisibility(View.INVISIBLE);
                                loading_text.setText("");
                                loading_text.setVisibility(View.INVISIBLE);
                            }else {
                                loading_text.setText("Waiting for network...");
                                loading.setVisibility(View.VISIBLE);

                                final Snackbar sb =  Snackbar.make(getView(), "Sorry! We couldn't update.", Snackbar.LENGTH_LONG).setActionTextColor(Color.WHITE);

                                sb.setAction("Dismiss", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        sb.dismiss();
                                    }
                                });
                                sb.show();

                            }

                        }else{
                            dbg_album.save(api.get_json());
                            if(attached) {
                                AlbumAdapter albumAdapter2 = new AlbumAdapter(getActivity(), R.layout.album_layout_unit);
                                albumAdapter2 = dbg_album.load(albumAdapter2);
                                current_albumAdapter = albumAdapter2;
                                albums_music_list_view.setAdapter(albumAdapter2);
                                back_button.setVisibility(View.INVISIBLE);

                                loading_text.setVisibility(View.INVISIBLE);
                                loading_text.setText("");
                                loading.setVisibility(View.INVISIBLE);

                                final Snackbar sb = Snackbar.make(getView(), "Albums updated!", Snackbar.LENGTH_LONG).setActionTextColor(Color.WHITE);

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
                                            //load shit
                                            if(attached) {
                                                AlbumAdapter albumAdapter2 = new AlbumAdapter(getActivity(), R.layout.album_layout_unit);
                                                albumAdapter2 = dbg_album.load(albumAdapter2);
                                                current_albumAdapter = albumAdapter2;
                                                albums_music_list_view.setAdapter(albumAdapter2);

                                                back_button.setVisibility(View.INVISIBLE);
                                            }
                                            break;
                                        default:
                                            break;
                                    }
                                }
                            };
                            dbg_album.setActivity(getActivity());
                            dbg_album.cache("album_art_url", "album_art_local", api.get_json(), myHandler2);
                        }
                    }else{
                        if(attached) {
                            loading_text.setVisibility(View.INVISIBLE);
                            loading_text.setText("");
                            loading.setVisibility(View.INVISIBLE);
                            final Snackbar sb = Snackbar.make(getView(), "Albums up to date!", Snackbar.LENGTH_LONG).setActionTextColor(Color.WHITE);

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
    if(dbg_album.count == 0){
        api = new Api(myHandler,getResources().getString(R.string.api_base), getResources().getString(R.string.api_table_albums), null, null, null);
    }else{
        api = new Api(myHandler, getResources().getString(R.string.api_base), getResources().getString(R.string.api_table_albums), dbg_album.getLastInsertedId(), null, null);
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

        if (serviceBound) {
            getActivity().unbindService(serviceConnection);
            //service is active
            player.dismiss();
        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction_news(Uri uri);
    }



    public class MediaPlayerControlReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String value=intent.getStringExtra("command");
            if(value.equals("play")){
                hardPlay();
                startService();
            } else if (value.equals("pause")){
                hardPause();
                startService();
            }else if (value.equals("forward")){
                hardForward();
            }else if (value.equals("backward")){
                hardBackward();
            }else if (value.equals("stop")){
                hardPause();
                if (serviceBound) {
                    getActivity().unbindService(serviceConnection);
                    //service is active
                    player.dismiss();
                }
            }
        }
    }

    public void startService() {
        //notificationManager.cancelAll();
        Intent serviceIntent = new Intent(getActivity(), NotificationService.class);
        serviceIntent.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
        getActivity().startService(serviceIntent);
    }

    public class NetworkChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive( final Context context, Intent intent )
        {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService( Context.CONNECTIVITY_SERVICE );

            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            if(activeNetworkInfo != null && activeNetworkInfo.isConnected() && attached){
                if(music_selected){
                    sync_save_load_music(last_album_id);
                }else{
                    sync_save_load_album();
                }
            }
        }
    }



    public void hardPlay(){
        if(isMediaPlayerDataSourceSet) {
            play.setImageResource(R.mipmap.nemesis_pause_fill);
            playOrPause = 1;
            player.resumeMedia();
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("media_player_state", "playing");
            editor.commit();
            seekBarHandler.postDelayed(UpdateSongTime, 100);
        }else{

           final Snackbar sb =  Snackbar.make(getView(), "Please select a song to play!", Snackbar.LENGTH_LONG).setActionTextColor(Color.WHITE);
            sb.setAction("Dismiss", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sb.dismiss();
                }
            });
            sb.show();

        }

    }
    public void hardPause(){
        play.setImageResource(R.mipmap.nemesis_play_fill);
        playOrPause = 0;
        player.pauseMedia();
        loading.setVisibility(View.INVISIBLE);
        loading_text.setVisibility(View.INVISIBLE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("media_player_state", "paused");
        editor.commit();
    }
    public void hardForward(){
        if(isMediaPlayerDataSourceSet){
            Boolean found =false;
            for (int i =0; i < music_array.length; i++){
                if(music_array[i].track_no.equals(String.valueOf(Integer.valueOf(current_music.track_no)+1))){
                    playFromUrl(music_array[i]);
                    found = true;
                    break;
                }
            }
            if(!found){
                playFromUrl(music_array[0]);
                found = true;
            }
        }else{
            final Snackbar sb =  Snackbar.make(getView(), "Please select a song first!", Snackbar.LENGTH_LONG).setActionTextColor(Color.WHITE);
            sb.setAction("Dismiss", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sb.dismiss();
                }
            });
            sb.show();
        }
    }

    public void hardBackward(){

        if(isMediaPlayerDataSourceSet) {
            Boolean found = false;
            for (int i = 0; i < music_array.length; i++) {
                if (music_array[i].track_no.equals(String.valueOf(Integer.valueOf(current_music.track_no) - 1))) {
                    playFromUrl(music_array[i]);
                    found = true;
                    break;
                }
            }
            if (!found) {
                playFromUrl(music_array[music_array.length - 1]);
                found = true;
            }
        }else{
            final Snackbar sb =  Snackbar.make(getView(), "Please select a song first!", Snackbar.LENGTH_LONG).setActionTextColor(Color.WHITE);
            sb.setAction("Dismiss", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sb.dismiss();
                }
            });
            sb.show();
        }
    }

    private void playFromUrl(final Music m) {
        if(isNetworkAvailable()){
            current_music = m;
            playOrPause = 1;
            isMediaPlayerDataSourceSet = true;
            Runnable music_loader_ui = new Runnable() {
                @Override
                public void run() {
                loading_text.setVisibility(View.VISIBLE);
                loading_text.setText("Buffering...");
                loading.setVisibility(View.VISIBLE);
                    play.setImageResource(R.mipmap.nemesis_pause_fill);
                    mediaPlayer_title.setText(m.title);
                }
            };
            getActivity().runOnUiThread(music_loader_ui);
            playAudio(filter_url(m.source_url));
            seekBarHandler.postDelayed(UpdateSongTime,100);

            SharedPreferences.Editor editor = settings.edit();
            editor.putString("media_player_state", "playing");
            editor.putString("media_player_current_music_title", m.title);
            editor.putString("media_player_current_music_id", m.id);

            Cursor cursor = dbg_album.run_query("SELECT album_title FROM album WHERE id='" + m.album_id + "'");
            String data[] = new String[cursor.getCount()];
            int count = 0;
            while (cursor.moveToNext()) {
                data[count] = cursor.getString(0);
                count++;
            }
            cursor.close();
            editor.putString("media_player_current_music_album", data[count-1]);
            editor.commit();

        }else{
            Runnable music_loader_ui = new Runnable() {
                @Override
                public void run() {
                loading_text.setVisibility(View.VISIBLE);
                loading_text.setText("Waiting for network...");
                loading.setVisibility(View.VISIBLE);
                final Snackbar sb =  Snackbar.make(getView(), "Sorry! We couldn't connect.", Snackbar.LENGTH_LONG).setActionTextColor(Color.WHITE);
                sb.setAction("Dismiss", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sb.dismiss();
                    }
                });
                sb.show();
                }

            };
            getActivity().runOnUiThread(music_loader_ui);
        }
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private String filter_url(String url){
        url = url.replace("\\", "");
        return url;
    }

    private void playAudio(String media) {
        //Check is service is active
        if (!serviceBound) {
            Intent playerIntent = new Intent(getActivity(), MediaPlayerService.class);
            playerIntent.putExtra("media", media);
            getActivity().startService(playerIntent);
            getActivity().bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        } else {
            //Service is active
            //Send media with BroadcastReceiver
            player.newUrlStream(media);
        }
    }
    //Binding this Client to the AudioPlayer Service
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            MediaPlayerService.LocalBinder binder = (MediaPlayerService.LocalBinder) service;
            player = binder.getService();
            serviceBound = true;
            player.setHandler(h);
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
        }
    };

}
