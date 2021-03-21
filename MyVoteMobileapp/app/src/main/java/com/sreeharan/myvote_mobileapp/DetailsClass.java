package com.sreeharan.myvote_mobileapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

import static com.sreeharan.myvote_mobileapp.MainActivity.MobileNumber;
import static com.sreeharan.myvote_mobileapp.VerifyActivity.locationCheck;
import static com.sreeharan.myvote_mobileapp.VerifyActivity.IDCheck;
import static com.sreeharan.myvote_mobileapp.VerifyActivity.faceCheck;


public class DetailsClass {
    private final String TAG = "DetailsClass";
    FirebaseStorage storage = FirebaseStorage.getInstance("gs://my-vote---mobileapp.appspot.com");
    StorageReference storageRef = storage.getReference();

    StorageReference FaceRef = storageRef.child("Requests/"+MobileNumber+"/face");
    StorageReference voterRef = storageRef.child("Requests/"+MobileNumber+"/voterID");

    ByteArrayOutputStream faceByte = new ByteArrayOutputStream();
    ByteArrayOutputStream voterIdByte = new ByteArrayOutputStream();

    void sendingRequest(Context context, boolean mFaceCheck, boolean voteIdCheck, boolean mLocationCheck,
                        Bitmap face, Bitmap voterID, String location){
        Log.e("DetailsClass", "sendingRequest: checking"+mFaceCheck+" - "+voteIdCheck+" - "+mLocationCheck);
        if(mFaceCheck && voteIdCheck && mLocationCheck){
            Toast.makeText(context, "Sending Request", Toast.LENGTH_SHORT).show();

            uploadToFirebase(face, voterID, location, context);


            context.startActivity(new Intent(context, MainActivity.class));
            faceCheck = false; IDCheck = false; locationCheck = false;
        }else{
            Toast.makeText(context, "Failed to send request check all the fields are checked", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadToFirebase(Bitmap face, Bitmap voterID, String location, Context context) {
        face.compress(Bitmap.CompressFormat.JPEG, 100, faceByte);
        voterID.compress(Bitmap.CompressFormat.JPEG, 100, voterIdByte);
        byte[] faceData = faceByte.toByteArray();
        byte[] voterIdData = voterIdByte.toByteArray();
        UploadTask faceUploadTask = FaceRef.putBytes(faceData);
        UploadTask voterIdUploadTask = voterRef.putBytes(voterIdData);

        faceUploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "onFailure: Failed to upload face data - "+e);
                Toast.makeText(context, "Failed to send request", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.e(TAG, "onSuccess: Face: "+ taskSnapshot.getMetadata());
            }
        });;
        voterIdUploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "onFailure: Failed to upload voter ID data - "+e);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.e(TAG, "onSuccess: VoterID: "+ taskSnapshot.getMetadata());
            }
        });

    }

    public boolean isConnected(Context context, ConnectivityManager cm){
        cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}
