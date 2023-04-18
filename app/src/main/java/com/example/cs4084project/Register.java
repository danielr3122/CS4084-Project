package com.example.cs4084project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Register extends AppCompatActivity {
// initialising variables and Firebase authentication
    TextInputEditText editTextEmail, editTextPassword, editTextConfirmPassword, editTextFirstName, editTextSecondName;
    Button buttonReg;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    ProgressBar progressBar;
    TextView textview;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        if(currentUser != null){
            Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
            startActivity(intent);
            finish();

        }
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();

//        editTextFirstName =findViewById(R.id.fName);
//        editTextSecondName = findViewById(R.id.sName);
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        editTextConfirmPassword = findViewById(R.id.confirmpassword);
        buttonReg = findViewById(R.id.btn_register);
        progressBar = findViewById(R.id.progressBar);
        textview = findViewById(R.id.loginNow);
        textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });

        buttonReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // progress bar has been hidden as it can fool people into thinking something is loading
                progressBar.setVisibility(View.GONE);
                String name, email, password;
                email = String.valueOf(editTextEmail.getText());
                password = String.valueOf(editTextPassword.getText());
                String confirmPassword = String.valueOf(editTextConfirmPassword.getText());

                if(TextUtils.isEmpty(email)){
                    Toast.makeText(Register.this, "Enter Email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    Toast.makeText(Register.this, "Enter Password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(confirmPassword)){
                    Toast.makeText(Register.this, "Confirm Password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(password.length() < 6){
                    Toast.makeText(Register.this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!isValidPassword(password)){
                    Toast.makeText(Register.this, "Password must contain at least one uppercase letter, one lowercase letter, and one digit", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!password.equals(confirmPassword)) {
                    Toast.makeText(Register.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    return;

                }

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                // progress bar has been hidden as it can fool people into thinking something is loading
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    // User registration successful
                                    Toast.makeText(Register.this, "Account created.",
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    // User registration failed
                                    String errorMessage = task.getException().getMessage();
                                    Toast.makeText(Register.this, "Authentication failed: " + errorMessage,
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });



            }
        });

    }
//  This is just a method that takes the password entered and puts it in this method where it is checked,
//    if it meets the criteria laid out it will return a true or false flag in a boolean form to the above if statement,
//      if it meets the criteria the user will see a message saying account created and be able to login, if the password is
//      is not valid the user will be prompted that the password entered is missing whatever in this list to be valid.

    private boolean isValidPassword(String password) {
        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);
        return matcher.matches();
    }

//    public void sendData(View view){
//        writeNewUser();
//    }
//    public void writeNewUser(){
//       User user = new User( editTextFirstName.getText().toString(),
//               editTextSecondName.getText().toString(),
//               editTextEmail.getText().toString());
//
//       mDatabase.child("users").child(user.get)
//    }
}


