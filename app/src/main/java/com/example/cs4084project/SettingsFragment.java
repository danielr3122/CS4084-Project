package com.example.cs4084project;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class SettingsFragment extends Fragment {

    public SettingsFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout. fragment_settings, container, false);

        Button buttonLogout = (Button) view.findViewById(R.id.btn_Logout);
        buttonLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getActivity(), Login.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            requireActivity().finish();
        });

        Button buttonChangePassword = view.findViewById(R.id.btn_change_pw);
        buttonChangePassword.setOnClickListener(view1 -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        });

        return view;
    }
}