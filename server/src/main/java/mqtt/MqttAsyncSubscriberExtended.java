package mqtt;

// import java.util.Scanner;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MqttAsyncSubscriberExtended implements MqttCallbackExtended {

    private String BROKER;
    private String CLIENT_ID;
    private String TOPIC;
    private Boolean CONNECTED = false;
    public String receivedMessage = "";


    private IMqttAsyncClient mqttClient;

    public MqttAsyncSubscriberExtended(String broker, String clientId) {
        try {
            BROKER = broker;
            CLIENT_ID = clientId;
            mqttClient = new MqttAsyncClient(BROKER, CLIENT_ID, new MemoryPersistence());
            mqttClient.setCallback(this);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void connect() {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true); // Enable automatic reconnection
        try {
            mqttClient.connect(options, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    System.out.println("Connected to broker");
                    CONNECTED = true;
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    System.out.println("Failed to connect to broker: " + exception.getMessage());
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void subscribeToTopic(String topic){
        TOPIC = topic;
        try {
            while (!CONNECTED) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            mqttClient.subscribe(topic, 0, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    System.out.println("Subscribed to topic: " + TOPIC);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    System.out.println("Failed to subscribe to topic: " + TOPIC);
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void connectionLost(Throwable cause) {
        System.out.println("Connection lost! Cause: " + cause.getMessage());

    }

    @Override
    public void messageArrived(String topic, MqttMessage message) {
        //System.out.println("Message received on topic: " + topic);
        String messageContent = new String(message.getPayload());
        char firstChar = messageContent.charAt(0);
        if ((firstChar == '[') || (firstChar == '{')){      //filer out messages sent by me
            //System.out.println("Message content: " + messageContent);
            synchronized (this){
                if (receivedMessage.length() != 0) {
                    receivedMessage = "";
                }
                receivedMessage = messageContent;
            }
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        System.out.println("Message delivery complete.");
    }

    @Override
    public void connectComplete(boolean reconnect, String serverURI) {
        if (reconnect) {
            System.out.println("Reconnected to server: " + serverURI);
            subscribeToTopic(TOPIC);
        }
    }

    public void disconnect() {
        try {
            mqttClient.disconnect();
            System.out.println("Disconnected from broker");
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    // public static void main(String[] args) {
    //     MqttAsyncSubscriberExtended subscriber = new MqttAsyncSubscriberExtended("tcp://localhost:1883", "sub");
    //     MqttAsyncSubscriberExtended subscriber2 = new MqttAsyncSubscriberExtended("tcp://localhost:1883", "sub2");
    //     subscriber.connect();
    //     subscriber2.connect();

    //     subscriber.subscribeToTopic("test/topic");
    //     subscriber2.subscribeToTopic("test/topic2");

    //     // Listen for user input to terminate the program
    //     Scanner scanner = new Scanner(System.in);
    //     while (true) {
    //         if (scanner.nextLine().equals("exit")) {
    //             scanner.close();
    //             subscriber.disconnect();
    //             subscriber2.disconnect();
    //             System.exit(0);
    //         }
    //     }
    // }
}

