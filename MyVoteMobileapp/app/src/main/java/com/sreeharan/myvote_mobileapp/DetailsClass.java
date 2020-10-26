package com.sreeharan.myvote_mobileapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

public class DetailsClass {
    static Bundle informations;
    static void setLocation(Context context,ImageView toggleImage){
        //TODO: setting the location information by user
        Toast.makeText(context, "Setting Location", Toast.LENGTH_SHORT).show();
        if(1>0) {
            toggleImage.setImageResource(R.drawable.correct_symbol);
        }else{
            toggleImage.setImageResource(R.drawable.wrong_symbol);
        }
    }

    static void addInformation(Bitmap Image, int code){
        //TODO: add the face image, voterID image, location details
    }
}
