package strings;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class AndroidData {
    private int id;
    private double longitude;
    private double latitude;

    public AndroidData(String input) {
        // Remove square brackets at the beginning and end - the message has square brackets as we get it from android
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
         
        // inserting all the substrings from the vectoring into a list called resultList
        List<String> resultList = new ArrayList<>();
        for (String substring : vectorSubstrings) {
            List<String> values = extractValuesFromString(substring);
            resultList.addAll(values);
        }
        
        // we receive the values from the resultList. id is on the 1st position, longitude is in the 2nd position and latitude is in the 3rd position 

        this.id = Integer.parseInt(resultList.get(0));
        this.longitude = Double.parseDouble(resultList.get(1));
        this.latitude = Double.parseDouble(resultList.get(2));
        
    }

    // Getter methods
    public int getId() {
        return id;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
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
