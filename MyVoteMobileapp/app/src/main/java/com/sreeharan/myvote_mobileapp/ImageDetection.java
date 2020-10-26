package com.sreeharan.myvote_mobileapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import static com.sreeharan.myvote_mobileapp.VerifyActivity.errorMessage;

public class ImageDetection {

    static void detectFaces(Context context, Bitmap picture, ImageView detection) {
        // Create the face detector, disable tracking and enable classifications
        FaceDetector detector = new FaceDetector.Builder(context)
                .setTrackingEnabled(false)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .build();

        // Build the frame
        Frame frame = new Frame.Builder().setBitmap(picture).build();

        // Detect the faces
        SparseArray<Face> faces = detector.detect(frame);

        // Log the number of faces
        Log.w("FaceDetectionClass", "detectFaces: number of faces = " + faces.size());

        // If there are no faces detected, show a Toast message
        if (faces.size() == 0) {
            detection.setImageResource(R.drawable.wrong_symbol);
            errorMessage.setVisibility(View.VISIBLE);
            errorMessage.setText("No face has been detected in the picture");
        }else if(faces.size() == 1){
            detection.setImageResource(R.drawable.correct_symbol);
            errorMessage.setVisibility(View.INVISIBLE);
        }
        else if(faces.size()>1){
            detection.setImageResource(R.drawable.wrong_symbol);
            errorMessage.setVisibility(View.VISIBLE);
            errorMessage.setText("Multiple faces has been detected");
        }
        // Release the detector
        detector.release();
    }

    static void detectVoterID(Context context, Bitmap picture, ImageView detection){
        //TODO: detect the voter ID card
    }
}
