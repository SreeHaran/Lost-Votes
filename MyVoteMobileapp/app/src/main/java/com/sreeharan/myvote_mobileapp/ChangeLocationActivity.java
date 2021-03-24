package com.sreeharan.myvote_mobileapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class ChangeLocationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_location);
        Button accepting = findViewById(R.id.accepting_button);
        accepting.setOnClickListener(v -> {
            Intent i = new Intent(ChangeLocationActivity.this, VerifyActivity.class);
            startActivity(i);
        });
    }
}