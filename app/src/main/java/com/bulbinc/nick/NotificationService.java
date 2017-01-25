package com.bulbinc.nick;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.app.Service;
import android.content.Intent;
import android.app.Notification;
import android.app.PendingIntent;
import android.view.View;
import android.widget.RemoteViews;

public class NotificationService extends Service {

    int playOrPause = 1; //0=pause, 1 =play
    RemoteViews bigViews, views;
    SharedPreferences sharedPreferences;
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction().equals(Constants.ACTION.STARTFOREGROUND_ACTION)) {
            showNotification();

        } else if (intent.getAction().equals(Constants.ACTION.PREV_ACTION)) {

            Intent in = new Intent("mediaplayeractionfromnotification");
            in.putExtra("command","backward");
            sendBroadcast(in);
            views.setTextViewText(R.id.status_bar_track_name, sharedPreferences.getString("media_player_current_music_title", "Awesome"));
            bigViews.setTextViewText(R.id.status_bar_track_name, sharedPreferences.getString("media_player_current_music_title", "Awesome"));

            bigViews.setTextViewText(R.id.status_bar_album_name, sharedPreferences.getString("media_player_current_music_album", "Awesome"));


        } else if (intent.getAction().equals(Constants.ACTION.PLAY_ACTION)) {

            if(playOrPause == 0){
                playOrPause = 1;


                Intent in = new Intent("mediaplayeractionfromnotification");
                in.putExtra("command","play");
                sendBroadcast(in);
            }else{
                playOrPause = 0;


                Intent in = new Intent("mediaplayeractionfromnotification");
                in.putExtra("command","pause");
                sendBroadcast(in);
            }

        } else if (intent.getAction().equals(Constants.ACTION.NEXT_ACTION)) {

            Intent in = new Intent("mediaplayeractionfromnotification");
            in.putExtra("command","forward");
            sendBroadcast(in);
            views.setTextViewText(R.id.status_bar_track_name, sharedPreferences.getString("media_player_current_music_title", "Awesome"));
            bigViews.setTextViewText(R.id.status_bar_track_name, sharedPreferences.getString("media_player_current_music_title", "Awesome"));

            bigViews.setTextViewText(R.id.status_bar_album_name, sharedPreferences.getString("media_player_current_music_album", "Awesome"));


        } else if (intent.getAction().equals(
                Constants.ACTION.STOPFOREGROUND_ACTION)) {
            Intent in = new Intent("mediaplayeractionfromnotification");
            in.putExtra("command","stop");
            sendBroadcast(in);
            stopForeground(true);
            stopSelf();
        }
        return START_STICKY;
    }

        Notification status;
        private final String LOG_TAG = "NotificationService";

        private void showNotification() {
            sharedPreferences =  this.getSharedPreferences("LEL", 0);

// Using RemoteViews to bind custom layouts into Notification
             views = new RemoteViews(getPackageName(),
                    R.layout.status_bar);
             bigViews = new RemoteViews(getPackageName(),
                    R.layout.status_bar_expanded);

// showing default album image
            views.setViewVisibility(R.id.status_bar_icon, View.VISIBLE);
            views.setViewVisibility(R.id.status_bar_album_art, View.GONE);


            bigViews.setImageViewBitmap(R.id.status_bar_album_art, Constants.getDefaultAlbumArt(this,sharedPreferences.getString("album_art_local", "na")));

/*

            Intent notificationIntent = new Intent(this, MainActivity.class);
          notificationIntent.setAction(Constants.ACTION.MAIN_ACTION);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                    notificationIntent, 0);

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("fragment_name", "Music");
            editor.commit();
*/
            Intent previousIntent = new Intent(this, NotificationService.class);
            previousIntent.setAction(Constants.ACTION.PREV_ACTION);
            PendingIntent ppreviousIntent = PendingIntent.getService(this, 0,
                    previousIntent, 0);

            Intent playIntent = new Intent(this, NotificationService.class);
            playIntent.setAction(Constants.ACTION.PLAY_ACTION);
            PendingIntent pplayIntent = PendingIntent.getService(this, 0,
                    playIntent, 0);

            Intent nextIntent = new Intent(this, NotificationService.class);
            nextIntent.setAction(Constants.ACTION.NEXT_ACTION);
            PendingIntent pnextIntent = PendingIntent.getService(this, 0,
                    nextIntent, 0);

            Intent closeIntent = new Intent(this, NotificationService.class);
            closeIntent.setAction(Constants.ACTION.STOPFOREGROUND_ACTION);
            PendingIntent pcloseIntent = PendingIntent.getService(this, 0,
                    closeIntent, 0);

            views.setOnClickPendingIntent(R.id.status_bar_play, pplayIntent);
            bigViews.setOnClickPendingIntent(R.id.status_bar_play, pplayIntent);

            views.setOnClickPendingIntent(R.id.status_bar_next, pnextIntent);
            bigViews.setOnClickPendingIntent(R.id.status_bar_next, pnextIntent);

            views.setOnClickPendingIntent(R.id.status_bar_prev, ppreviousIntent);
            bigViews.setOnClickPendingIntent(R.id.status_bar_prev, ppreviousIntent);

            views.setOnClickPendingIntent(R.id.status_bar_collapse, pcloseIntent);
            bigViews.setOnClickPendingIntent(R.id.status_bar_collapse, pcloseIntent);

            if(sharedPreferences.getString("media_player_state", "Awesome").equals("playing")){
                views.setImageViewResource(R.id.status_bar_play,
                        R.mipmap.nemesis_pause_fill);
                bigViews.setImageViewResource(R.id.status_bar_play,
                        R.mipmap.nemesis_pause_fill);
            }else if(sharedPreferences.getString("media_player_state", "Awesome").equals("paused")){
                views.setImageViewResource(R.id.status_bar_play,
                        R.mipmap.nemesis_play_fill);
                bigViews.setImageViewResource(R.id.status_bar_play,
                        R.mipmap.nemesis_play_fill);
            }else{

            }


            views.setTextViewText(R.id.status_bar_track_name, sharedPreferences.getString("media_player_current_music_title", "Awesome"));
            bigViews.setTextViewText(R.id.status_bar_track_name, sharedPreferences.getString("media_player_current_music_title", "Awesome"));

            bigViews.setTextViewText(R.id.status_bar_album_name, sharedPreferences.getString("media_player_current_music_album", "Awesome"));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                status = new Notification.Builder(this).build();
            }
            status.contentView = views;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                status.bigContentView = bigViews;
            }
            status.flags = Notification.FLAG_ONGOING_EVENT;
            status.icon = R.mipmap.ic_launcher;
            //status.contentIntent = pendingIntent;
            startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, status);
        }

}