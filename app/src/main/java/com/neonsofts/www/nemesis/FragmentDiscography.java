package com.neonsofts.www.nemesis;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentDiscography.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentDiscography#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentDiscography extends Fragment {


    private OnFragmentInteractionListener mListener;

    public FragmentDiscography() {
        // Required empty public constructor
    }

    public static FragmentDiscography newInstance(String param1, String param2) {
        FragmentDiscography fragment = new FragmentDiscography();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }

        SharedPreferences settings = getActivity().getSharedPreferences("LEL", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("fragment_name", "discography");
        editor.commit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_about, container, false);
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
