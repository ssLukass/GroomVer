package com.example.groomver.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.groomver.R;


public class MessegeFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_messeges, container, false);
        Toast.makeText(requireContext(), getString(R.string.Messeges), Toast.LENGTH_SHORT).show();





        return view;
    }
}