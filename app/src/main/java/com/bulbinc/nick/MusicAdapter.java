package com.bulbinc.nick;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by erfan on 4/29/2016.
 */
public class MusicAdapter extends ArrayAdapter<Music> {
    Context mContext;

    /**
     * Adapter View layout
     */
    int mLayoutResourceId;

    public MusicAdapter(Context context, int layoutResourceId) {
        super(context, layoutResourceId);

        mContext = context;
        mLayoutResourceId = layoutResourceId;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        final Music currentItem = getItem(position);

        if (row == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(R.layout.music_layout_unit, parent, false);
        }

        row.setTag(currentItem);
        final TextView the_unit_music_title = (TextView) row.findViewById(R.id.textView_music_title);

        the_unit_music_title.setText(currentItem.title);

        return row;
    }
}