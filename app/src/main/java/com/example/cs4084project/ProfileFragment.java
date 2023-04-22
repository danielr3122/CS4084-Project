package com.example.cs4084project;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class ProfileFragment extends Fragment {
//initialize  variables for the textView used to display userdata in the profie and to get the datasbse refrence to update teh users information
    private DatabaseReference mDatabase;
    private TextView mfNameTextView, msNameTextView, mEmailTextView, mNickname;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get references to the UI elements
        mfNameTextView = view.findViewById(R.id.profile_Fname);
        msNameTextView = view.findViewById(R.id.profile_Sname);
        mEmailTextView = view.findViewById(R.id.profile_email);
        mNickname = view.findViewById(R.id.profile_username);

        // Get a reference to the Realtime Database
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Get a reference to the current user
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();

            // Get a reference to the user's data in the Realtime Database
            DatabaseReference userRef = mDatabase.child("users").child(uid);

            // Attach a ValueEventListener to update the UI with the user's data
            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    // Retrieve the user's data from the database we get  a snapshot of the data base when it is called and extract the data from that
                    // each time the paqe is refreshed we grab a new snap shot, one advantage of the real time database
                    User user = snapshot.getValue(User.class);
                    // The commented out code has been left in so that I dont make the same mistake in other parts
                    // the code below is the proper way to do it or the best way I found to do it
//                    if (user != null) {
//                        if (!TextUtils.isEmpty(user.getfirstName())) {
//                            mFNameTextView.setText(user.getfirstName());
//                        }
//                        if (!TextUtils.isEmpty(user.getsecondName())) {
//                            mSNameTextView.setText(user.getsecondName());
//                        }
//                        if (!TextUtils.isEmpty(user.getEmail())) {
//                            mEmailTextView.setText(user.getEmail());
//                        }
//                        if (!TextUtils.isEmpty(user.getNickname())) {
//                            mNickname.setText(user.getNickname());
//                        }
//                    }

                    // Update the UI with the user's data
                    assert user != null;
                    mfNameTextView.setText(user.getfirstName());
                    msNameTextView.setText(user.getsecondName());
                    mEmailTextView.setText(user.getEmail());
                    mNickname.setText(user.getNickname());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("ProfileFragment", "Error retrieving user data from Realtime Database: " + error.getMessage());
                }
            });
        } else {
            Log.e("ProfileFragment", "Current user is null.");
        }
    }
}

//  This is old code purely left in for my benefit  it can be ignored by anyone editing this program
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
//        TextView usernameText = view.findViewById(R.id.profile_username);
//        usernameText.setText(userEmail);
//    }
//}