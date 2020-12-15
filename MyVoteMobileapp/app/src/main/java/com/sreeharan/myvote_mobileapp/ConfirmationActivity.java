package com.sreeharan.myvote_mobileapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ConfirmationActivity extends AppCompatActivity {

    TextView no, yes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);

        no = findViewById(R.id.no_button);
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent main = new Intent(ConfirmationActivity.this, MainActivity.class);
                startActivity(main);
                Toast.makeText(ConfirmationActivity.this, "Your response has been recorded", Toast.LENGTH_SHORT).show();
            }
        });
        yes = findViewById(R.id.yes_text);
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent main = new Intent(ConfirmationActivity.this, MainActivity.class);
                startActivity(main);
                Toast.makeText(ConfirmationActivity.this, "You have responded yes, Action will be taken accordingly", Toast.LENGTH_SHORT).show();
            }
        });
    }
}