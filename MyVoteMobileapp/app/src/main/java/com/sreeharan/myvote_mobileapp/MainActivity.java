package com.sreeharan.myvote_mobileapp;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    private static final int RC_SIGN_OUT = 1;
    private final String REF = "Voters";
    private final String TAG = "Main-Activity";
    Button changeLocationButton, VoteButton;
    LinearLayout noConnection, actualLayout;
    RelativeLayout loadingLayout;
    DetailsClass details = new DetailsClass();
    TextView voterName, voterId, relativeName, gender, age;
    private ConnectivityManager cm;
    private FirebaseDatabase database;
    private DatabaseReference mFirebaseReference;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private ChildEventListener mChildEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        changeLocationButton = findViewById(R.id.change_location_button);
        noConnection = findViewById(R.id.no_connection_layout);
        actualLayout = findViewById(R.id.actual_layout);
        loadingLayout = findViewById(R.id.loading_data);

        voterName = findViewById(R.id.voter_name);
        voterId = findViewById(R.id.voterID_number);
        relativeName = findViewById(R.id.voter_relative_name);
        gender = findViewById(R.id.voter_gender);
        age = findViewById(R.id.voter_age);

        VoteButton = findViewById(R.id.voting_button);

        // Write a message to the database
        database = FirebaseDatabase.getInstance();

        mFirebaseAuth = FirebaseAuth.getInstance();

        changeLocationButton.setOnClickListener(v -> {
            Toast.makeText(MainActivity.this, "Requesting to change the location",
                    Toast.LENGTH_SHORT).show();
            Intent i = new Intent(MainActivity.this, ChangeLocationActivity.class);
            startActivity(i);
        });
        VoteButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, QrScannerActivity.class);
                startActivity(i);
            }
        });
        if (!(details.isConnected(this, cm))) {
            noConnection.setVisibility(View.VISIBLE);
            actualLayout.setVisibility(View.INVISIBLE);
        }

        mAuthStateListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user == null) {
                //signed out
                startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setAvailableProviders(
                                        Collections.singletonList(
                                                new AuthUI.IdpConfig.PhoneBuilder()
                                                        .build()
                                        )
                                )
                                .build(), RC_SIGN_OUT);
            } else {
                actualLayout.setVisibility(View.INVISIBLE);
                loadingLayout.setVisibility(View.VISIBLE);
                mFirebaseReference = database.getReference(REF);
                //.child(user.getPhoneNumber());
                mFirebaseReference.child(user.getPhoneNumber()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Log.e(TAG, "onDataChange: phoneNumber: " + user.getPhoneNumber() + " exists: " + snapshot.exists() + " Children: " + snapshot.getChildren());

                        Log.e(TAG, "onDataChange: Name: " + snapshot.child("Name").getValue());
                        Log.e(TAG, "onDataChange: Voter Id: " + snapshot.child("Voter Id").getValue());
                        Log.e(TAG, "onDataChange: Gender: " + snapshot.child("Gender").getValue());
                        Log.e(TAG, "onDataChange: Relative: " + snapshot.child("Relative").getValue());
                        Log.e(TAG, "onDataChange: Age: " + snapshot.child("Age").getValue());


                        voterName.setText(snapshot.child("Name").getValue().toString());
                        voterId.setText(snapshot.child("Voter Id").getValue().toString());
                        gender.setText(snapshot.child("Gender").getValue().toString());
                        relativeName.setText(snapshot.child("Relative").getValue().toString());
                        age.setText(snapshot.child("Age").getValue().toString());

                        loadingLayout.setVisibility(View.INVISIBLE);
                        actualLayout.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "onCancelled: " + error.toString());
                        loadingLayout.setVisibility(View.INVISIBLE);
                        noConnection.setVisibility(View.VISIBLE);
                    }
                });
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
    }
}