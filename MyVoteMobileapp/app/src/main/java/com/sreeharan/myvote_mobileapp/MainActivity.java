package com.sreeharan.myvote_mobileapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button changeLocationButton = findViewById(R.id.change_location_button);
        changeLocationButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Requesting to change the location",
                        Toast.LENGTH_SHORT).show();
                Intent i = new Intent(MainActivity.this,ChangeLocationActivity.class);
                startActivity(i);
            }
        });
    }
}