package com.bulbinc.nick;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Created by erfan on 4/29/2016.
 */
public class AlbumAdapter extends ArrayAdapter<Album> {
    Context mContext;

    /**
     * Adapter View layout
     */
    int mLayoutResourceId;

    public AlbumAdapter(Context context, int layoutResourceId) {
        super(context, layoutResourceId);

        mContext = context;
        mLayoutResourceId = layoutResourceId;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        final Album currentItem = getItem(position);

        if (row == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(R.layout.album_layout_unit, parent, false);
        }

        row.setTag(currentItem);
        final TextView the_unit_album_title = (TextView) row.findViewById(R.id.textView_album_title);
        final TextView the_unit_album_year = (TextView) row.findViewById(R.id.textView_album_year);
        final ImageView album_art_ = (ImageView) row.findViewById(R.id.album_art);

        the_unit_album_title.setText(currentItem.title);
        the_unit_album_year.setText(currentItem.year);

        if(!currentItem.album_art_local.equals("na")){
            try {
                File f=new File(currentItem.album_art_local);
                Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
                album_art_.setImageBitmap(b);
            }
            catch (FileNotFoundException e)
            {

            }
        }
        return row;
    }
}