package com.sreeharan.myvote_mobileapp;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.messaging.RemoteMessage;

import static com.sreeharan.myvote_mobileapp.Notifications.NOTIFY_CHANNEL;

public class QrScannerActivity extends AppCompatActivity {
    private final int CAMERA_PERMISSION_CODE = 23;
    private final String TAG = "QRCodeScanner-Activity";
    Dialog myDialog;
    Button ScanButton;
    TextView ScanCode;
    LinearLayout ScanLayout;

    private NotificationManagerCompat notificationManager;
    private Notification notification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_scanner);

        myDialog = new Dialog(this);

        ScanLayout = findViewById(R.id.scan_layout);
        ScanCode = findViewById(R.id.qr_code);

        ScanButton = findViewById(R.id.scan_button);
        ScanButton.setOnClickListener(v -> checkCameraPermission(QrScannerActivity.this));
        
        Intent resultIntent = new Intent(this, ConfirmationActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 23, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        notificationManager = NotificationManagerCompat.from(this);
        notification = new NotificationCompat.Builder(this, NOTIFY_CHANNEL)
                .setSmallIcon(R.drawable.add_symbol)
                .setContentTitle("Voter Coercion?")
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setContentInfo("Are you forced to vote for a particular party")
                .build();


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Permission Granted
                openCamera();
            }
        } else {
            Toast.makeText(this, "Camera Permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 123 && resultCode == RESULT_OK) {
            Log.w(TAG, "onActivityResult: Photo captured successfully");
            ScanLayout.setVisibility(View.INVISIBLE);
            ScanCode.setText("Code is:\n TC2EY5634");
            ScanCode.setVisibility(View.VISIBLE);
        } else {
            Log.w(TAG, "onActivityResult: Failed to capture the photo");
        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        notificationManager.notify(1, notification);
    }

    public void checkCameraPermission(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            Toast.makeText(context, "Version less than Marshmallow",
                    Toast.LENGTH_SHORT).show();
            openCamera();
        } else {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(context, "Already Granted the permission", Toast.LENGTH_SHORT).show();
                openCamera();

            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.CAMERA)) {
                    CameraPermissionPopUp();
                } else {
                    ActivityCompat.requestPermissions((Activity) context,
                            new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
                }
            }
        }
    }

    private void openCamera() {
        Intent takePictureIntent = new Intent("android.media.action.IMAGE_CAPTURE");
        startActivityForResult(takePictureIntent, 123);
    }

    public void CameraPermissionPopUp() {
        myDialog.setContentView(R.layout.camera_popup);
        Button okButton = myDialog.findViewById(R.id.ok_button);
        okButton.setOnClickListener(v -> {
            myDialog.dismiss();
            ActivityCompat.requestPermissions(QrScannerActivity.this,
                    new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        });
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }

}