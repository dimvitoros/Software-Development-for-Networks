package com.example.androidapp;

import static android.app.PendingIntent.getActivity;
import static java.lang.Long.parseLong;
import static java.lang.Thread.sleep;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.location.LocationServices;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.eclipse.paho.android.service.MqttAndroidClient;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class SubscribeActivity extends AppCompatActivity {
    private static final String TAG = "SubscribeActivity";
    Runnable runnable;
    Handler handler = new Handler();
    Handler handler_ConnChk = new Handler();
    private Button auto_subscription;
    private Button man_subscription;
    private Button stop_subscription;
    private boolean connection_success = false;
    private static String BROKER_URL = "tcp://broker.hivemq.com:1883"; //this is just default text, it gets changed
    private String CLIENT_ID = new MainActivity().getID();
    private String TOPIC = "2024project"; //this is just default text, it gets change
    @NonNull
    private MqttAndroidClient mqttAndroidClient;
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;
    private static final int PERMISSION_CODE_READ_EXTERNAL_STORAGE = 999;
    private static int delay = 4000;
    private boolean auto = false;
    private boolean isGPSEnabled = false;
    private List<String> LongitudeList1, LatitudeList1;
    private List<String> LongitudeList2, LatitudeList2;
    private int xmlLength1, xmlLength2;
    private Thread backgroundThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscribe);

        SharedPreferences pref = getSharedPreferences("settings", MODE_PRIVATE);
        BROKER_URL = pref.getString("savedIP","tcp://broker.hivemq.com") + ":" +pref.getString("savedPort","1883");

        TOPIC = "2024project" + CLIENT_ID;
        String MsgLog = "will connect to " + BROKER_URL + " on topic " + TOPIC;
        Log.i("connection status", MsgLog);
        initViews();
        parseXML();
        MqttConnect();
        permissionsGPS();
        Subscription();
    }
    private void mqttPublishManual() throws JSONException {
        Log.d(TAG, "mqttPublishManual: Started");
        String time = new Settings().getTime();
        // Start a new thread
        backgroundThread = new Thread(new Runnable() {
            @Override
            public void run() {
                // Perform background task here
                SharedPreferences pref = getSharedPreferences("settings", MODE_PRIVATE);
                // Get the time slot of location requests from the user
                String timeSlot = time;
                int numberOfMessages = Integer.parseInt(timeSlot);
                // Get a random number between 1 and 2
                int rand = getRandomNumber(1, 2);
                if (rand == 1) {
                    if (timeSlot.equals("0")) {
                        Iterator<String> Longitude1Iter = LongitudeList1.iterator();
                        Iterator<String> Latitude1Iter = LatitudeList1.iterator();
                        for (int i = 0; i < xmlLength1; i++) {
                            JSONObject dataMessage = null;
                            dataMessage = new JSONObject();
                            // Add the device ID to the JSON file
                            try {
                                dataMessage.put("App devID", CLIENT_ID);
                                dataMessage.put("Longitude", Longitude1Iter.next());
                                dataMessage.put("Latitude", Latitude1Iter.next());
                                MqttMessage message = new MqttMessage();
                                message.setPayload(dataMessage.toString().getBytes(StandardCharsets.UTF_8));
                                String msg = new String(message.getPayload());
                                Log.e("MQTT Publish", msg);
                                publishMessage(TOPIC, String.valueOf(message));
                                sleep(1000);
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            } catch (InterruptedException e) {
                                return;
                            }
                        }
                    }
                    else { //timeslot is 1
                        Iterator<String> Longitude1Iter = LongitudeList1.iterator();
                        Iterator<String> Latitude1Iter = LatitudeList1.iterator();
                        if (numberOfMessages > xmlLength1) {
                            numberOfMessages = xmlLength1;
                        }
                        for (int i = 0; i < numberOfMessages; i++) {
                            JSONObject dataMessage = null;
                            dataMessage = new JSONObject();
                            // Add the device ID to the JSON file
                            try {
                                dataMessage.put("App devID", CLIENT_ID);
                                dataMessage.put("Longitude", Longitude1Iter.next());
                                dataMessage.put("Latitude", Latitude1Iter.next());
                                MqttMessage message = new MqttMessage();
                                message.setPayload(dataMessage.toString().getBytes(StandardCharsets.UTF_8));
                                String msg = new String(message.getPayload());
                                Log.e("MQTT Publish", msg);
                                publishMessage(TOPIC, String.valueOf(message));
                                sleep(1000);
                            } catch (JSONException  e) {
                                throw new RuntimeException(e);
                            }
                            catch (InterruptedException e) {
                                return;
                            }
                        }
                    }
                }
                else { //rand is 1
                    if (timeSlot.equals("0")) {
                        Iterator<String> Longitude2Iter = LongitudeList2.iterator();
                        Iterator<String> Latitude2Iter = LatitudeList2.iterator();
                        for (int i = 0; i < xmlLength2; i++) {
                            JSONObject dataMessage = null;
                            dataMessage = new JSONObject();
                            // Add the device ID to the JSON file
                            try {
                                dataMessage.put("App devID", CLIENT_ID);
                                dataMessage.put("Longitude", Longitude2Iter.next());
                                dataMessage.put("Latitude", Latitude2Iter.next());
                                MqttMessage message = new MqttMessage();
                                message.setPayload(dataMessage.toString().getBytes(StandardCharsets.UTF_8));
                                String msg = new String(message.getPayload());
                                Log.e("MQTT Publish", msg);
                                publishMessage(TOPIC, String.valueOf(message));
                                sleep(1000);
                            } catch (JSONException  e) {
                                throw new RuntimeException(e);
                            }
                            catch (InterruptedException e) {
                                return;
                            }
                        }
                    }
                    else { //timeslot is 1
                        Iterator<String> Longitude2Iter = LongitudeList2.iterator();
                        Iterator<String> Latitude2Iter = LatitudeList2.iterator();
                        if (numberOfMessages > xmlLength2) {
                            numberOfMessages = xmlLength2;
                        }
                        for (int i = 0; i < numberOfMessages; i++) {
                            JSONObject dataMessage = null;
                            dataMessage = new JSONObject();
                            // Add the device ID to the JSON file
                            try {
                                dataMessage.put("App devID", CLIENT_ID);
                                dataMessage.put("Longitude", Longitude2Iter.next());
                                dataMessage.put("Latitude", Latitude2Iter.next());
                                MqttMessage message = new MqttMessage();
                                message.setPayload(dataMessage.toString().getBytes(StandardCharsets.UTF_8));
                                String msg = new String(message.getPayload());
                                Log.e("MQTT Publish", msg);
                                publishMessage(TOPIC, String.valueOf(message));
                                sleep(1000);
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                            catch (InterruptedException e) {
                                return;
                            }
                        }
                    }
                }
            }
        });
        backgroundThread.start();

    }
    // Get a random number
    public int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }
    private void mqttPublishAuto() {
        Log.d(TAG, "mqttPublishAuto: Started");
        SharedPreferences pref = getSharedPreferences("settings", MODE_PRIVATE);
        JSONObject dataMessage = null;
        try {
            // Prepare the JSON
            dataMessage = new JSONObject();
            // Add the device ID to the JSON file
            dataMessage.put("App devID", CLIENT_ID);
            // Check if the automated publish is chosen
            dataMessage.put("Longitude", pref.getString("Longitude", ""));
            dataMessage.put("Latitude", pref.getString("Latitude", ""));
            } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        // Create a new MQTT message
            MqttMessage message = new MqttMessage();
            message.setPayload(dataMessage.toString().getBytes(StandardCharsets.UTF_8));
            String msg = new String(message.getPayload());
            Log.e("MQTT Publish", msg);
            publishMessage(TOPIC, String.valueOf(message));
        }



    //Functions for XML Parsing
    public List<String> parseLongitude(Context context, int xmlResourceId) {
        List<String> longitudeList = new ArrayList<>();

        try {
            Resources res = context.getResources();
            XmlResourceParser xmlParser = res.getXml(xmlResourceId);

            int eventType = xmlParser.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    String tagName = xmlParser.getName();
                    if ("vehicle".equals(tagName)) {
                        String x = xmlParser.getAttributeValue(null, "x");
                        if (x != null) {
                            longitudeList.add(x);
                        }
                    }
                }
                eventType = xmlParser.next();
            }
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }

        return longitudeList;
    }

    public List<String> parseLatitude(Context context, int xmlResourceId) {
        List<String> latitudeList = new ArrayList<>();

        try {
            Resources res = context.getResources();
            XmlResourceParser xmlParser = res.getXml(xmlResourceId);

            int eventType = xmlParser.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    String tagName = xmlParser.getName();
                    if ("vehicle".equals(tagName)) {
                        String y = xmlParser.getAttributeValue(null, "y");
                        if (y != null) {
                            latitudeList.add(y);
                        }
                    }
                }
                eventType = xmlParser.next();
            }
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }

        return latitudeList;
    }
    @Override
    protected void onResume() {
        handler_ConnChk.postDelayed(runnable = new Runnable() {
            public void run() {
                handler_ConnChk.postDelayed(runnable, delay);
                checkConnection();
            }
        }, delay);
        super.onResume();
    }
    private void parseXML() {
        Log.d(TAG, "parseXML: Started");

        try {
            LongitudeList1 = parseLongitude(this,R.xml.android_1);
            LatitudeList1 = parseLatitude(this,R.xml.android_1);
            xmlLength1 = LongitudeList1.size();
            //Log.d("XML Sizes (1)", String.valueOf(xmlLength1));
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            LongitudeList2 = parseLongitude(this,R.xml.android_2);
            LatitudeList2 = parseLatitude(this,R.xml.android_2);
            xmlLength2 = LongitudeList1.size();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void onPause() {
        handler_ConnChk.removeCallbacks(runnable);
        super.onPause();
    }
    private boolean isGPSServiceRunning() {
        Log.d(TAG, "isGPSServiceRunning: Started");
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null) {
            for (ActivityManager.RunningServiceInfo service :
                    activityManager.getRunningServices(Integer.MAX_VALUE)) {
                if (com.example.androidapp.GPSHandler.class.getName().equals(service.service.getClassName()))
                    // If the service is running
                    return true;
            }
        }

        return false;
    }

    private void startGPSService() {
        //Status value holds whether or not the location service is already running
        boolean status = false;
        //Run Check
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null) {
            for (ActivityManager.RunningServiceInfo service :
                    activityManager.getRunningServices(Integer.MAX_VALUE)) {
                if (com.example.androidapp.GPSHandler.class.getName().equals(service.service.getClassName()))
                    // If the service is running
                    status=true;
            }
        }
        if (!status) {
            // Create an intent to start the location service
            Intent intent = new Intent(getApplicationContext(), com.example.androidapp.GPSHandler.class);
            startService(intent);
            Toast.makeText(this, "GPS Tracking is enabled", Toast.LENGTH_SHORT).show();

        }
    }

    private void stopGPSService() {
        //Status value holds whether or not the location service is already running
        boolean status = false;
        //Run Check
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null) {
            for (ActivityManager.RunningServiceInfo service :
                    activityManager.getRunningServices(Integer.MAX_VALUE)) {
                if (com.example.androidapp.GPSHandler.class.getName().equals(service.service.getClassName()))
                    // If the service is running
                    status=true;
            }
        }
        if (status) {
            // Create an intent to stop the location service
            Intent intent = new Intent(getApplicationContext(), com.example.androidapp.GPSHandler.class);
            stopService(intent);
            Toast.makeText(this, "GPS Tracking is disabled", Toast.LENGTH_SHORT).show();
        }
    }

    // Check the permissions to enable GPS
    private void permissionsGPS() {
        // check if the app has permission to access the device's location
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // if the permission has not been granted, request it
            ActivityCompat.requestPermissions(SubscribeActivity.this, new String[] {
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, REQUEST_CODE_LOCATION_PERMISSION);
        }

        // check if the app has permission to access the device's location
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
        }

        // check if the app has permission to read external storage
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            // if the permission has not been granted, request it
            requestPermissions(new String[] {
                    Manifest.permission.READ_EXTERNAL_STORAGE
            }, PERMISSION_CODE_READ_EXTERNAL_STORAGE);
        }
    }

    // Define the buttons for manual or automated subscription
    private void Subscription() {
        Log.d(TAG, "Subscription: Started");

        // The user can press the according button and choose the type of subscription
        // or the termination of the subscription
        auto_subscription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences pref = getSharedPreferences("settings", MODE_PRIVATE);
                if(ContextCompat.checkSelfPermission(
                        getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(SubscribeActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE_LOCATION_PERMISSION);
                } else {
                    auto = true;
                    startGPSService();
                      //handler for communication
                    long startTime = System.currentTimeMillis();
                    handler.postDelayed(runnable = new Runnable() {
                        public void run() {
                            handler.postDelayed(runnable, 1000);
                            if (((parseLong(pref.getString("savedTime","0"))) > 0) && (System.currentTimeMillis() - startTime > 1000* parseLong( pref.getString("savedTime","0")))){
                                handler.removeCallbacksAndMessages(null);
                                stopGPSService();
                                Log.i("timer", "Timer expired");
                            }
                            mqttPublishAuto();
                        }
                    }, 1000);
                }
            }
        });

        man_subscription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(
                        getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(SubscribeActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE_LOCATION_PERMISSION);
                } else {
                    auto = false;
                    try {
                        mqttPublishManual();
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });

        stop_subscription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if (auto) {
                handler.removeCallbacksAndMessages(null);
                stopGPSService();
            }
            else
            {
                if (backgroundThread.isAlive())
                {
                    backgroundThread.interrupt();
                }
            }

            }
        });
    }

    // Defines the connection of the MQTT
    // https://www.hivemq.com/article/mqtt-client-library-enyclopedia-paho-android-service/
    private void MqttConnect() {
        Log.d(TAG, "MqttConnect: Started");

        mqttAndroidClient = new MqttAndroidClient(this.getApplicationContext(), BROKER_URL, CLIENT_ID);
        try {
            IMqttToken token = mqttAndroidClient.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Log.e(TAG, "onSuccess");
                    connection_success = true;
                    subscribeToTopic();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Log.e(TAG, "onFailure");

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    protected void onDestroy() {
        Log.d(TAG, "onDestroy: Started");
        disconnect_mqtt();
        handler.removeCallbacksAndMessages(null);
        stopGPSService();
        super.onDestroy();
    }
private void disconnect_mqtt(){
    try {
        IMqttToken disconToken = mqttAndroidClient.disconnect();
        disconToken.setActionCallback(new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
                // we are now successfully disconnected
                Log.d(TAG, "MQTT successfully disconnected ");
            }

            @Override
            public void onFailure(IMqttToken asyncActionToken,
                                  Throwable exception) {
                // something went wrong, but probably we are disconnected anyway
                Log.d(TAG, "MQTT did not disconnected successfully ");
            }
        });
    } catch (MqttException e) {
        e.printStackTrace();
    }
}
    private void publishMessage(String topic, String message) {
        Log.d(TAG, "publishMessage: Started");
        //Toast.makeText(this, "Publishing message: " + message, Toast.LENGTH_SHORT).show();

        byte[] encodedPayload = new byte[0];
        try {
            encodedPayload = message.getBytes("UTF-8");
            MqttMessage mqttMessage = new MqttMessage(encodedPayload);
            mqttAndroidClient.publish(topic, mqttMessage);
        } catch (MqttException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private void subscribeToTopic() {
        Log.d(TAG, "subscribeToTopic: Started");
       try {
           mqttAndroidClient.subscribe(TOPIC, 0);
           mqttAndroidClient.setCallback(new MqttCallback() {
               @Override
               public void connectionLost(Throwable cause) {
                   Log.d(TAG, "Subscribe service is not connected");
               }

               @Override
               public void messageArrived(String topic, MqttMessage message) throws Exception {
                   String received = new String(message.getPayload());
                   Log.d(TAG, "topic: "+ TOPIC);
                   Log.d(TAG, "message: " + received);
                   if (received.startsWith("Danger")) {
                       notificationOfTopic(received);
                   }
               }

               @Override
               public void deliveryComplete(IMqttDeliveryToken token) {
                   //Toast.makeText(SubscribeActivity.this, "Subscribe has been completed", Toast.LENGTH_SHORT).show();

               }
           });
       } catch (MqttException e) {
           throw new RuntimeException(e);
       }
    }

    // Show the option menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu: Started");
        getMenuInflater().inflate(R.menu.menu_item, menu);
        return true;
    }

    // Defines the use of each item in the menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected: Started");
        switch (item.getItemId()) {

            case R.id.settings:
               // Toast.makeText(this, "You have clicked settings",Toast.LENGTH_SHORT).show();
                gotoSettings();
                return true;

            case R.id.exit:
               // Toast.makeText(this, "You have clicked log out", Toast.LENGTH_SHORT).show();
                exitApp();
                return true;

            default:
            return super.onOptionsItemSelected(item);
        }
    }

    private void gotoSettings() {
        Log.d(TAG, "gotoSettings: Started");

        Intent intent = new Intent(SubscribeActivity.this, Settings.class);
        startActivity(intent);
    }

    private void exitApp() {
        Log.d(TAG, "exitSubscribe: Started");

        Dialog dialog = new Dialog(this, R.style.DialogStyle);
        dialog.setContentView(R.layout.popwindow);

        ImageView btn_close = dialog.findViewById(R.id.btn_close);
        Button btn_no = dialog.findViewById(R.id.btn_no);
        Button btn_yes = dialog.findViewById(R.id.btn_yes);
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopGPSService();
                dialog.dismiss();
            }
        });

        btn_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    dialog.dismiss();
            }
        });

        btn_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAffinity();
                System.exit(0);
            }
        });

        dialog.getWindow().setBackgroundDrawableResource(R.drawable.bg_window);
        dialog.show();
    }

    private void initViews() {
        Log.d(TAG, "initViews: Started");

        auto_subscription = findViewById(R.id.auto_subscription);
        man_subscription = findViewById(R.id.man_subscription);
        stop_subscription = findViewById(R.id.stop_subscription);
        if(isGPSEnabled) {
            startGPSService();
        }
    }

    private void notificationOfTopic(String message) {
        String channelid = "CHANNEL_ID_NOTIFICATION";
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelid)
                .setSmallIcon(R.drawable.baseline_notification_important_24)
                .setContentTitle("WARNING")
                .setContentText(message)
                .setSound(alarmSound)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_MAX);

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0,
                intent, PendingIntent.FLAG_MUTABLE);
        builder.setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = notificationManager
                    .getNotificationChannel(channelid);
            if(notificationChannel == null) {
                int importance = NotificationManager.IMPORTANCE_HIGH;
                notificationChannel = new NotificationChannel(channelid, "warning", importance);
                notificationChannel.setLightColor(android.R.color.holo_red_dark);
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }

        notificationManager.notify(0,builder.build());
    }
    private void checkConnection() {
        if(!isOnline()) {
            Dialog dialog = new Dialog(this, R.style.DialogStyle);
            dialog.setContentView(R.layout.network_connection);

            ImageView btn_close = dialog.findViewById(R.id.btn_close);
            btn_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            dialog.getWindow().setBackgroundDrawableResource(R.drawable.bg_window);
            dialog.show();
        }
    }

    public boolean isOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if(connectivityManager != null) {
            networkInfo = connectivityManager.getActiveNetworkInfo();
        }

        return  networkInfo != null && networkInfo.isConnected();
    }

}