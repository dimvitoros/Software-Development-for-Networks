package main;

import mqtt.MqttAsyncSubscriberExtended;
import strings.AndroidData;

public class Get_Mqtt_Android implements Runnable{
    MqttAsyncSubscriberExtended subscriber_Android;
    public String receivedMessage;

    private int Android_id;
    private double Android_Longitude;
    private double Android_Latitude;

    public Get_Mqtt_Android(MqttAsyncSubscriberExtended subscriber_Android){
        this.subscriber_Android = subscriber_Android;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            if (subscriber_Android.receivedMessage.length() != 0) {         //if the received message is empty go back to waiting
                synchronized (subscriber_Android.receivedMessage){          //synchronize the received message so that it is not changed while being read and rewritten
                    //System.out.println("Message received from Android: " + subscriber_Android.receivedMessage);
                    receivedMessage = subscriber_Android.receivedMessage;
                    
                    // Clear the received message
                    subscriber_Android.receivedMessage = "";
                }
                AndroidData Message = new AndroidData(receivedMessage);     //create a new AndroidData object that extracts the values from the received message
                /*Fetch the values from the AndroidData object*/
                Android_id = Message.getId();
                Android_Longitude = Message.getLongitude();
                Android_Latitude = Message.getLatitude();

                // System.out.println("Android ID: " + Android_id);
                // System.out.println("Android Longitude: " + Android_Longitude);
                // System.out.println("Android Latitude: " + Android_Latitude);
            }
            try {
                Thread.sleep(1000);     //sleep for 1 second and then repeat
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public int getAndroid_id() {
        return Android_id;
    }

    public double getAndroid_Longitude() {
        return Android_Longitude;
    }

    public double getAndroid_Latitude() {
        return Android_Latitude;
    }
}