package com.example.androidapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.health.connect.datatypes.Device;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationServices;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private EditText edtID;
    private Button edtLogIn;
    private TextView txtWarningID;
    private RelativeLayout parent;
    private static String DeviceID = "0";
    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pref=getSharedPreferences("settings", MODE_PRIVATE);
        initViews();
        stopGPSService();
        // Defines the use of Log in button
        edtLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initLogIn();
            }
        });
    }

    private boolean isGPSServiceRunning() {
        Log.d(TAG, "isGPSServiceRunning: Started");
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        if(activityManager != null) {
            for(ActivityManager.RunningServiceInfo service :
                    activityManager.getRunningServices(Integer.MAX_VALUE)) {
                if(LocationServices.class.getName().equals(service.service.getClassName())) {
                    if(service.foreground) {
                        return true;
                    }
                }
            }
            return false;
        }
        return false;
    }

    private void stopGPSService() {
        Log.d(TAG, "stopGPSService: Started");
        if(isGPSServiceRunning()) {
            Intent intent = new Intent(getApplicationContext(), GPSHandler.class);
            startService(intent);
            Toast.makeText(this, "GPS service stopped", Toast.LENGTH_SHORT).show();
        }
    }

    // Returns the device ID
    public String getID() {
        Log.i("devID", DeviceID);
        return DeviceID;
    }

     private void initLogIn() {
        Log.d(TAG, "initLogIn: Started");
         SharedPreferences.Editor editor = pref.edit();
         editor.putString("devid",edtID.getText().toString());
         editor.commit();

         DeviceID = pref.getString("devid","0");
        if(ValidateData()) {
            showSnackBar();
            Log.i("deviceID",DeviceID);
            Intent intent = new Intent(MainActivity.this, SubscribeActivity.class);
            startActivity(intent);
        }
    }

    private void showSnackBar() {
        Log.d(TAG, "showSnackBar: Started");
        SharedPreferences pref = getSharedPreferences("settings", MODE_PRIVATE);
        txtWarningID.setVisibility(View.GONE);

        String id = pref.getString("devid","0");

        Snackbar.make(parent, id + " logged in", Snackbar.LENGTH_INDEFINITE)
                .setAction("Dismiss", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("devid",edtID.getText().toString());
                        editor.commit();

                        DeviceID = pref.getString("devid","0");
                        edtID.setText(DeviceID);
                    }
                }).show();
    }

    private boolean ValidateData() {
        Log.d(TAG, "ValidateData: Started");

        if(edtID.getText().toString().equals("")) {
            txtWarningID.setVisibility(View.VISIBLE);
            txtWarningID.setText("Enter your Device ID");
            return false;
        }

        return true;
    }

    private void initViews() {
        Log.d(TAG, "initViews: Started");

        edtID = findViewById(R.id.edtID);
        edtLogIn = findViewById(R.id.edtLogIn);
        txtWarningID = findViewById(R.id.txtWarningID);
        parent = findViewById(R.id.parent);
        edtID.setText(pref.getString("devid","0"));

    }
}