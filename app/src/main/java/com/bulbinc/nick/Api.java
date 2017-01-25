package com.bulbinc.nick;

import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by NewUsername on 12/20/2016.
 */

public class Api {
     AsyncTask<Void, Void, Boolean> task;
    public String result_json = null;

    public Api(final Handler myHandler ,  String base_url, final String table_name, final String last_id, final String column_name2, final String column_value2){

        String url_param = "NeonSofts726=Nopassword01&band_id=13101209&data=" + table_name;
        if(last_id!=null){
            url_param += "&lastid="+last_id;
        }
        if(column_name2 !=null && column_value2 != null){
            url_param += "&table_name="+column_name2+"&table_value="+column_value2;
        }

        final String final_url = base_url+"?"+url_param;


        try {
            //in the background the Api from the neonsofts server will pull all the data of News
            task = new AsyncTask<Void, Void, Boolean>() {
                @Override
                protected Boolean  doInBackground(Void... params) {
                    try {

Log.v("==url ", final_url);
                        URL url = new URL(final_url);
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setReadTimeout(10000);
                        conn.setConnectTimeout(15000);
                        conn.setRequestProperty("USER-AGENT", "Mozilla/5.0");
                        conn.setRequestMethod("GET");
                        conn.setDoInput(true);
                        conn.setDoOutput(true);


                        //Log.v("===url params", url_param);

                        /*DataOutputStream dStream = new DataOutputStream(conn.getOutputStream());
                        dStream.writeBytes(url_param);
                        dStream.flush();
                        dStream.close();
                        */

                        InputStream in;
                        if (conn.getResponseCode() == 200) {
                            in = conn.getInputStream();
                        } else {
                            /* error from server */
                            in = conn.getErrorStream();
                        }
                        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                        final StringBuilder result = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            result.append(line);
                        }
                        result_json = result.toString();
                        Log.v("===url reply", result_json);
                    }catch (Exception e ){
                        Log.v("===url params", "api exception"+e.getMessage());
                    }
                    return true;
                }
                protected void onPostExecute(Boolean result){

                    myHandler.sendEmptyMessage(0);
                }
            };
            task.execute();
        }
        catch (Exception e){
            Log.v("===url params", "api exception 2");
        }
    }

    public String get_json_reply_string(){
        return result_json.toString();
    }
    public JSONObject get_json (){
        JSONObject jObj = null;

        try {
            if (result_json.toString().length() > 2 && result_json.toString().contains("{") && result_json.toString().contains("}")) {
                try {
                    jObj = new JSONObject(result_json.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }catch(Exception e){
            jObj = new JSONObject();
            try {
                jObj.put("Exception", "-666");
            }catch (Exception ee){
                //oh well!
            }
        }

        return jObj;
    }

    public boolean cancle_api(){
        boolean b = task.cancel(true);
        return b;
    }
}
