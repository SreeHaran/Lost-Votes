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

import static com.sreeharan.myvote_mobileapp.VerifyActivity.IDCheck;
import static com.sreeharan.myvote_mobileapp.VerifyActivity.faceCheck;
import static com.sreeharan.myvote_mobileapp.VerifyActivity.faceErrorMessage;
import static com.sreeharan.myvote_mobileapp.VerifyActivity.voterIDErrorMessage;
public class ImageDetection {

    static boolean detectFaces(Context context, Bitmap picture, ImageView detection) {
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
        faceErrorMessage.setVisibility(View.VISIBLE);
        if (faces.size() == 0) {
            detection.setImageResource(R.drawable.wrong_symbol);
            faceErrorMessage.setText("No face has been detected in the picture");
            faceCheck = false;
        }else if(faces.size() == 1){
            detection.setImageResource(R.drawable.correct_symbol);
            faceErrorMessage.setText("Face Detection Successful");
            faceCheck = true;
        }
        else if(faces.size()>1){
            detection.setImageResource(R.drawable.wrong_symbol);
            faceErrorMessage.setText("Multiple persons has been detected");
            faceCheck = false;
        }
        // Release the detector
        detector.release();
        return faceCheck;
    }

    static boolean detectVoterID(Bitmap picture, ImageView detection){
        voterIDErrorMessage.setVisibility(View.VISIBLE);
        if(picture != null){
            detection.setImageResource(R.drawable.correct_symbol);
            voterIDErrorMessage.setText("Voter ID detection Successful");
            IDCheck = true;
        }else{
            detection.setImageResource(R.drawable.wrong_symbol);
            voterIDErrorMessage.setText("Voter ID detection failed");
            IDCheck = false;
        }
        return IDCheck;
    }
}
