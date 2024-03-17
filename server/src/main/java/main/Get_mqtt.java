package main;

import mqtt.MqttAsyncSubscriberExtended;
import strings.*;
import danger.*;

public class Get_mqtt implements Runnable{
    MqttAsyncSubscriberExtended subscriber_IOT;
    public String receivedMessage;

    public int IOT_id;
    public int Battery_Percentage;
    public double Longitude;
    public double Latitude;
    public double Smoke_Sensor_value;
    public double Gas_Sensor_value;
    public double Thermal_Sensor_value;
    public double UV_Sensor_value;

    public int danger_level;

    public Get_mqtt(MqttAsyncSubscriberExtended subscriber_IOT){
        this.subscriber_IOT = subscriber_IOT;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            if ( subscriber_IOT.receivedMessage.length() != 0) {        //if the received message is empty go back to waiting after marking the values as invalid
                synchronized (subscriber_IOT.receivedMessage){          //synchronize the received message so that it is not changed while being read and rewritten
                    //System.out.println("Message received from IOT: " + subscriber_IOT.receivedMessage);
                    receivedMessage = subscriber_IOT.receivedMessage;
                    
                    // Clear the received message
                    subscriber_IOT.receivedMessage = "";
                }
                Sensors Message = new Sensors(receivedMessage);     //create a new Sensors object that extracts the values from the IOT message
                IOT_id = Message.getId();
                Battery_Percentage = Message.getBattery();
                Longitude = Message.getLongitude();
                Latitude = Message.getLatitude();
                Smoke_Sensor_value = Message.getSmokeVal();
                Gas_Sensor_value = Message.getGasVal();
                Thermal_Sensor_value = Message.getThermalVal();
                UV_Sensor_value = Message.getUvVal();

                // Create danger object
                Danger danger = new Danger(Smoke_Sensor_value, Gas_Sensor_value, Thermal_Sensor_value, UV_Sensor_value);    //create a new Danger object that calculates the danger level
                danger_level = danger.getDangerLevel();
                
                // System.out.println("IOT ID: " + IOT_id);
                // System.out.println("Battery Percentage: " + Battery_Percentage);
                // System.out.println("Longitude: " + Longitude);
                // System.out.println("Latitude: " + Latitude);
                // System.out.println("Smoke Sensor Value: " + Smoke_Sensor_value);
                // System.out.println("Gas Sensor Value: " + Gas_Sensor_value);
                // System.out.println("Thermal Sensor Value: " + Thermal_Sensor_value);
                // System.out.println("UV Sensor Value: " + UV_Sensor_value);
                // System.out.println("Danger Level: " + danger_level);
            }
            else{       //if the received message is empty mark the values as invalid
                Smoke_Sensor_value = -1000;
                Gas_Sensor_value = -1000;
                Thermal_Sensor_value = -1000;
                UV_Sensor_value = -1000;
                danger_level = -1;
            }
            try {
                Thread.sleep(1000);     //sleep for 1 second and then repeat
            } catch (InterruptedException e) {
                e.printStackTrace();
            }   
        }
            
    }

    public int getIOT_id(){
        return IOT_id;
    }

    public int getBattery_Percentage(){
        return Battery_Percentage;
    }

    public double getLongitude(){
        return Longitude;
    }

    public double getLatitude(){
        return Latitude;
    }

    public double getSmoke_Sensor_value(){
        return Smoke_Sensor_value;
    }

    public double getGas_Sensor_value(){
        return Gas_Sensor_value;
    }

    public double getThermal_Sensor_value(){
        return Thermal_Sensor_value;
    }

    public double getUV_Sensor_value(){
        return UV_Sensor_value;
    }

    public int getDanger_level(){
        return danger_level;
    }
}


