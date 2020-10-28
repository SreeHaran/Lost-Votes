package com.sreeharan.myvote_mobileapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
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
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private static final int RC_SIGN_OUT = 1;
    private ConnectivityManager cm;
    Button changeLocationButton;
    LinearLayout noConnection, actualLayout;
    DetailsClass details = new DetailsClass();
    TextView voterName;


    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        changeLocationButton = findViewById(R.id.change_location_button);
        noConnection = findViewById(R.id.no_connection_layout);
        actualLayout = findViewById(R.id.actual_layout);
        voterName = findViewById(R.id.voter_name);

        mFirebaseAuth = FirebaseAuth.getInstance();

        changeLocationButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Requesting to change the location",
                        Toast.LENGTH_SHORT).show();
                Intent i = new Intent(MainActivity.this,ChangeLocationActivity.class);
                startActivity(i);
            }
        });
        if(!(details.isConnected(this, cm))){
            noConnection.setVisibility(View.VISIBLE);
            actualLayout.setVisibility(View.INVISIBLE);
        }

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user == null){
                    //signed out
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setAvailableProviders(
                                            Collections.singletonList(new AuthUI.IdpConfig.PhoneBuilder().build())
                                    )
                            .build(), RC_SIGN_OUT);
                }else{
                    voterName.setText(user.getDisplayName());
                }
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