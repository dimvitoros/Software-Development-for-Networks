package com.dit.iot23;



import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.BatteryManager;
import android.os.Bundle;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import com.dit.iot23.databinding.ActivityMainBinding;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import androidx.navigation.ui.AppBarConfiguration;

import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

//Ta import pou exei stis diafaneies. Eida yparxei kai ver4, thn opoia den exw xrhsimopoihsei omws.
import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private LinearLayoutManager layoutManager;
    private SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener;
    private RecyclerView recyclerView;
    private AppBarConfiguration appBarConfiguration;
    private SharedPreferences.OnSharedPreferenceChangeListener listener_preferences;
    private ActivityMainBinding binding;
    public ArrayList<Sensor> SensorList = new ArrayList<>(); //current sensors in array
    public NewSensor SelectedSensor;
    //Get intent received through intent.PutExtra from NewSensor activity, which permits adding the (two new) sensors
    public static final int REQUEST_CODE = 1;
    private String preset_position;
    private boolean usegps;
    private SensorAdapter sensorAdapter;
    //used for MQTT connection
    private String IP;
    private String Port;
    MqttAndroidClient client;
    private boolean thermalcreated, uvcreated;
    private String credentials;
    private String iotid, mqtt_topic;
    Handler handler = new Handler();
    Runnable runnable;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE  && resultCode  == RESULT_OK)
        {
            String requiredValue = data.getStringExtra("key");
            switch (requiredValue) {
                case "thermal":
                    addSensorTab(3); //No 3 is Thermal Sensor
                    break;
                case "uv":
                    addSensorTab(4); //No 4 is UV Sensor
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        //https://developer.android.com/training/data-storage/shared-preferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        //https://mobikul.com/using-shared-preference-change-listener/
        //Code for recycler view and adapter init..
        //See: https://developer.android.com/develop/ui/views/layout/recyclerview
        //Recycler view allows for dynamic lists, which is useful because we will be adding new items on the fly
        //(the last 2 sensors, which do not pre-exist).
        recyclerView = findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        sensorAdapter = new SensorAdapter(SensorList);
        recyclerView.setAdapter(sensorAdapter);

        //Find location setting , and if it is yes, start location tracking service
        usegps = sharedPreferences.getBoolean("usegps", false);
        if (!usegps)
        {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            String selected_coordinate = sharedPreferences.getString("coordinate_list", "1");
            editor.putString("Longitude", getResources().getStringArray(R.array.longitude_val)[Integer.parseInt(selected_coordinate) - 1]);
            editor.putString("Latitude", getResources().getStringArray(R.array.latitude_val)[Integer.parseInt(selected_coordinate)- 1]);
            editor.apply();
        }
        //Initialize MQTT Connection options
        iotid = sharedPreferences.getString("iot_id", "");
        IP = sharedPreferences.getString("remote_ip", "");
        mqtt_topic="2024project" + iotid;
        Port = sharedPreferences.getString("remote_port", "");
        credentials = IP +":" +Port;
        try {
            connectmqtt();
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }

        //Ask location permission and enable location detection if needed
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 99);
            //Recheck if it was indeed granted
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                if (usegps) {
                    startGPS();
                }
            }
        } else {
            if (usegps) {
                startGPS();
            }
        }

        //Create default sensors
        addSensorTab(1);
        addSensorTab(2);

        //Shared Preferences Listener allows for options to change dynamically when the user changes.
        sharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if (key.equals("usegps")) {
                    usegps = sharedPreferences.getBoolean("usegps", false);
                    if (usegps) {
                        startGPS();
                    } else {
                        stopGPS();
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        String selected_coordinate = sharedPreferences.getString("coordinate_list", "1");
                        editor.putString("Longitude", getResources().getStringArray(R.array.longitude_val)[Integer.parseInt(selected_coordinate)-1]);
                        editor.putString("Latitude", getResources().getStringArray(R.array.latitude_val)[Integer.parseInt(selected_coordinate)-1]);
                        editor.apply();
                    }
                }
                else if (key.equals("remote_ip")) {
                    // If the IP preference has changed, disconnect from the broker and connect with the new credentials
                    try {
                        disconnect_mqtt();
                    } catch (MqttException e) {
                        throw new RuntimeException(e);
                    }
                    IP = sharedPreferences.getString("remote_ip", "");
                    credentials = IP +":" +Port;
                    try {
                        connectmqtt();
                    } catch (MqttException e) {
                        throw new RuntimeException(e);
                    }
                }
                else if (key.equals("remote_port")) {
                    // If the IP preference has changed, disconnect from the broker and connect with the new credentials
                    try {
                        disconnect_mqtt();
                    } catch (MqttException e) {
                        throw new RuntimeException(e);
                    }
                    Port = sharedPreferences.getString("remote_ip", "");
                    credentials = IP +":" +Port;
                    try {
                        connectmqtt();
                    } catch (MqttException e) {
                        throw new RuntimeException(e);
                    }
                }
                else if (key.equals("iot_id")) {
                    try {
                        disconnect_mqtt();
                    } catch (MqttException e) {
                        throw new RuntimeException(e);
                    }
                    iotid = sharedPreferences.getString("iot_id", "");
                    mqtt_topic="2024project" + iotid;
                    try {
                        connectmqtt();
                    } catch (MqttException e) {
                        throw new RuntimeException(e);
                    }
                }
                else if (key.equals("coordinate_list")) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    String selected_coordinate = sharedPreferences.getString("coordinate_list", "1");
                    editor.putString("Longitude", getResources().getStringArray(R.array.longitude_val)[Integer.parseInt(selected_coordinate)-1]);
                    editor.putString("Latitude", getResources().getStringArray(R.array.latitude_val)[Integer.parseInt(selected_coordinate)-1]);
                    editor.apply();
                }
            }

        };
        sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
    }

    private void disconnect_mqtt() throws MqttException {
        client.close();
        client.disconnect();
        client.setCallback(null);
        client = null;
    }

    private void connectmqtt() throws MqttException {
        //https://www.alibabacloud.com/help/en/iot/use-cases/use-the-paho-mqtt-android-client
        //iotid = MqttClient.generateClientId();
        client = new MqttAndroidClient(this.getApplicationContext(),
                credentials,
                iotid);

        try {
            IMqttToken token = client.connect();
            Log.i("CONNECTION", credentials + iotid);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Log.d("mqqt stats", "onSuccess");
                    try {
                        client.subscribe(mqtt_topic,0);
                    } catch (MqttException e) {
                        throw new RuntimeException(e);
                    }
                }
                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Log.e("mqqt stats", "onFailure");

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }


    }

    private void startGPS() {
        //Status value holds whether or not the location service is already running
        boolean status = false;
        //Run Check
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null) {
            for (ActivityManager.RunningServiceInfo service :
                    activityManager.getRunningServices(Integer.MAX_VALUE)) {
                if (com.dit.iot23.GPS_Service.class.getName().equals(service.service.getClassName()))
                    // If the service is running
                        status=true;
            }
        }
        if (!status) {
            // Create an intent to start the location service
            Intent intent = new Intent(getApplicationContext(), com.dit.iot23.GPS_Service.class);
            startService(intent);
            Toast.makeText(this, "GPS Tracking is enabled", Toast.LENGTH_SHORT).show();

        }
    }
    private void stopGPS() {
        //Status value holds whether or not the location service is already running
        boolean status = false;
        //Run Check
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null) {
            for (ActivityManager.RunningServiceInfo service :
                    activityManager.getRunningServices(Integer.MAX_VALUE)) {
                if (com.dit.iot23.GPS_Service.class.getName().equals(service.service.getClassName()))
                    // If the service is running
                        status=true;
            }
        }
        if (status) {
            // Create an intent to stop the location service
            Intent intent = new Intent(getApplicationContext(), com.dit.iot23.GPS_Service.class);
            stopService(intent);
            Toast.makeText(this, "GPS Tracking is disabled", Toast.LENGTH_SHORT).show();
        }
        //Now
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }
        else if (id==R.id.action_newsensor) {
            Intent intent = new Intent(this, NewSensor.class);
            startActivityForResult(intent , REQUEST_CODE);
        }
        else if (id==R.id.action_exit) {
            new AlertDialog.Builder(MainActivity.this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("IOT Sensor Management Framework")
                    .setMessage("Are you sure that you want to exit IOT Sensors app and stop sending sensor information?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //stop location service
                            stopGPS();
                            try {
                                disconnect_mqtt();
                            } catch (MqttException e) {
                                throw new RuntimeException(e);
                            }
                            finish();
                        }

                    })
                    .setNegativeButton("No", null)
                    .show();
        }
        return super.onOptionsItemSelected(item);
    }
    public void addSensorTab(int senstype) {
        //https://www.javatpoint.com/android-tablayout
        switch (senstype) {
            case 1: //Smoke Sensor
                this.SensorList.add(new Sensor("Smoke Sensor"));
                //sensorViewAdapter.refreshview();
                break;
            case 2:
                this.SensorList.add(new Sensor("Gas Sensor"));
                //sensorViewAdapter.refreshview();
                break;
            case 3:
                if (!thermalcreated) {
                    this.SensorList.add(new Sensor("Thermal Sensor"));
                    sensorAdapter.notifyItemInserted(SensorList.size() - 1);
                    thermalcreated = true;
                    //sensorViewAdapter.refreshview();
                }
                break;
            case 4:
                if (!uvcreated) {
                    this.SensorList.add(new Sensor("UV radiation Sensor"));
                    sensorAdapter.notifyItemInserted(SensorList.size() - 1);
                    uvcreated = true;
                    //sensorViewAdapter.refreshview();
                }
                break;
        }
    }
    //create the JSON
    private void communicate_mqtt() throws JSONException, MqttException {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        //Prepare data which needs to be sent
        //Prepare the JSON
        //find battery status (code from google)
        JSONArray iotinfo = new JSONArray();
        BatteryManager batterycheck = (BatteryManager) getSystemService(BATTERY_SERVICE);
        long cur_bat = batterycheck.getLongProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        JSONObject device_status = new JSONObject();
        device_status.put("IOT Device ID",iotid);
        device_status.put("Battery Percentage",cur_bat);
        device_status.put("Longitude" ,sharedPreferences.getString("Longitude",""));
        device_status.put("Latitude" ,sharedPreferences.getString("Latitude",""));
        iotinfo.put(device_status);
        //Array of sensor data
        for (int i =0; i<SensorList.size();i++){
            Sensor current = SensorList.get(i);
            //We only send data for active sensors
            if (current.getstatus()){
                JSONObject to_append = new JSONObject();
                to_append.put("Sensor Type",current.getType());
                to_append.put("Sensor Val",current.getval());
                iotinfo.put(to_append);
            }
        }
        //Now, get ready to send the message
        MqttMessage message = new MqttMessage();
        message.setPayload(iotinfo.toString().getBytes(StandardCharsets.UTF_8));
        String msg = new String(message.getPayload());
        Log.d("Current status",msg);
        client.publish(mqtt_topic, message);
    }
    @Override
    protected void onResume() {
        handler.postDelayed(runnable = new Runnable() {
            public void run() {
                handler.postDelayed(runnable, 1000);
                try {
                    communicate_mqtt();
                } catch (MqttException | JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, 1000);
        super.onResume();
    }
}