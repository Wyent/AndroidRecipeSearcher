package com.example.semesterproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class ProfileEdit extends AppCompatActivity {

    EditText mUsername, mEmail, mPassword, mConfirmPassword;
    Button mChangeUsername, mChangeEmail, mChangePassword, mHome;
    FirebaseFirestore mFirestore;
    FirebaseAuth mAuth;
    String name, email, password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        mUsername = findViewById(R.id.edtUsername);
        mEmail = findViewById(R.id.edtEmail);
        mPassword = findViewById(R.id.edtPassword);
        mConfirmPassword = findViewById(R.id.edtConfirmPassword);
        mChangeUsername = findViewById(R.id.btnUsername);
        mChangeEmail = findViewById(R.id.btnEmail);
        mChangePassword = findViewById(R.id.btnPassword);
        mHome = findViewById(R.id.btnHome);
        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        DocumentReference docRef = mFirestore.collection("users").document(mAuth.getUid());
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                name = documentSnapshot.getString("username");
                email = documentSnapshot.getString("email");
                password = documentSnapshot.getString("password");
            }
        });


        mChangeUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mUsername.getText().toString().trim().equals("")) {
                    mUsername.setError("Username cannot be left blank");
                }
                else {
                    name = mUsername.getText().toString().trim();
                    Map<String, Object> profile = new HashMap<>();
                    profile.put("username", name);
                    profile.put("email", email);
                    profile.put("password", password);
                    docRef.set(profile);
                }
            }
        });

        mChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPassword.getText().toString().trim().equals("")) {
                    mPassword.setError("Password cannot be left blank");
                    if (mConfirmPassword.getText().toString().trim().equals("")) {
                        mConfirmPassword.setError("Confirm Password cannot be left blank");
                    }
                }
                else if(mConfirmPassword.getText().toString().trim().equals("")) {
                    mConfirmPassword.setError("Confirm Password cannot be left blank");
                }
                else if (!mPassword.getText().toString().trim().equals(mConfirmPassword.getText().toString().trim())) {
                    mConfirmPassword.setError("Passwords must match");
                }
                else {
                    password = mPassword.getText().toString().trim();
                    Map<String, Object> profile = new HashMap<>();
                    profile.put("username", name);
                    profile.put("email", email);
                    profile.put("password", password);
                    docRef.set(profile);
                    mAuth.getCurrentUser().updatePassword(password);
                }
            }
        });

        mChangeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mEmail.getText().toString().trim().equals("")) {
                    mEmail.setError("Email cannot be left blank");
                }
                else {
                    email = mEmail.getText().toString().trim();
                    Map<String, Object> profile = new HashMap<>();
                    profile.put("username", name);
                    profile.put("email", email);
                    profile.put("password", password);
                    docRef.set(profile);
                    mAuth.getCurrentUser().updateEmail(email);
                }
            }
        });

        mHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileEdit.this, MainActivity.class));
            }
        });
    }
}