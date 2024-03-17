package com.example.androidapp;

import static java.lang.Long.parseLong;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

public class Settings extends AppCompatActivity {
    private static final String TAG = "Settings";
    private Button backToSubscribe;
    private Button save;
    private EditText edtIP;
    private EditText edtPort;
    private EditText edtTime;
    private EditText edtID;
    private TextView txtWarningID;
    private RelativeLayout parent;
    @NonNull
    private String time = "0", IP = "0", port = "0", ID = "0";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initViews();
        backToSubscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoSubscribe();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSettings();

            }
        });
    }

    public String getTime() {
        return time;
    }

    public String getPort() {
        return port;
    }

    public String getIP() {
        return IP;
    }

    private void saveSettings() {
        Log.d(TAG, "saveSettings: Started");

        if(ValidateData()) {
            showSnackBar();
            SharedPreferences pref = getSharedPreferences("settings", MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("savedIP",IP);
            editor.putString("savedPort",port);
            editor.putString("savedTime",time);
            editor.putString("devid",ID);
            editor.commit();



            PackageManager packageManager = getApplicationContext().getPackageManager();
            Intent intent = packageManager.getLaunchIntentForPackage(getApplicationContext().getPackageName());
            ComponentName componentName = intent.getComponent();
            Intent mainIntent = Intent.makeRestartActivityTask(componentName);
            // Required for API 34 and later
            // Ref: https://developer.android.com/about/versions/14/behavior-changes-14#safer-intents
            mainIntent.setPackage(getApplicationContext().getPackageName());
            getApplicationContext().startActivity(mainIntent);
            Runtime.getRuntime().exit(0);

        }
    }

    private void showSnackBar() {
        Log.d(TAG, "showSnackBar: Started");

        txtWarningID.setVisibility(View.GONE);
        time = edtTime.getText().toString();
        ID = edtID.getText().toString();
        IP = edtIP.getText().toString();
        port = edtPort.getText().toString();
        SharedPreferences pref = getSharedPreferences("settings", MODE_PRIVATE);



        String text_to_show;
        if (parseLong(time) == 0){
            text_to_show = "Server IP: " + IP + " and port: " + port + " for " + time + " seconds ";
        }
        else {
            text_to_show = "Server IP: " + IP + " and port: " + port + "indefinitely";
        }
        Snackbar.make(parent, text_to_show, Snackbar.LENGTH_INDEFINITE)
                .setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.e("dismiss","a");
                    }
                }).show();
    }

    private boolean ValidateData() {
        Log.d(TAG, "ValidateData: Started");

//        if(edtIP.getText().toString().equals("")) {
//            // TODO add the servers IP
//            edtIP.setText("1.1.1.1");
//        } else if (!isValidIPAddress(edtIP)){
//            txtWarningID.setVisibility(View.VISIBLE);
//            txtWarningID.setText(R.string.ip_not_valid);
//            return false;
//        }

        if(edtPort.getText().toString().equals("")) {
            // TODO add the servers port
            edtPort.setText("1");
        } else if (!isValidNumber(edtPort)){
            txtWarningID.setVisibility(View.VISIBLE);
            txtWarningID.setText(R.string.ip_not_valid);
            return false;
        }

        if(edtTime.getText().toString().equals("")) {
            edtTime.setText("0");
        } else if (!isValidNumber(edtTime)){
            txtWarningID.setVisibility(View.VISIBLE);
            txtWarningID.setText(R.string.ip_not_valid);
            return false;
        }

        return true;
    }

    public static boolean isValidNumber(EditText editText) {
        String input = editText.getText().toString().trim();

        // Try parsing the input as a number
        try {
            Double.parseDouble(input);
            // If parsing succeeds, it's a valid number
            return true;
        } catch (NumberFormatException e) {
            // Parsing failed, so it's not a number
            return false;
        }
    }

    public boolean isValidIPAddress(EditText editText) {
        String ipAddress = editText.getText().toString().trim();

        // Regular expression to validate an IP address
        String ipPattern = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

        // Check if the IP address matches the pattern
        return ipAddress.matches(ipPattern);
    }

    private void gotoSubscribe() {
        Log.d(TAG, "gotoSubscribe: Started");

        Intent intent = new Intent(Settings.this, SubscribeActivity.class);
        startActivity(intent);
    }

    private void initViews() {
        Log.d(TAG, "initViews: Started");
        SharedPreferences pref = getSharedPreferences("settings", MODE_PRIVATE);
        backToSubscribe = findViewById(R.id.backToSubscribe);
        save = findViewById(R.id.save);
        edtIP = findViewById(R.id.edtIP);
        edtPort = findViewById(R.id.edtPort);
        edtTime = findViewById(R.id.edtTime);
        edtID = findViewById(R.id.edtID);
        txtWarningID = findViewById(R.id.txtWarningID);
        parent = findViewById(R.id.parent);
        //get saved settings..
        edtIP.setText(pref.getString("savedIP","tcp://broker.hivemq.com"));
        edtPort.setText(pref.getString("savedPort","1883"));
        edtTime.setText(pref.getString("savedTime","0"));
        edtID.setText(pref.getString("devid","0"));
        IP = edtIP.getText().toString();
        port = edtPort.getText().toString();
        time = edtTime.getText().toString();
        Log.i("cur_settings","brokr " + IP + " port "+ port + " time "  + time);
    }

}