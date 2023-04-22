package com.example.cs4084project;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
// The user class is what is used to get and setr the data in the realtime firebase database as unlike with authenticator we create the data fields
// and have to make the getters and setter for the user object
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
// This is the setter method for a user when a user is edited or created they must enter all the data to keep it simple
    //
    public User(String firstName, String secondName, String nickname, String email) {
        this.firstName = firstName;
        this.secondName = secondName;
        this.email= email;
        this.nickname= nickname;
        this.UID= FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
// this is the getter methods this is how we return the data
    public String getfirstName() {
        return firstName;
    }
    public String getsecondName() { return secondName; }
    public String getEmail(){ return email;}
    public String getNickname(){ return nickname;}
    public String getUID() {return UID;}


}