package com.sreeharan.myvote_mobileapp;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.sreeharan.myvote_mobileapp.VerifyActivity.locationCheck;

public class LocationSetting {
    private final String TAG = "LocationSetting Class";
    RequestQueue mQueue;
    ProgressBar progress;
    DetailsClass details = new DetailsClass();
    EditText pincodeText;
    Button okButton;
    private String templateUrl = "https://api.postalpincode.in/pincode/";
    public LocationSetting() {
    }

    void setLocation(Context context, ImageView toggleImage, TextView place,
                     Dialog locationDialog, ConnectivityManager cm) {
        Toast.makeText(context, "Setting Location", Toast.LENGTH_SHORT).show();

        locationDialog.setContentView(R.layout.location_popup);
        pincodeText = locationDialog.findViewById(R.id.pincode_text);
        okButton = locationDialog.findViewById(R.id.final_button);
        progress = locationDialog.findViewById(R.id.progress_bar);

        mQueue = Volley.newRequestQueue(context);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "onClick: Clicked the Button");
                if (details.isConnected(context, cm)) {
                    jsonParse(pincodeText.getText().toString().trim(), place, toggleImage, locationDialog);
                } else {
                    locationDialog.dismiss();
                    Toast.makeText(context, "Check your internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
        locationDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        locationDialog.show();
    }

    private void jsonParse(String pincode, TextView place, ImageView toggle, Dialog locationDialog) {
        String url = templateUrl + String.valueOf(pincodeText.getText());
        Log.e(TAG, "jsonParse: Went into the method" + url);
        progress.setVisibility(View.VISIBLE);
        pincodeText.setVisibility(View.INVISIBLE);
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.e(TAG, "onResponse: returned good\n" + response);
                        try {
                            Log.e(TAG, "onResponse: " + response.length());
                            JSONObject root = response.getJSONObject(0);
                            try {
                                JSONArray postOffice = root.getJSONArray("PostOffice");
                                JSONObject object = postOffice.getJSONObject(0);
                                place.setText(object.getString("Division") + " - " + pincode);
                                toggle.setImageResource(R.drawable.correct_symbol);
                                locationCheck = true;
                            } catch (JSONException e) {
                                place.setText(pincode + " is invalid");
                                toggle.setImageResource(R.drawable.wrong_symbol);
                                locationCheck = false;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        progress.setVisibility(View.GONE);
                        locationDialog.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                progress.setVisibility(View.GONE);
                toggle.setImageResource(R.drawable.wrong_symbol);
                place.setText(pincode + " is invalid");
                Log.e(TAG, "onResponse: returned error");
                locationCheck = false;
            }
        });
        mQueue.add(request);
    }
}
