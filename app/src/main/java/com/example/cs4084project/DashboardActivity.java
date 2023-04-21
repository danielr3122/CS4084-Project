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
//initialize in variables for Firebase and for the navigation in the app
    FirebaseAuth auth;
    FirebaseUser user;
    String myuid;
    ActionBar actionBar;
    BottomNavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        auth = FirebaseAuth.getInstance();
        actionBar = getSupportActionBar();
        actionBar.setTitle("Profile Activity");
        //auth = FirebaseAuth.getInstance();

        navigationView = findViewById(R.id.navigation);
        navigationView.setOnItemSelectedListener(selectedListener);
        actionBar.setTitle("Home");

        // When we open the application and are logged in
        // this fragment should be shown to the user
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
// creating the nav bar buttons and linking them to a page when clicked on, in the res folder and sub folder menu is where we change the
// the displayed names for the nav bar but here is where we call and create the fragments for each button on the nave abr.
                case R.id.nav_home:
                    actionBar.setTitle("Home");
                    // here we are creating a new instance of class home fragment
                    HomeFragment homeFragment = new HomeFragment();
                    // now we call a frame manger to and begin the fragment we can add replace, remove or hide the fragment
                    FragmentTransaction homeFragTransaction = getSupportFragmentManager().beginTransaction();
                    // In our case when a peron click the home button we are replacing the content with the home fragment class in the UI
                    homeFragTransaction.replace(R.id.content, homeFragment, "");
                    homeFragTransaction.commit();
                    return true;

                case R.id.nav_profile:
                    actionBar.setTitle("Profile");
                    ProfileFragment profileFragment = new ProfileFragment();
                    FragmentTransaction profileFragTransaction = getSupportFragmentManager().beginTransaction();
                    profileFragTransaction.replace(R.id.content, profileFragment);
                    profileFragTransaction.commit();
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


                case R.id.nav_edit:
                    actionBar.setTitle("Edit Profile");
                    EditProfileFragment editProfileFragment = new EditProfileFragment();
                    FragmentTransaction editFragTransaction = getSupportFragmentManager().beginTransaction();
                    editFragTransaction.replace(R.id.content, editProfileFragment, "");
                    editFragTransaction.commit();
                    return true;
            }
            return false;
        }
    };
}
