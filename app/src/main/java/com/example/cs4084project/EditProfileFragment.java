package com.example.cs4084project;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class EditProfileFragment extends Fragment {

    private DatabaseReference mDatabase;
    private EditText editText1, editText2, editText3, editText4;
    private FirebaseUser firebaseUser;

    public EditProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = firebaseUser.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        Button sendData = (Button) view.findViewById(R.id.btn_sendData);
        sendData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendData(view);
            }
        });
        editText1 = view.findViewById(R.id.edit_id1);
        editText2 = view.findViewById(R.id.edit_id2);
        editText3 = view.findViewById(R.id.edit_id3);
        editText4 = view.findViewById(R.id.edit_id4);

        return view;
    }

    public void sendData(View view) {
        writeNewUser();
    }

    public void writeNewUser() {

            User user = new User(editText1.getText().toString(),
                    editText2.getText().toString(),
                    editText3.getText().toString(),
                    editText4.getText().toString());

            mDatabase.child("users").child(user.getUID()).setValue(user);
        }


    }

