package com.example.cs4084project;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;

public class DashboardActivity extends AppCompatActivity {

//    private FirebaseAuth firebaseAuth;
//    FirebaseUser firebaseUser;
    String myuid;
    ActionBar actionBar;
    BottomNavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        actionBar = getSupportActionBar();
        actionBar.setTitle("Profile Activity");
//        firebaseAuth = FirebaseAuth.getInstance();

        navigationView = findViewById(R.id.navigation);
        navigationView.setOnItemSelectedListener(selectedListener);
        actionBar.setTitle("Home");

        // When we open the application first
        // time the fragment should be shown to the user
        // in this case it is home fragment
        HomeFragment fragment = new HomeFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content, fragment, "");
        fragmentTransaction.commit();
    }

    private BottomNavigationView.OnItemSelectedListener selectedListener = new BottomNavigationView.OnItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId()) {

                case R.id.nav_home:
                    actionBar.setTitle("Home");
                    HomeFragment fragment = new HomeFragment();
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.content, fragment, "");
                    fragmentTransaction.commit();
                    return true;

                case R.id.nav_profile:
                    actionBar.setTitle("Profile");
                    ProfileFragment fragment1 = new ProfileFragment();
                    FragmentTransaction fragmentTransaction1 = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction1.replace(R.id.content, fragment1);
                    fragmentTransaction1.commit();
                    return true;

                case R.id.nav_post:
                    actionBar.setTitle("Users");
                    NewPostFragment fragment2 = new NewPostFragment();
                    FragmentTransaction fragmentTransaction2 = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction2.replace(R.id.content, fragment2, "");
                    fragmentTransaction2.commit();
                    return true;
            }
            return false;
        }
    };
}
