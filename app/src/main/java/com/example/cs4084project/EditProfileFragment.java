package com.example.cs4084project;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditProfileFragment extends Fragment {
//    initializing variables for firebase  and for edit text so that we can edit text input by the user
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
        String firstName = editText1.getText().toString().trim();
        String secondName = editText2.getText().toString().trim();
        String email = editText3.getText().toString().trim();
        String nickname = editText4.getText().toString().trim();

        // Check if any input field is empty
        if (TextUtils.isEmpty(firstName) || TextUtils.isEmpty(secondName)
                || TextUtils.isEmpty(email) || TextUtils.isEmpty(nickname)) {
            Toast.makeText(getActivity(), "Please enter valid data", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a new User object and write it to the database
        User user = new User(firstName, secondName, email, nickname);
        // Here we are setting the identifier to how the data is stored, in this case I am grabbing the UID from authenticator and using that
        // so for the child of realtime database root/ users we are getting the UID and using that as the way to identify a user and link them to the
        mDatabase.child("users").child(user.getUID()).setValue(user);
    }

    // this method is how we are creating the user object in the data base, we are passing in the fist name and other information from the send data method

    public void writeNewUser() {

        User user = new User(editText1.getText().toString(),
                editText2.getText().toString(),
                editText3.getText().toString(),
                editText4.getText().toString());

        mDatabase.child("users").child(user.getUID()).setValue(user);
    }


}

