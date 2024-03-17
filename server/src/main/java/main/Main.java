package main;

import mqtt.*;
import java.util.Scanner;
import Server_to_GUI.*;
import database.*;

public class Main {
    
    public static void main(String[] args) {
        // Create new mqtt subscribers and publishers
        String broker = "tcp://localhost:1883";
        String IOT1_topic = "2024project1";
        String IOT2_topic = "2024project2";
        String Android_topic = "2024project3";


        MqttAsyncSubscriberExtended subscriber_IOT1 = new MqttAsyncSubscriberExtended(broker, "sub1");
        MqttAsyncSubscriberExtended subscriber_IOT2 = new MqttAsyncSubscriberExtended(broker, "sub2");
        MqttAsyncSubscriberExtended subscriber_Android = new MqttAsyncSubscriberExtended(broker, "sub3");
        subscriber_IOT1.connect();
        subscriber_IOT2.connect();
        subscriber_Android.connect();

        MqttAsyncPublisher publisher_Android = new MqttAsyncPublisher(broker, Android_topic);
        publisher_Android.connect();

        // Subscribe to topic
        subscriber_IOT1.subscribeToTopic(IOT1_topic);
        subscriber_IOT2.subscribeToTopic(IOT2_topic);
        subscriber_Android.subscribeToTopic(Android_topic);

        // Create server
        SimpleHttpServer server = new SimpleHttpServer();
        server.set_values(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);     // Initialize values

        // Create database connection
        String url = "jdbc:mysql://localhost:3306/IOT_Android";
        String user = "root";
        String password = "";
        ConnectionSql connectionSql = new ConnectionSql();
        connectionSql.connectToDatabase(url, user, password);

        // Create worker thread
        Action worker = new Action(subscriber_IOT1, subscriber_IOT2, subscriber_Android, publisher_Android, server, connectionSql, Android_topic);
        Thread thread = new Thread(worker);
        thread.start();

        
        // Listen for user input to terminate the program and close all connections
        Scanner scanner = new Scanner(System.in);
        while (true) {
            if (scanner.nextLine().equals("exit")) {
                scanner.close();
                thread.interrupt();
                subscriber_IOT1.disconnect();
                subscriber_IOT2.disconnect();
                subscriber_Android.disconnect();
                publisher_Android.disconnect();
                server.stopServer();
                connectionSql.closeConnection();
                System.exit(0);
            }
        }
    }
}