package database;

import java.sql.*;

public class ConnectionSql implements Runnable{
    private Connection connection;

    private String date;
    private String time;
    private int iot1_id;
    private double iot1_smoke;
    private double iot1_gas;
    private double iot1_temp;
    private double iot1_uv;
    private double iot1_lat;
    private double iot1_lng;
    private int iot1_danger;
    private int iot2_id;
    private double iot2_smoke;
    private double iot2_gas;
    private double iot2_temp;
    private double iot2_uv;
    private double iot2_lat;
    private double iot2_lng;
    private int iot2_danger;

    public void setValues(String date, String time, int iot1_id, double iot1_smoke, double iot1_gas, double iot1_temp, double iot1_uv, double iot1_lat, double iot1_lng, int iot1_danger, int iot2_id, double iot2_smoke, double iot2_gas, double iot2_temp, double iot2_uv, double iot2_lat, double iot2_lng, int iot2_danger) {
        this.date = date;
        this.time = time;
        this.iot1_id = iot1_id;
        this.iot1_smoke = iot1_smoke;
        this.iot1_gas = iot1_gas;
        this.iot1_temp = iot1_temp;
        this.iot1_uv = iot1_uv;
        this.iot1_lat = iot1_lat;
        this.iot1_lng = iot1_lng;
        this.iot1_danger = iot1_danger;
        this.iot2_id = iot2_id;
        this.iot2_smoke = iot2_smoke;
        this.iot2_gas = iot2_gas;
        this.iot2_temp = iot2_temp;
        this.iot2_uv = iot2_uv;
        this.iot2_lat = iot2_lat;
        this.iot2_lng = iot2_lng;
        this.iot2_danger = iot2_danger;
    }
    
    public void connectToDatabase(String url, String user, String password) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Connection is successful to the database" + url);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
    
    public void run() {
        try {            
            String query = "INSERT INTO IOT_Android(date, time, iot1_id, iot1_smoke, iot1_gas, iot1_temp, iot1_uv, iot1_lat, iot1_lng, iot1_danger, iot2_id, iot2_smoke, iot2_gas, iot2_temp, iot2_uv, iot2_lat, iot2_lng, iot2_danger) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?, ?, ?, ?, ?, ?, ?)";
            
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, date);
            preparedStatement.setString(2, time);
            preparedStatement.setInt(3, iot1_id);
            if (iot1_smoke != -1000) {              //if a sensor value is not available because the sensor is offline or not connected, NULL is added to the database
                preparedStatement.setDouble(4, iot1_smoke);
            } else {
                preparedStatement.setNull(4, Types.DOUBLE);
            }
            if (iot1_gas != -1000) {
                preparedStatement.setDouble(5, iot1_gas);
            } else {
                preparedStatement.setNull(5, Types.DOUBLE);
            }
            if (iot1_temp != -1000) {
                preparedStatement.setDouble(6, iot1_temp);
            } else {
                preparedStatement.setNull(6, Types.DOUBLE);
            }
            if (iot1_uv != -1000) {
                preparedStatement.setDouble(7, iot1_uv);
            } else {
                preparedStatement.setNull(7, Types.DOUBLE);
            }
            preparedStatement.setDouble(8, iot1_lat);
            preparedStatement.setDouble(9, iot1_lng);
            if (iot1_danger != -1) {                //if the danger level is not available because the sensor is offline or not connected, NULL is added to the database
                preparedStatement.setInt(10, iot1_danger);
            } else {
                preparedStatement.setNull(10, Types.INTEGER);
            }
            preparedStatement.setInt(11, iot2_id);
            if (iot2_smoke != -1000) {
                preparedStatement.setDouble(12, iot2_smoke);
            } else {
                preparedStatement.setNull(12, Types.DOUBLE);
            }
            if (iot2_gas != -1000) {
                preparedStatement.setDouble(13, iot2_gas);
            } else {
                preparedStatement.setNull(13, Types.DOUBLE);
            }
            if (iot2_temp != -1000) {
                preparedStatement.setDouble(14, iot2_temp);
            } else {
                preparedStatement.setNull(14, Types.DOUBLE);
            }
            if (iot2_uv != -1000) {
                preparedStatement.setDouble(15, iot2_uv);
            } else {
                preparedStatement.setNull(15, Types.DOUBLE);
            }
            preparedStatement.setDouble(16, iot2_lat);
            preparedStatement.setDouble(17, iot2_lng);
            if (iot2_danger != -1) {
                preparedStatement.setInt(18, iot2_danger);
            } else {
                preparedStatement.setNull(18, Types.INTEGER);
            }
                        
            preparedStatement.executeUpdate();          //execute the query

            System.out.println("Data has been inserted into the database");     //print a message to the console to indicate that the operation is successful
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void closeConnection() {
        try {
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
