package mqtt;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MqttAsyncPublisher {

    private static String BROKER;
    private static String CLIENT_ID;
	private static Boolean CONNECTED = false;

    private IMqttAsyncClient mqttClient;

    public MqttAsyncPublisher(String broker, String clientId) {
        try {
            BROKER = broker;
            CLIENT_ID = clientId;
            mqttClient = new MqttAsyncClient(BROKER, CLIENT_ID, new MemoryPersistence());
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

    public void publishMessage(String topic, String payload) {
        try {
			while (!CONNECTED) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
            MqttMessage message = new MqttMessage(payload.getBytes());
            mqttClient.publish(topic, message);
            System.out.println("Published message: " + payload + " to topic: " + topic);
        } catch (MqttException e) {
            e.printStackTrace();
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

//     public static void main(String[] args) {
//         String broker = "tcp://broker.hivemq.com:1883";
//         String IOT1_topic = "2024project300";
//         // String IOT2_topic = "2024projectIOT2";
//         // String Android_topic = "2024project96";

//         MqttAsyncPublisher publisher = new MqttAsyncPublisher(broker, "pub");
//         publisher.connect();

//         //publisher.publishMessage(Android_topic, "[{\"Android Device ID\":\"10\",\"Longitude\":\"23.781519\",\"Latitude\":\"37.968907\"}]");
//         publisher.publishMessage(IOT1_topic, "[{\"IOT Device ID\":\"2\",\"Battery Percentage\":100,\"Longitude\":\"23.766362\",\"Latitude\":\"37.969116\"},{\"Sensor Type\":\"Smoke Sensor\",\"Sensor Val\":0.20},{\"Sensor Type\":\"Gas Sensor\",\"Sensor Val\":10},{\"Sensor Type\":\"Thermal Sensor\",\"Sensor Val\":5},{\"Sensor Type\":\"UV radiation Sensor\",\"Sensor Val\":6}]");
//         // publisher.publishMessage(IOT2_topic, "[{\"IOT Device ID\":\"3\",\"Battery Percentage\":100,\"Longitude\":\"23.767931\",\"Latitude\":\"37.967768\"},{\"Sensor Type\":\"Smoke Sensor\",\"Sensor Val\":0.09506060183048248},{\"Sensor Type\":\"Gas Sensor\",\"Sensor Val\":2.1449673175811768},{\"Sensor Type\":\"Thermal Sensor\",\"Sensor Val\":56},{\"Sensor Type\":\"UV radiation Sensor\",\"Sensor Val\":9}]");
//         //publisher.publishMessage(IOT1_topic, "[{\"IOT Device ID\":\"2\",\"Battery Percentage\":100,\"Longitude\":\"23.766362\",\"Latitude\":\"37.969116\"},{\"Sensor Type\":\"Smoke Sensor\",\"Sensor Val\":0.20},{\"Sensor Type\":\"Gas Sensor\",\"Sensor Val\":10}]");
//         //publisher.publishMessage(IOT1_topic, "[{\"IOT Device ID\":\"2\",\"Battery Percentage\":100,\"Longitude\":\"23.766362\",\"Latitude\":\"37.969116\"},{\"Sensor Type\":\"Smoke Sensor\",\"Sensor Val\":0.20},{\"Sensor Type\":\"Gas Sensor\",\"Sensor Val\":10},{\"Sensor Type\":\"Thermal Sensor\",\"Sensor Val\":5}]");
//         //publisher.publishMessage(IOT1_topic, "[{\"IOT Device ID\":\"2\",\"Battery Percentage\":100,\"Longitude\":\"23.766362\",\"Latitude\":\"37.969116\"},{\"Sensor Type\":\"Smoke Sensor\",\"Sensor Val\":0.20},{\"Sensor Type\":\"Gas Sensor\",\"Sensor Val\":10},{\"Sensor Type\":\"UV radiation Sensor\",\"Sensor Val\":6}]");
        
//         //publisher.publishMessage("test/topic2", "This is a test message.");

//         //publisher.publishMessage(Android_topic, "Danger level: High, Distance: 0.000000");
        
//         // Add more publishMessage calls as needed

//         // Wait for a moment before disconnecting
//         try {
//             Thread.sleep(2000);
//         } catch (InterruptedException e) {
//             e.printStackTrace();
//         }

//         publisher.disconnect();
// 		System.exit(0);
//     }
}
