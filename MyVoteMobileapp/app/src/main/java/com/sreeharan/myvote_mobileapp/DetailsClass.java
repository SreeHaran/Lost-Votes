package com.sreeharan.myvote_mobileapp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import static com.sreeharan.myvote_mobileapp.VerifyActivity.locationCheck;
import static com.sreeharan.myvote_mobileapp.VerifyActivity.IDCheck;
import static com.sreeharan.myvote_mobileapp.VerifyActivity.faceCheck;


public class DetailsClass {

    void sendingRequest(Context context, boolean mFaceCheck, boolean voteIdCheck, boolean mLocationCheck){
        //TODO: add the face image, voterID image, location details
        Log.e("DetailsClass", "sendingRequest: checking"+mFaceCheck+" - "+voteIdCheck+" - "+mLocationCheck);
        if(mFaceCheck && voteIdCheck && mLocationCheck){
            Toast.makeText(context, "Request Sent", Toast.LENGTH_SHORT).show();
            context.startActivity(new Intent(context, MainActivity.class));
            faceCheck = false; IDCheck = false; locationCheck = false;
        }else{
            Toast.makeText(context, "Failed to send request check all the fields are checked", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean isConnected(Context context, ConnectivityManager cm){
        cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}
