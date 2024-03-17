package danger;

public class Danger{
    
    private double smokeThreshold = 0.14;
    private double gasThreshold = 9.15;
    private double temperatureThreshold = 50.0;
    private double UVThreshold = 6.0;

    private double smokeSensorValue;
    private double gasSensorValue;
    private double temperatureSensorValue;
    private double UVSensorValue;

    public Danger(double smokeSensorValue , double gasSensorValue , double temperatureSensorValue, double UVSensorValue){
        this.smokeSensorValue = smokeSensorValue;
        this.gasSensorValue = gasSensorValue;
        this.temperatureSensorValue = temperatureSensorValue;
        this.UVSensorValue = UVSensorValue;
    }

    /*function to determine the danger level*/
    public int getDangerLevel(){
        
        boolean SmokeDanger = smokeSensorValue > smokeThreshold;
        boolean GasDanger = gasSensorValue > gasThreshold;
        boolean TemperatureDanger = temperatureSensorValue >= temperatureThreshold;
        boolean UVDanger =UVSensorValue >=UVThreshold;
        
        
        if (SmokeDanger && GasDanger) {
            return 2;
        } else if   ((!SmokeDanger && !GasDanger) && (TemperatureDanger && UVDanger)) {
            return 1;
        } else if (GasDanger) {
            return 2;
        } else if (SmokeDanger && GasDanger && TemperatureDanger && UVDanger) {
            return 2;
        } else {
            return 0;
        }
    }

}
