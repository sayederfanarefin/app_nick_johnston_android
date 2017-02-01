package com.bulbinc.nick;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentGetInTouch.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentGetInTouch#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentGetInTouch extends Fragment {


    private OnFragmentInteractionListener mListener;
ListView contact_us_list_view;
    public FragmentGetInTouch() {
        // Required empty public constructor
    }

    public static FragmentGetInTouch newInstance(String param1, String param2) {
        FragmentGetInTouch fragment = new FragmentGetInTouch();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }

        SharedPreferences settings = getActivity().getSharedPreferences("LEL", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("fragment_name", "getInTouch");
        editor.commit();

        String email  = settings.getString("user_email", "na");

        if(email.equals("na")){
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
        }

    }

    View getIntoucheView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getIntoucheView = inflater.inflate(R.layout.fragment_get_in_touch, container, false);
        contact_us_list_view = (ListView) getIntoucheView.findViewById(R.id.listView_contact_info);

        GetInTouchAdapter gita = new GetInTouchAdapter(getActivity(), R.layout.get_int_touch_layout_unit);

        GetInTouch n = new GetInTouch();
        n.manager = "";
        n.type = "email";
        n.contact_1 = "nick@nickjohnstonmusic.com";
        n.contact_2 = "@NickJohnstonOfficial";
        gita.add(n);

        n = new GetInTouch();
        n.manager = "Booking Agent";
        n.type = "email";
        n.contact_1 = "liam@arteryglobal.com";
        n.contact_2 = "Liam John Byrne";
        gita.add(n);

/*
        n = new GetInTouch();
            n.manager = "Press Contact";
            n.type = "phone";
            n.contact_1 = "01712959848";
            n.contact_2 = "01751597947";
            gita.add(n);

        n = new GetInTouch();
            n.manager = "General Manager";
            n.type = "phone";
            n.contact_1 = "01712959848";
            n.contact_2 = "01751597947";
            gita.add(n);
*/


        contact_us_list_view.setAdapter(gita);
        return getIntoucheView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction_discography(uri);
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
        void onFragmentInteraction_discography(Uri uri);
    }
}
