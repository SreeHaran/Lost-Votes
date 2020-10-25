package com.sreeharan.myvote_mobileapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

public class DetailsClass {
    static Bundle informations;
    static void setLocation(Context context,ImageView toggleImage){
        //TODO: setting the location information by user
        Toast.makeText(context, "Setting Location", Toast.LENGTH_SHORT).show();
        if(1>0) {
            toggleImage.setImageResource(R.drawable.ic_baseline_check_circle_24);
        }else{
            toggleImage.setImageResource(R.drawable.ic_baseline_cancel_24);
        }
    }

    static void addInformation(Bitmap Image, int code){
        //TODO: add the face image, voterID image, location details
    }
}
