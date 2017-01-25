package com.bulbinc.nick;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by NewUsername on 12/30/2016.
 */

public class download_save_guy {

    Activity app_activity;
    String local_url = "na";
    Handler myHandler;

    public download_save_guy(Activity a){
        this.app_activity = a;

    }

    public void download(final String file_url, Handler h ){
        this.myHandler = h;
        try{

            new AsyncTask<Void, Void, String>() {
                String path_temp;
                @Override
                protected String doInBackground(Void... params) {
                    String file_name="666";
                    try {
                        URL url = new URL(filter_url(file_url));
                        HttpURLConnection conection = (HttpURLConnection) url.openConnection();
                        conection.connect();
                        // download the file
                        InputStream iStream = conection.getInputStream();
                        // Creating a bitmap from the downloaded inputstream
                        Bitmap b = BitmapFactory.decodeStream(iStream);
                        file_name = makeNameFromURL(file_url);
                        path_temp = saveToInternalStorage(b, file_name);
                        iStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return path_temp+"/"+file_name;
                }

                @Override
                protected void onPostExecute(String file_location) {
                    local_url = file_location;
                    myHandler.sendEmptyMessage(0);
                }
            }.execute();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

private String filter_url(String url){
    url = url.replace("\\", "");
    return url;
}
    private String makeNameFromURL(String url){
        String name = "";
        int x= url.lastIndexOf('/');
        name = url.substring(x+1, url.length());
        return name;
    }

    private String saveToInternalStorage(Bitmap bitmapImage, String fileName){
        ContextWrapper cw = new ContextWrapper( app_activity.getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory,fileName);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }
}
