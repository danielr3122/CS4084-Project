package com.example.cs4084project;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DashboardActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseUser user;
    String myuid;
    ActionBar actionBar;
    BottomNavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        // using auth to try create instance of session to use to log someone out, not currently working out
        auth = FirebaseAuth.getInstance();
        actionBar = getSupportActionBar();
        actionBar.setTitle("Profile Activity");
        //auth = FirebaseAuth.getInstance();

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
// creating the nav bar buttons and linking them to a page when clicked on
                case R.id.nav_home:
                    actionBar.setTitle("Home");
                    HomeFragment homeFragment = new HomeFragment();
                    FragmentTransaction homeFragTransaction = getSupportFragmentManager().beginTransaction();
                    homeFragTransaction.replace(R.id.content, homeFragment, "");
                    homeFragTransaction.commit();
                    return true;

                case R.id.nav_search:
                    actionBar.setTitle("Search");
                    SearchFragment searchFragment= new SearchFragment();
                    FragmentTransaction searchFragTransaction = getSupportFragmentManager().beginTransaction();
                    searchFragTransaction.replace(R.id.content, searchFragment);
                    searchFragTransaction.commit();
                    return true;

                case R.id.nav_post:
                    actionBar.setTitle("Create Post");
                    NewPostFragment postFragment = new NewPostFragment();
                    FragmentTransaction postFragTransaction = getSupportFragmentManager().beginTransaction();
                    postFragTransaction.replace(R.id.content, postFragment, "");
                    postFragTransaction.commit();
                    return true;

                case R.id.nav_settings:
                    actionBar.setTitle("Settings");
                    SettingsFragment settingsFragment = new SettingsFragment();
                    FragmentTransaction settingsFragTransaction = getSupportFragmentManager().beginTransaction();
                    settingsFragTransaction.replace(R.id.content, settingsFragment, "");
                    settingsFragTransaction.commit();
                    return true;

                case R.id.nav_profile:
                    actionBar.setTitle("Profile");
                    ProfileFragment profileFragment = new ProfileFragment();
                    FragmentTransaction profileFragTransaction = getSupportFragmentManager().beginTransaction();
                    profileFragTransaction.replace(R.id.content, profileFragment);
                    profileFragTransaction.commit();
                    return true;
            }
            return false;
        }
    };
}
