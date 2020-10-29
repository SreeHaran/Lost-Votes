package com.sreeharan.myvote_mobileapp;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import static com.sreeharan.myvote_mobileapp.ImageDetection.*;

public class VerifyActivity extends AppCompatActivity {

    private final int CAMERA_PERMISSION_CODE = 23;
    private final String TAG = "Verify-Activity";
    private static int checkCase = -100;
    private static final int REQUEST_CODE_PHOTO = 100;
    private static final int REQUEST_CODE_VOTER_ID = 200;

    public static boolean faceCheck = false, IDCheck = false, locationCheck = false;
    private ConnectivityManager cm;
    Dialog myDialog;
    private ImageView faceToggleImage, VoterIdToggleImage , LocationToggleImage;
    Bitmap faceImage , voterIdImage ;
    private LinearLayout actualLayout, noConnectionLayout;
    public static TextView faceErrorMessage, voterIDErrorMessage;
    public TextView locationMessage;
    DetailsClass details = new DetailsClass();
    LocationSetting locationSetting = new LocationSetting();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);

        myDialog = new Dialog(this);
        locationMessage = findViewById(R.id.location_message);
        faceErrorMessage = findViewById(R.id.face_detection_message);
        voterIDErrorMessage = findViewById(R.id.voter_id_detection_message);

        actualLayout = findViewById(R.id.actual_layout);
        noConnectionLayout = findViewById(R.id.no_connection_layout);

        LinearLayout faceButton = findViewById(R.id.face_detection_button);
        faceToggleImage = findViewById(R.id.face_image_toggle);
        faceButton.setOnClickListener(v -> {
            networkUI();
            checkCase = 0;
            checkCameraPermission(VerifyActivity.this);
        });

        LinearLayout voterIdButton = findViewById(R.id.voterID_button);
        VoterIdToggleImage = findViewById(R.id.voter_ID_image_toggle);
        voterIdButton.setOnClickListener(v -> {
            networkUI();
            checkCase = 1;
            checkCameraPermission(VerifyActivity.this);
        });

        LinearLayout locationButton = findViewById(R.id.location_button);
        LocationToggleImage = findViewById(R.id.location_image_toggle);
        locationButton.setOnClickListener(v -> locationSetting.setLocation(VerifyActivity.this, LocationToggleImage, locationMessage, myDialog, cm));

        Button requestButton = findViewById(R.id.request_button);
        requestButton.setOnClickListener(v -> {
            networkUI();
            details.sendingRequest(VerifyActivity.this, faceCheck, IDCheck, locationCheck);
        });
        networkUI();
    }

    private void networkUI() {
        if (!(details.isConnected(this, cm))) {
            noConnectionLayout.setVisibility(View.VISIBLE);
            actualLayout.setVisibility(View.INVISIBLE);
        }
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
        if(requestCode == REQUEST_CODE_PHOTO && resultCode == RESULT_OK){
            Log.w(TAG, "onActivityResult: Photo captured successfully");
            faceImage = (Bitmap) data.getExtras().get("data");

            detectFaces(this, faceImage, faceToggleImage);
        }
        else if(requestCode == REQUEST_CODE_VOTER_ID && resultCode == RESULT_OK){
            Log.w(TAG, "onActivityResult: VoterID captured successfully");
            voterIdImage = (Bitmap) data.getExtras().get("data");

            detectVoterID(voterIdImage, VoterIdToggleImage);
        }
        else{
            Log.w(TAG, "onActivityResult: Failed to capture the photo");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void checkCameraPermission(Context context){
        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.M){
            Toast.makeText(context, "Version less than Marshmallow",
                    Toast.LENGTH_SHORT).show();
            openCamera();
        }else{
            if(ContextCompat.checkSelfPermission(this,
                    Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(context, "Already Granted the permission", Toast.LENGTH_SHORT).show();
                openCamera();

            }else{
                if(ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.CAMERA)) {
                    CameraPermissionPopUp();
                }else{
                    ActivityCompat.requestPermissions((Activity) context,
                            new String[]{Manifest.permission.CAMERA},CAMERA_PERMISSION_CODE);
                }
            }
        }
    }

    private void openCamera() {
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
        okButton.setOnClickListener(v -> {
            myDialog.dismiss();
            ActivityCompat.requestPermissions(VerifyActivity.this,
                    new String[]{Manifest.permission.CAMERA},CAMERA_PERMISSION_CODE);
        });
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }

}