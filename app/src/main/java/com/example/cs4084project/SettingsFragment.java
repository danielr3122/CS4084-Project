package com.example.cs4084project;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
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
        Button buttonChangeEmail = view.findViewById(R.id.btn_change_email);
        Button buttonChangePassword = view.findViewById(R.id.btn_change_pw);
        EditText oldEmailET = view.findViewById(R.id.et_old_email);
        EditText newEmailET = view.findViewById(R.id.et_new_email);
        EditText oldPasswordET = view.findViewById(R.id.et_old_pw);
        EditText newPasswordET = view.findViewById(R.id.et_new_pw);

        buttonLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getActivity(), Login.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            requireActivity().finish();
        });

        buttonChangePassword.setOnClickListener(view1 -> {
            String oldPassword = oldPasswordET.getText().toString().trim();
            String newPassword = newPasswordET.getText().toString().trim();
            changePassword(oldPassword, newPassword);
        });

        return view;
    }

    private void changePassword(String oldPassword, String newPassword){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String email = user.getEmail();
        AuthCredential credential = EmailAuthProvider.getCredential(email, oldPassword);
        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(!task.isSuccessful()){
                                Toast.makeText(getContext(), "Oops, some thing went wrong. Please try again.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), "Successfully Changed Password", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(getContext(), "Failure, Incorrect Old Password", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}