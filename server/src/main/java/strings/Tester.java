// *** FOR TESTING PURPOSES ***

package strings;


public class Tester {
    public static void main(String[] args) {
        // Test input string
    	// 3 Substrings
    	//String input = "[{\"IOT Device ID\":\"3\",\"Battery Percentage\":100,\"Longitude\":\"37.421998\",\"Latitude\":\"-122.084000\"},{\"Sensor Type\":\"Smoke Sensor\",\"Sensor Val\":0.09506060183048248},{\"Sensor Type\":\"Gas Sensor\",\"Sensor Val\":2.1449673175811768}]";
    	
    	// 4 Substrings - Uv Sensor
    	//String input = "[{\"IOT Device ID\":\"3\",\"Battery Percentage\":100,\"Longitude\":\"37.421998\",\"Latitude\":\"-122.084000\"},{\"Sensor Type\":\"Smoke Sensor\",\"Sensor Val\":0.09506060183048248},{\"Sensor Type\":\"Gas Sensor\",\"Sensor Val\":2.1449673175811768},{\"Sensor Type\":\"UV radiation Sensor\",\"Sensor Val\":3.3956878185272217}]";
    	
    	// 4 Substrings - Thermal Sensor
    	//String input = "[{\"IOT Device ID\":\"3\",\"Battery Percentage\":100,\"Longitude\":\"37.421998\",\"Latitude\":\"-122.084000\"},{\"Sensor Type\":\"Smoke Sensor\",\"Sensor Val\":0.09506060183048248},{\"Sensor Type\":\"Gas Sensor\",\"Sensor Val\":2.1449673175811768},{\"Sensor Type\":\"Thermal Sensor\",\"Sensor Val\":3.0065667629241943}]";
    	
    	// 5 Substrings - Thermal - Uv
        //String input = "[{\"IOT Device ID\":\"3\",\"Battery Percentage\":100,\"Longitude\":\"37.421998\",\"Latitude\":\"-122.084000\"},{\"Sensor Type\":\"Smoke Sensor\",\"Sensor Val\":0.09506060183048248},{\"Sensor Type\":\"Gas Sensor\",\"Sensor Val\":2.1449673175811768},{\"Sensor Type\":\"Thermal Sensor\",\"Sensor Val\":3.3956878185272217},{\"Sensor Type\":\"UV radiation Sensor\",\"Sensor Val\":3.0065667629241943}]";
    	
    	// 5 Substrings - Uv - Thermal
    	//String input = "[{\"IOT Device ID\":\"3\",\"Battery Percentage\":100,\"Longitude\":\"37.421998\",\"Latitude\":\"-122.084000\"},{\"Sensor Type\":\"Smoke Sensor\",\"Sensor Val\":0.09506060183048248},{\"Sensor Type\":\"Gas Sensor\",\"Sensor Val\":2.1449673175811768},{\"Sensor Type\":\"UV radiation Sensor\",\"Sensor Val\":3.3956878185272217},{\"Sensor Type\":\"Thermal Sensor\",\"Sensor Val\":3.0065667629241943}]";
    	
        // ANDROID STRING
        String input = "[{\"ANDROID Device ID\":\"3\",\"Longitude\":\"37.421998\",\"Latitude\":\"-122.084000\"}]";
        
        // Create Sensor object
        //Sensors sensors = new Sensors(input);
        AndroidData data = new AndroidData(input);
        
        /*
        
        // Test getter methods
        System.out.println("Iot ID: " + sensors.getId());
        System.out.println("Battery: " + sensors.getBattery());
        System.out.println("Longitude: " + sensors.getLongitude());
        System.out.println("Latitude: " + sensors.getLatitude());
        System.out.println("Smoke Sensor: " + sensors.getSmokeVal());
        System.out.println("Gas Sensor: " + sensors.getGasVal());
        System.out.println("Thermal Sensor: " + sensors.getThermalVal());
        System.out.println("Uv Sensor: " + sensors.getUvVal());
        
        */
        
        System.out.println("Android ID: " + data.getId());
        System.out.println("Longitude: " + data.getLongitude());
        System.out.println("Latitude: " + data.getLatitude());
        
    }
}

