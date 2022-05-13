package com.example.semesterproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentProviderClient;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    EditText mEmail, mPassword, mUsername;
    Button mRegister;
    TextView mLogin;
    String userID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mEmail = findViewById(R.id.txtEmail);
        mPassword = findViewById(R.id.txtPassword);
        mUsername = findViewById(R.id.txtUsername);
        mRegister = findViewById(R.id.btnRegister);
        mLogin = findViewById(R.id.txtLogin);

        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = mEmail.getText().toString();
                final String password = mPassword.getText().toString();
                final String username = mUsername.getText().toString();

                //Check that all fields are filled in

                if (TextUtils.isEmpty(email)) {
                    mEmail.setError("Email is required");
                    if(TextUtils.isEmpty(password)) {
                        mPassword.setError("Password is required");
                    }
                    if(TextUtils.isEmpty(username)) {
                        mUsername.setError("Username is required");
                    }
                    return;
                }
                if(TextUtils.isEmpty(password)) {
                    mPassword.setError("Password is required");
                    if(TextUtils.isEmpty(username)) {
                        mUsername.setError("Username is required");
                    }
                    return;
                }
                if(TextUtils.isEmpty(username)) {
                    mUsername.setError("Username is required");
                    return;
                }

                //Register the user

                mAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        userID = mAuth.getCurrentUser().getUid();
                        DocumentReference document = mFirestore.collection("users").document(userID);
                        Map<String, Object> user = new HashMap<>();
                        user.put("username", username);
                        user.put("email", email);
                        user.put("password", password);
                        document.set(user);
                        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegisterActivity.this, "Failed to Create Account", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

    }

    public void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
        }
    }
}