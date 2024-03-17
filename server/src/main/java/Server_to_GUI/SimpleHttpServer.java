package Server_to_GUI;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

@SuppressWarnings("restriction")
public class SimpleHttpServer {

    private HttpServer server;

    private static int Android_id;
    public static double Android_lat;
    public static double Android_long;

    public static int IOT1_id;
    public static double IOT1_Longitude;
    public static double IOT1_Latitude;
    public static double IOT1_Smoke_Sensor_value;
    public static double IOT1_Gas_Sensor_value;
    public static double IOT1_Thermal_Sensor_value;
    public static double IOT1_UV_Sensor_value;
    public static int IOT1_danger_level;

    public static int IOT2_id;
    public static double IOT2_Longitude;
    public static double IOT2_Latitude;
    public static double IOT2_Smoke_Sensor_value;
    public static double IOT2_Gas_Sensor_value;
    public static double IOT2_Thermal_Sensor_value;
    public static double IOT2_UV_Sensor_value;
    public static int IOT2_danger_level;


    public SimpleHttpServer() {
        try {
            server = HttpServer.create(new InetSocketAddress(8000), 0);
            server.createContext("/data_endpoint", new DataHandler());
            server.setExecutor(null); // Use the default executor
            server.start();
            System.out.println("Server started on port 8000");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopServer() {
        server.stop(0);
    }

    public void set_values(int Android_id, double Android_lat, double Android_long, int IOT1_id, double IOT1_Longitude, double IOT1_Latitude, double IOT1_Smoke_Sensor_value, double IOT1_Gas_Sensor_value, double IOT1_Thermal_Sensor_value, double IOT1_UV_Sensor_value, int IOT1_danger_level, int IOT2_id, double IOT2_Longitude, double IOT2_Latitude, double IOT2_Smoke_Sensor_value, double IOT2_Gas_Sensor_value, double IOT2_Thermal_Sensor_value, double IOT2_UV_Sensor_value, int IOT2_danger_level){
        setAndroid_id(Android_id);
        setAndroid_lat(Android_lat);
        setAndroid_long(Android_long);
        setIOT1_id(IOT1_id);
        setIOT1_Longitude(IOT1_Longitude);
        setIOT1_Latitude(IOT1_Latitude);
        setIOT1_Smoke_Sensor_value(IOT1_Smoke_Sensor_value);
        setIOT1_Gas_Sensor_value(IOT1_Gas_Sensor_value);
        setIOT1_Thermal_Sensor_value(IOT1_Thermal_Sensor_value);
        setIOT1_UV_Sensor_value(IOT1_UV_Sensor_value);
        setIOT1_danger_level(IOT1_danger_level);
        setIOT2_id(IOT2_id);
        setIOT2_Longitude(IOT2_Longitude);
        setIOT2_Latitude(IOT2_Latitude);
        setIOT2_Smoke_Sensor_value(IOT2_Smoke_Sensor_value);
        setIOT2_Gas_Sensor_value(IOT2_Gas_Sensor_value);
        setIOT2_Thermal_Sensor_value(IOT2_Thermal_Sensor_value);
        setIOT2_UV_Sensor_value(IOT2_UV_Sensor_value);
        setIOT2_danger_level(IOT2_danger_level);
    }

    public void setAndroid_id(int id){
        Android_id = id;
    }

    public void setAndroid_lat(double lat){
        Android_lat = lat;
    }

    public void setAndroid_long(double lon){
        Android_long = lon;
    }

    public void setIOT1_id(int id){
        IOT1_id = id;
    }

    public void setIOT1_Longitude(double lon){
        IOT1_Longitude = lon;
    }

    public void setIOT1_Latitude(double lat){
        IOT1_Latitude = lat;
    }

    public void setIOT1_Smoke_Sensor_value(double value){
        IOT1_Smoke_Sensor_value = value;
    }

    public void setIOT1_Gas_Sensor_value(double value){
        IOT1_Gas_Sensor_value = value;
    }

    public void setIOT1_Thermal_Sensor_value(double value){
        IOT1_Thermal_Sensor_value = value;
    }

    public void setIOT1_UV_Sensor_value(double value){
        IOT1_UV_Sensor_value = value;
    }

    public void setIOT1_danger_level(int level){
        IOT1_danger_level = level;
    }

    public void setIOT2_id(int id){
        IOT2_id = id;
    }

    public void setIOT2_Longitude(double lon){
        IOT2_Longitude = lon;
    }

    public void setIOT2_Latitude(double lat){
        IOT2_Latitude = lat;
    }

    public void setIOT2_Smoke_Sensor_value(double value){
        IOT2_Smoke_Sensor_value = value;
    }

    public void setIOT2_Gas_Sensor_value(double value){
        IOT2_Gas_Sensor_value = value;
    }

    public void setIOT2_Thermal_Sensor_value(double value){
        IOT2_Thermal_Sensor_value = value;
    }

    public void setIOT2_UV_Sensor_value(double value){
        IOT2_UV_Sensor_value = value;
    }

    public void setIOT2_danger_level(int level){
        IOT2_danger_level = level;
    }

    // Send the data to the GUI when requested
    static class DataHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {    
            // Prepare the JSON response
            String response = "{\"Android_id\":" + Android_id + ",\"Android_lat\":" + Android_lat + ",\"Android_long\":" + Android_long + ",\"Iot1_id\":" + IOT1_id + ",\"Iot1_smoke\":" + IOT1_Smoke_Sensor_value + ",\"Iot1_gas\":" + IOT1_Gas_Sensor_value + ",\"Iot1_temp\":" + IOT1_Thermal_Sensor_value + ",\"Iot1_uv\":" + IOT1_UV_Sensor_value + ",\"Iot1_lat\":" + IOT1_Latitude + ",\"Iot1_lng\":" + IOT1_Longitude + ",\"Iot1_danger\":" + IOT1_danger_level + ",\"Iot2_id\":" + IOT2_id + ",\"Iot2_smoke\":" + IOT2_Smoke_Sensor_value + ",\"Iot2_gas\":" + IOT2_Gas_Sensor_value + ",\"Iot2_temp\":" + IOT2_Thermal_Sensor_value + ",\"Iot2_uv\":" + IOT2_UV_Sensor_value + ",\"Iot2_lat\":" + IOT2_Latitude + ",\"Iot2_lng\":" + IOT2_Longitude + ",\"Iot2_danger\":" + IOT2_danger_level + "}";
    
            // Set CORS headers to allow requests from any origin
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*"); // Allow requests from any origin
            exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET"); // Allow only GET requests
    
            // Send the response
            exchange.sendResponseHeaders(200, response.getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }
    
}
