package com.example.inrapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class Diet_Stratergy extends Fragment {

    public Diet_Stratergy() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_diet__stratergy, container, false);
        TextView tv = (TextView) view.findViewById(R.id. website);
        tv.setMovementMethod(LinkMovementMethod.getInstance());
        return view;
    }
}
