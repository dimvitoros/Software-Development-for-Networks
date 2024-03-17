package strings;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class Sensors {
    private int id;
    private int battery;
    private double longitude;
    private double latitude;
    private double smokeVal;
    private double gasVal;
    private double thermalVal;
    private double uvVal;

    public Sensors(String input) {
        // Remove square brackets at the beginning and end
        input = input.substring(1, input.length() - 1);

        // Split by '},{'
        String[] substrings = input.split("\\},\\{");
        
     // Create a vector to store the substrings
        Vector<String> vectorSubstrings = new Vector<>();
        
     // Add curly braces to each substring
        for (String substring : substrings) {
            // If it's not the last substring, append }
            if (!substring.equals(substrings[substrings.length - 1])) {
                substring = "{" + substring + "}";
            }
            vectorSubstrings.add(substring);
        }
        
        // save the size of the vector

        int vectorsize;
        vectorsize = vectorSubstrings.size(); 
        
        List<String> resultList = new ArrayList<>();
        for (String substring : vectorSubstrings) {
            List<String> values = extractValuesFromString(substring);
            resultList.addAll(values);
        }
        
        if(vectorsize == 3) {       // if vectorsize is 3 it means that we dont have values for temp and uv 

            // Accessing values from the list
            this.id = Integer.parseInt(resultList.get(0));
            this.battery = Integer.parseInt(resultList.get(1));
            this.longitude = Double.parseDouble(resultList.get(2));
            this.latitude = Double.parseDouble(resultList.get(3));
            this.smokeVal = Double.parseDouble(resultList.get(5));
            this.gasVal = Double.parseDouble(resultList.get(7));
            this.thermalVal = -1000;
            this.uvVal = -1000;
            
            /*
            // Print the results
            System.out.println("ID: " + id);
            System.out.println("Battery: " + battery);
            System.out.println("Longitude: " + longitude);
            System.out.println("Latitude: " + latitude);
            System.out.println("Smoke Value: " + smokeVal);
            System.out.println("Gas Value: " + gasVal);
            System.out.println("Thermal Value: " + thermalVal);
            System.out.println("Uv Value: " + uvVal);
            
            */
          }
        
        if(vectorsize == 4) {              // if vectorsize is 4 it means that we dont have values either for temp either for uv 

            // Accessing values from the list
            this.id = Integer.parseInt(resultList.get(0));
            this.battery = Integer.parseInt(resultList.get(1));
            this.longitude = Double.parseDouble(resultList.get(2));
            this.latitude = Double.parseDouble(resultList.get(3));
            this.smokeVal = Double.parseDouble(resultList.get(5));
            this.gasVal = Double.parseDouble(resultList.get(7));
            this.thermalVal = 0;
            this.uvVal = 0;
            
            if(resultList.get(8).equals("Thermal Sensor")) {        // check which type of sensor is this
            	this.thermalVal = Double.parseDouble(resultList.get(9));
            	this.uvVal = -1000;
            }
            else if (resultList.get(8).equals("UV radiation Sensor")){    // check which type of sensor is this
            	this.uvVal = Double.parseDouble(resultList.get(9));
            	this.thermalVal = -1000;
            }
            
            /*
            // Print the results
            System.out.println("ID: " + id);
            System.out.println("Battery: " + battery);
            System.out.println("Longitude: " + longitude);
            System.out.println("Latitude: " + latitude);
            System.out.println("Smoke Value: " + smokeVal);
            System.out.println("Gas Value: " + gasVal);
            System.out.println("Thermal Value: " + thermalVal);
            System.out.println("Uv Value: " + uvVal);
            
            */
          }
        
        
        if(vectorsize == 5) {       // if vectorsize is 5 it means that we dont have values either for temp either for uv 


        // Accessing values from the list
        this.id = Integer.parseInt(resultList.get(0));
        this.battery = Integer.parseInt(resultList.get(1));
        this.longitude = Double.parseDouble(resultList.get(2));
        this.latitude = Double.parseDouble(resultList.get(3));
        this.smokeVal = Double.parseDouble(resultList.get(5));
        this.gasVal = Double.parseDouble(resultList.get(7));
        this.thermalVal = 0;
        this.uvVal = 0;
        
        if(resultList.get(8).equals("Thermal Sensor")) {        // check which type of sensor is this
        	this.thermalVal = Double.parseDouble(resultList.get(9));
        	this.uvVal = Double.parseDouble(resultList.get(11));
        }
        else if (resultList.get(8).equals("UV radiation Sensor")){      // check which type of sensor is this
        	this.uvVal = Double.parseDouble(resultList.get(9));
        	this.thermalVal = Double.parseDouble(resultList.get(11));
        }
        	
        /*
        // Print the results
        System.out.println("ID: " + id);
        System.out.println("Battery: " + battery);
        System.out.println("Longitude: " + longitude);
        System.out.println("Latitude: " + latitude);
        System.out.println("Smoke Value: " + smokeVal);
        System.out.println("Gas Value: " + gasVal);
        System.out.println("Thermal Value: " + thermalVal);
        System.out.println("Uv Value: " + uvVal);
        */
      }
        
        
       
    }

    // Getter methods
    public int getId() {
        return id;
    }

    public int getBattery() {
        return battery;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getSmokeVal() {
        return smokeVal;
    }

    public double getGasVal() {
        return gasVal;
    }

    public double getThermalVal() {
        return thermalVal;
    }

    public double getUvVal() {
        return uvVal;
    }

    // extract values from a substring and store them in a list
    private List<String> extractValuesFromString(String substring) {
        String[] keyValuePairs = substring.substring(1, substring.length() - 1).split(",");
        List<String> resultList = new ArrayList<>();
        for (String pair : keyValuePairs) {
            String[] keyValue = pair.split(":");
            String value = keyValue[1].trim().replaceAll("\"", "");
            resultList.add(value);
        }
        return resultList;
    }
}
