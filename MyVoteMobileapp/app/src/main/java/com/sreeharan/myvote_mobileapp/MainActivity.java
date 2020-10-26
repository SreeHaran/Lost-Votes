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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private ConnectivityManager cm;
    Button changeLocationButton;
    LinearLayout noConnection, actualLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        changeLocationButton = findViewById(R.id.change_location_button);
        noConnection = findViewById(R.id.no_connection_layout);
        actualLayout = findViewById(R.id.actual_layout);

        changeLocationButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Requesting to change the location",
                        Toast.LENGTH_SHORT).show();
                Intent i = new Intent(MainActivity.this,ChangeLocationActivity.class);
                startActivity(i);
            }
        });
        if(!isConnected()){
            noConnection.setVisibility(View.VISIBLE);
            actualLayout.setVisibility(View.INVISIBLE);
        }
    }
    private boolean isConnected(){
        cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}