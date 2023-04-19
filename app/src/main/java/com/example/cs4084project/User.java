package com.example.cs4084project;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class User {
    private String firstName;
    private String secondName;
    private String nickname;
    private String email;
    private String UID;
    // FirebaseAuth mAuth;
    //FirebaseUser firebaseUser;

    public User() {}

    public void setfName(String fName) {
        this.firstName = firstName;
    }

    public void setsName(String sName) {
        this.secondName = secondName;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public User(String firstName, String secondName, String nickname, String email) {
        this.firstName = firstName;
        this.secondName = secondName;
        this.email= email;
        this.nickname= nickname;
        this.UID= FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public String getfirstName() {
        return firstName;
    }
    public String getsecondName() { return secondName; }
    public String getEmail(){ return email;}
    public String getNickname(){ return nickname;}
    public String getUID() {return UID;}


}