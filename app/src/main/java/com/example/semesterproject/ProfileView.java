package com.example.semesterproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import javax.annotation.Nullable;

public class ProfileView extends AppCompatActivity {

    FirebaseAuth mAuth;
    Button mShowPassword, mEditProfile, mHome;
    TextView mUsername, mEmail, mPassword;
    FirebaseFirestore mFirestore;
    String username, email, password;
    Boolean showingPassword = Boolean.FALSE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_view);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mShowPassword = findViewById(R.id.btnShowPassword);
        mEditProfile = findViewById(R.id.btnEditProfile);
        mHome = findViewById(R.id.btnHome);
        mUsername = findViewById(R.id.txtvUsername);
        mEmail = findViewById(R.id.txtvEmail);
        mPassword = findViewById(R.id.txtvPassword);

        mHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileView.this, MainActivity.class));
            }
        });

        mEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileView.this, ProfileEdit.class));
            }
        });

        mShowPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (showingPassword == Boolean.FALSE) {
                    mPassword.setText(password);
                    showingPassword = Boolean.TRUE;
                    mShowPassword.setText("Hide Password");
                }
                else if (showingPassword == Boolean.TRUE) {
                    mPassword.setText("Password");
                    showingPassword = Boolean.FALSE;
                    mShowPassword.setText("Show Password");
                }
            }
        });

    }

    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    public void updateUI(FirebaseUser user) {
        if (user == null) {
            startActivity(new Intent(this, LoginActivity.class));
        }
        else {
            String userID = mAuth.getUid();
            final DocumentReference docRef = mFirestore.collection("users").document(userID);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                                username = documentSnapshot.getString("username");
                                email = documentSnapshot.getString("email");
                                password = documentSnapshot.getString("password");
                                mUsername.setText(username);
                                mEmail.setText(email);
                            }
                        });
                    }
                }
            });
        }
    }
}