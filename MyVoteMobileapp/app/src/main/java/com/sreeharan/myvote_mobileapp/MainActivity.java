package com.sreeharan.myvote_mobileapp;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.DialogInterface;
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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.Collections;

import static com.sreeharan.myvote_mobileapp.Notifications.NOTIFY_CHANNEL;

public class MainActivity extends AppCompatActivity {

    private static final int RC_SIGN_OUT = 1;
    public static String MobileNumber;
    private final String VOTERS_REF = "Voters";
    private final String SCANNER_REF = "QR-Code";
    private final String TAG = "Main-Activity";
    Button changeLocationButton, VoteButton;
    LinearLayout noConnection, actualLayout;
    RelativeLayout loadingLayout;
    DetailsClass details = new DetailsClass();
    TextView voterName, voterId, relativeName, gender, age;
    private NotificationManagerCompat notificationManager;
    private Notification notification;
    private ConnectivityManager cm;
    private FirebaseDatabase database;
    private DatabaseReference mFirebaseReference;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private ChildEventListener mChildEventListener;
    private String constituency;

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

        Intent resultIntent = new Intent(this, ConfirmationActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 23, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        notificationManager = NotificationManagerCompat.from(this);
        notification = new NotificationCompat.Builder(this, NOTIFY_CHANNEL)
                .setSmallIcon(R.drawable.correct_symbol)
                .setContentTitle("Voter Coercion?")
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setContentInfo("Are you forced to vote for a particular party in the polling  station?")
                .build();

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
                IntentIntegrator intentIntegrator = new IntentIntegrator(MainActivity.this);
                intentIntegrator.setOrientationLocked(true)
                        .setBeepEnabled(true)
                        .setPrompt("For Flash use Volume Up key")
                        .setCaptureActivity(Capture.class).initiateScan();
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
                mFirebaseReference = database.getReference(VOTERS_REF);
                //.child(user.getPhoneNumber());
                MobileNumber = user.getPhoneNumber();
                mFirebaseReference.child(MobileNumber).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Log.e(TAG, "onDataChange: phoneNumber: " + MobileNumber + " exists: " + snapshot.exists() + " Children: " + snapshot.getChildren());

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
                        constituency = snapshot.child("Constituency").getValue().toString();

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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "onActivityResult: requestCode=" + requestCode + " ResultCode = " + resultCode);
        if (requestCode != 1) {
            IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (intentResult.getContents() != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Secret Code");
                builder.setMessage(intentResult.getContents());
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        notificationManager.notify(1, notification);
                    }
                });
                builder.show();
            /*mFirebaseReference = database.getReference(SCANNER_REF);
            mFirebaseReference.child(intentResult.getContents()).setValue(constituency);*/
            } else {
                Toast.makeText(this, "QR code unsuccessful", Toast.LENGTH_SHORT).show();
            }
        }
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