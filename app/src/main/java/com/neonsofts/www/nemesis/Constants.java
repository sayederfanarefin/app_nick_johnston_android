package com.neonsofts.www.nemesis;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Constants {
    public interface ACTION {
        public static String MAIN_ACTION = "com.neonsofts.www.nemesis.main";
        public static String INIT_ACTION = "com.neonsofts.www.nemesis.init";
        public static String PREV_ACTION = "com.neonsofts.www.nemesis.prev";
        public static String PLAY_ACTION = "com.neonsofts.www.nemesis.play";
        public static String NEXT_ACTION = "com.neonsofts.www.nemesis.next";
        public static String STARTFOREGROUND_ACTION = "com.neonsofts.www.nemesis.startforeground";
        public static String STOPFOREGROUND_ACTION = "com.neonsofts.www.nemesis.stopforeground";

    }

    public interface NOTIFICATION_ID {
        public static int FOREGROUND_SERVICE = 101;
    }

    public static Bitmap getDefaultAlbumArt(Context context) {
        Bitmap bm = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        try {
            bm = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.nemesis, options);
        } catch (Error ee) {
        } catch (Exception e) {
        }
        return bm;
    }
    public static Bitmap getDefaultAlbumArt(Context context, String album_art_local) {
        Bitmap bm = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        try {
            if(album_art_local.equals("na")){
                bm = BitmapFactory.decodeResource(context.getResources(),
                        R.drawable.nemesis, options);
            }else{
                bm = BitmapFactory.decodeFile(album_art_local);
            }
        } catch (Error ee) {
        } catch (Exception e) {
        }
        return bm;
    }


}