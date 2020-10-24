package com.sreeharan.myvote_mobileapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class VerifyActivity extends AppCompatActivity {

    private final int CAMERA_PERMISSION_CODE = 23;
    Dialog myDialog;
    private final String TAG = "Verify Activity";
    private static int checkCase = -1;
    private static final int REQUEST_CODE_PHOTO = 100;
    private static final int REQUEST_CODE_VOTER_ID = 200;

    private LinearLayout faceButton;
    private LinearLayout VoterIdButton;
    private LinearLayout LocationButton;
    private Button requestButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);

        myDialog = new Dialog(this);

        faceButton = findViewById(R.id.face_detection_button);
        faceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCase = 0;
                checkCameraPermission();

            }
        });

        VoterIdButton = findViewById(R.id.voterID_button);
        VoterIdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCase = 1;
                checkCameraPermission();
            }
        });

        LocationButton = findViewById(R.id.location_button);
        LocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLocation();
            }
        });

        requestButton = findViewById(R.id.request_button);
        requestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(VerifyActivity.this, "Requesting", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setLocation() {
        //TODO: setting the location using a pop-up
    }

    public void checkCameraPermission(){
        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.M){
            Toast.makeText(this, "Version less than Marshmallow",
                    Toast.LENGTH_SHORT).show();
            openCamera();
        }else{

            if(ContextCompat.checkSelfPermission(this,
                    Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Already Granted the permission", Toast.LENGTH_SHORT).show();
                openCamera();

            }else{
                if(ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.CAMERA)) {
                    CameraPermissionPopUp();
                }else{
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.CAMERA},CAMERA_PERMISSION_CODE);
                }
            }
        }
    }

    private void openCamera() {
        //TODO: retrieve the bitmap from Camera
        Intent takePictureIntent = new Intent("android.media.action.IMAGE_CAPTURE");
        if(checkCase == 0){
            startActivityForResult(takePictureIntent, REQUEST_CODE_PHOTO);
        }else if(checkCase == 1){
            startActivityForResult(takePictureIntent, REQUEST_CODE_VOTER_ID);
        }else{
            Log.w(TAG, "openCamera: integer checkCase is not set to any value");
        }
    }

    public void CameraPermissionPopUp(){
        myDialog.setContentView(R.layout.camera_popup);
        Button okButton = myDialog.findViewById(R.id.ok_button);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
                ActivityCompat.requestPermissions(VerifyActivity.this, new String[]{Manifest.permission.CAMERA},CAMERA_PERMISSION_CODE);
            }
        });
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == CAMERA_PERMISSION_CODE){
            if(grantResults.length > 0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                //Permission Granted
                openCamera();
            }
        }else{
            Toast.makeText(this, "Camera Permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //TODO: Save the file and change the toggle
        if(requestCode == REQUEST_CODE_PHOTO && resultCode == RESULT_OK){
            Log.w(TAG, "onActivityResult: Photo captured successfully");
        }else if(requestCode == REQUEST_CODE_VOTER_ID && resultCode == RESULT_OK){
            Log.w(TAG, "onActivityResult: VoterID captured successfully");
        }else{
            Log.w(TAG, "onActivityResult: Failed to capture the photo");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}