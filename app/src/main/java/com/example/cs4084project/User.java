package com.example.cs4084project;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class User {
    private String fName;
    private String sName;
    private String nickname;
    private String email;
    private String UID;
   // FirebaseAuth mAuth;
    //FirebaseUser firebaseUser;

    public User() {}



    public User(String fName, String sName, String nickname, String email) {
        this.fName = fName;
        this.sName = sName;
        this.email= email;
        this.nickname= nickname;
        this.UID= FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public String getfirstName() {
        return fName;
    }
    public String getsecondName() { return sName; }
    public String getEmail(){ return email;}
    public String getNickname(){ return nickname;}
    public String getUID() {return UID;}


}