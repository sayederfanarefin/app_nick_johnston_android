package com.neonsofts.www.nemesis;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;


public class FragmentSettings extends Fragment {
    Switch news, jam, music, concert;
    SharedPreferences settings;
    Boolean news_b,jam_b,music_b,concert_b;
    private OnFragmentInteractionListener mListener;

    Button sign_out;
    TextView user_name,user_email;

    public FragmentSettings() {
        // Required empty public constructor
    }

    public static FragmentSettings newInstance(String param1, String param2) {
        FragmentSettings fragment = new FragmentSettings();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_settings, container, false);

        news = (Switch) view.findViewById(R.id.switch_news);
        jam = (Switch) view.findViewById(R.id.switch_new_jamming_video);
        music = (Switch) view.findViewById(R.id.switch_new_music_video);
        concert = (Switch) view.findViewById(R.id.switch_nearest_concert);

        settings = getActivity().getSharedPreferences("LEL", 0);
        news_b = settings.getBoolean("noti_news_settings", true);
        jam_b = settings.getBoolean("noti_jam_settings", true);
        music_b = settings.getBoolean("noti_music_settings", true);
        concert_b = settings.getBoolean("noti_concert_settings", true);

        user_name = (TextView) view.findViewById(R.id.user_name);
        user_email = (TextView) view.findViewById(R.id.user_email);

        user_email.setText(settings.getString("user_email", "User email"));
        user_name.setText(settings.getString("user_name", "User Name"));

        sign_out = (Button) view.findViewById(R.id.sign_out_button);
        sign_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences settings = getActivity().getSharedPreferences("LEL", 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.clear();
                editor.commit();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        SharedPreferences.Editor editor = settings.edit();
        editor.putString("fragment_name", "settings");
        editor.commit();

        news.setChecked(news_b);
        jam.setChecked(jam_b);
        music.setChecked(music_b);
        concert.setChecked(concert_b);

        final SharedPreferences.Editor edit = settings.edit();
        news.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                edit.putBoolean("noti_news_settings",isChecked);
                edit.commit();
            }
        });

        jam.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                edit.putBoolean("noti_jam_settings",isChecked);
                edit.commit();
            }
        });

        music.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                edit.putBoolean("noti_music_settings",isChecked);
                edit.commit();
            }
        });

        concert.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                edit.putBoolean("noti_concert_settings",isChecked);
                edit.commit();
            }
        });


        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction_settings(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
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
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction_settings(Uri uri);
    }
}
