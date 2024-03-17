package main;

import mqtt.*;
import Server_to_GUI.*;
import database.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.LocalTime;

public class Action implements Runnable{
    private int IOT1_id;
    // private int IOT1_Battery_Percentage;
    private double IOT1_Longitude;
    private double IOT1_Latitude;
    private double IOT1_Smoke_Sensor_value;
    private double IOT1_Gas_Sensor_value;
    private double IOT1_Thermal_Sensor_value;
    private double IOT1_UV_Sensor_value;
    private int IOT1_danger_level;

    private int IOT2_id;
    // private int IOT2_Battery_Percentage;
    private double IOT2_Longitude;
    private double IOT2_Latitude;
    private double IOT2_Smoke_Sensor_value;
    private double IOT2_Gas_Sensor_value;
    private double IOT2_Thermal_Sensor_value;
    private double IOT2_UV_Sensor_value;
    private int IOT2_danger_level;
    

    private int Android_id;
    private double Android_Longitude;
    private double Android_Latitude;

    private MqttAsyncSubscriberExtended subscriber_IOT1;
    private MqttAsyncSubscriberExtended subscriber_IOT2;
    private MqttAsyncSubscriberExtended subscriber_Android;
    private MqttAsyncPublisher publisher_Android;

    private SimpleHttpServer server;

    private ConnectionSql connectionSql;

    private String Android_topic;

    public Action(MqttAsyncSubscriberExtended subscriber_IOT1, MqttAsyncSubscriberExtended subscriber_IOT2, MqttAsyncSubscriberExtended subscriber_Android, MqttAsyncPublisher publisher_Android, SimpleHttpServer server, ConnectionSql connectionSql, String Android_topic){
        this.subscriber_IOT1 = subscriber_IOT1;
        this.subscriber_IOT2 = subscriber_IOT2;
        this.subscriber_Android = subscriber_Android;
        this.publisher_Android = publisher_Android;
        this.server = server;
        this.connectionSql = connectionSql;
        this.Android_topic = Android_topic;
    }

    @Override
    public void run() {
        /*Run different new threads that update the values received from the IOTs every 1 second*/
        Get_mqtt get_mqtt1 = new Get_mqtt(subscriber_IOT1);
        Get_mqtt get_mqtt2 = new Get_mqtt(subscriber_IOT2);
        Thread thread1 = new Thread(get_mqtt1);
        Thread thread2 = new Thread(get_mqtt2);
        thread1.start();
        thread2.start();

        /*Run a new thread that updates the values received from the Android every 1 second*/
        Get_Mqtt_Android get_mqtt_android = new Get_Mqtt_Android(subscriber_Android);
        Thread thread3 = new Thread(get_mqtt_android);
        thread3.start();

        while (!Thread.currentThread().isInterrupted()) {
            update_IOT_values(get_mqtt1, get_mqtt2);        //retrieve the values from the IOTs
            update_Android_values(get_mqtt_android);        //retrieve the values from the Android

            if (IOT1_danger_level > 0 || IOT2_danger_level > 0){        //if there is danger
                /*Publish alert to Android*/
                double distance = get_distance(IOT1_Longitude, IOT1_Latitude, IOT2_Longitude, IOT2_Latitude, Android_Longitude, Android_Latitude, IOT1_danger_level, IOT2_danger_level);
                int max_danger_level = get_max_danger_level(IOT1_danger_level, IOT2_danger_level);      //get the maximum danger level
                if (max_danger_level == 2){
                    publisher_Android.publishMessage(Android_topic, ("Danger Level: High , Distance: " + distance));
                }
                else if (max_danger_level == 1){
                    publisher_Android.publishMessage(Android_topic, ("Danger Level: Medium , Distance: " + distance));
                }

                /*insert event into database*/
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");      //This format is used because the database is set to this format
                DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("HH:mm:ss");
                LocalDate localDate = LocalDate.now();      //get the current date
                LocalTime localTime = LocalTime.now();      //get the current time
                String date = dtf.format(localDate);
                String time = dtf2.format(localTime);

                connectionSql.setValues(date, time, IOT1_id, IOT1_Smoke_Sensor_value, IOT1_Gas_Sensor_value, IOT1_Thermal_Sensor_value, IOT1_UV_Sensor_value, IOT1_Latitude, IOT1_Longitude, IOT1_danger_level, IOT2_id, IOT2_Smoke_Sensor_value, IOT2_Gas_Sensor_value, IOT2_Thermal_Sensor_value, IOT2_UV_Sensor_value, IOT2_Latitude, IOT2_Longitude, IOT2_danger_level);
                Thread threadsql = new Thread(connectionSql);
                threadsql.start();    //insert the event into the database with a new thread
            }


            /*Update values that the GUI requests*/
            server.set_values(Android_id, Android_Latitude, Android_Longitude, IOT1_id, IOT1_Longitude, IOT1_Latitude, IOT1_Smoke_Sensor_value, IOT1_Gas_Sensor_value, IOT1_Thermal_Sensor_value, IOT1_UV_Sensor_value, IOT1_danger_level, IOT2_id, IOT2_Longitude, IOT2_Latitude, IOT2_Smoke_Sensor_value, IOT2_Gas_Sensor_value, IOT2_Thermal_Sensor_value, IOT2_UV_Sensor_value, IOT2_danger_level);

            try {
                Thread.sleep(1000);     //sleep for 1 second and repeat the process
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }



    }

    public void update_IOT_values(Get_mqtt get_mqtt1, Get_mqtt get_mqtt2){
        IOT1_id = get_mqtt1.getIOT_id();
        // IOT1_Battery_Percentage = get_mqtt1.getBattery_Percentage();
        IOT1_Longitude = get_mqtt1.getLongitude();
        IOT1_Latitude = get_mqtt1.getLatitude();
        IOT1_Smoke_Sensor_value = get_mqtt1.getSmoke_Sensor_value();
        IOT1_Gas_Sensor_value = get_mqtt1.getGas_Sensor_value();
        IOT1_Thermal_Sensor_value = get_mqtt1.getThermal_Sensor_value();
        IOT1_UV_Sensor_value = get_mqtt1.getUV_Sensor_value();
        IOT1_danger_level = get_mqtt1.getDanger_level();

        IOT2_id = get_mqtt2.getIOT_id();
        // IOT2_Battery_Percentage = get_mqtt2.getBattery_Percentage();
        IOT2_Longitude = get_mqtt2.getLongitude();
        IOT2_Latitude = get_mqtt2.getLatitude();
        IOT2_Smoke_Sensor_value = get_mqtt2.getSmoke_Sensor_value();
        IOT2_Gas_Sensor_value = get_mqtt2.getGas_Sensor_value();
        IOT2_Thermal_Sensor_value = get_mqtt2.getThermal_Sensor_value();
        IOT2_UV_Sensor_value = get_mqtt2.getUV_Sensor_value();
        IOT2_danger_level = get_mqtt2.getDanger_level();
    }

    public void update_Android_values(Get_Mqtt_Android get_mqtt_android){
        Android_id = get_mqtt_android.getAndroid_id();
        Android_Longitude = get_mqtt_android.getAndroid_Longitude();
        Android_Latitude = get_mqtt_android.getAndroid_Latitude();
    }

    /*This function calculates the distance between the Android and the IOTs. It can determine automatically if it needs to calculate the distance from one IOT or the middle point between the two IOTs*/
    public static double get_distance(double IOT1_Longitude, double IOT1_Latitude, double IOT2_Longitude, double IOT2_Latitude, double Android_Longitude, double Android_Latitude, int IOT1_danger_level, int IOT2_danger_level){
        double distance = 0;
        if (IOT1_danger_level > 0 && IOT2_danger_level > 0){
            double middle_point_longitude = (IOT1_Longitude + IOT2_Longitude) / 2;
            double middle_point_latitude = (IOT1_Latitude + IOT2_Latitude) / 2;
            distance = distance(middle_point_latitude, middle_point_longitude, Android_Latitude, Android_Longitude, "K");
        }
        else if (IOT1_danger_level > 0){
            distance = distance(IOT1_Latitude, IOT1_Longitude, Android_Latitude, Android_Longitude, "K");
        }
        else if (IOT2_danger_level > 0){
            distance = distance(IOT2_Latitude, IOT2_Longitude, Android_Latitude, Android_Longitude, "K");
        }
            
        return distance;
    }
    
    /*Distance calculation function taken from https://www.geodatasource.com/developers/java*/
    private static double distance(double lat1, double lon1, double lat2, double lon2, String unit) {
		if ((lat1 == lat2) && (lon1 == lon2)) {
			return 0;
		}
		else {
			double theta = lon1 - lon2;
			double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
			dist = Math.acos(dist);
			dist = Math.toDegrees(dist);
			dist = dist * 60 * 1.1515;
			if (unit.equals("K")) {
				dist = dist * 1.609344;
			} else if (unit.equals("N")) {
				dist = dist * 0.8684;
			}
			return (dist);
		}
	}

    private static int get_max_danger_level(int IOT1_danger_level, int IOT2_danger_level){
        return Math.max(IOT1_danger_level, IOT2_danger_level);
    }

}