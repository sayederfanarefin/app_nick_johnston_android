package com.bulbinc.nick;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by erfan on 4/29/2016.
 */
public class GetInTouchAdapter extends ArrayAdapter<GetInTouch> {
    Context mContext;

    /**
     * Adapter View layout
     */
    int mLayoutResourceId;

    public GetInTouchAdapter(Context context, int layoutResourceId) {
        super(context, layoutResourceId);

        mContext = context;
        mLayoutResourceId = layoutResourceId;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        final GetInTouch currentItem = getItem(position);

        if (row == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(R.layout.get_int_touch_layout_unit, parent, false);
        }

        row.setTag(currentItem);
        final TextView manager_title = (TextView) row.findViewById(R.id.call_text_manager);
        final TextView contact_1 = (TextView) row.findViewById(R.id.call_text_1);
        final TextView contact_2 = (TextView) row.findViewById(R.id.call_text_2);
        final ImageView iv = (ImageView) row.findViewById(R.id.call_1);
        final ImageView iv_2 = (ImageView) row.findViewById(R.id.call_2);

        //the_unit_music_title.setText(currentItem.title);
        //if(currentItem.type.equals("phone")){
            manager_title.setText(currentItem.manager);
            contact_1.setText(currentItem.contact_1);
            contact_2.setText(currentItem.contact_2);
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    call(contact_1.getText().toString());
                }
            });
            iv_2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    call(contact_2.getText().toString());
                }
            });
        //}else
        if (currentItem.type.equals("email")){
            manager_title.setText("");
            contact_1.setText(currentItem.contact_1);
            contact_2.setText(currentItem.contact_2);
            iv_2.setImageResource(R.mipmap.messanger_icon);
            iv.setImageResource(R.mipmap.email);

            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("plain/text");
                    intent.putExtra(Intent.EXTRA_EMAIL, new String[] { currentItem.contact_1 });
                   // intent.putExtra(Intent.EXTRA_SUBJECT, "subject");
                    getContext().startActivity(Intent.createChooser(intent, ""));
                }
            });
            iv_2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
        return row;
    }
    public void call(String number){
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:"+number));
        getContext().startActivity(intent);
    }
}