package com.dit.iot23;

public class Sensor {
    private String type;
    private boolean enabled;
    private float curval;
    private float minval;
    private float maxval;

    //Constructor (As simple as it gets)
    public Sensor(String type) {
        this.type=type;
        this.curval = 0;
        this.enabled = true;

    }

    public void ChangeType(String newtype) {
        this.type = newtype;
    }
    public void ChangeVal(float newval) {
        this.curval = newval;
    }
    public void ChangeMaxVal(float newmax) {
        this.maxval = newmax;
    }
    public void ChangeMinVal(float newmin) {
        this.minval = newmin;
    }
    public void ChangeStatus(boolean enable) {
        this.enabled = enable;
    }
    public float getval(){
        return curval;
    }
    public float getMaxval(){
        return maxval;
    }
    public float getMinval(){
        return minval;
    }
    public String getType(){
        return type;
    }
    public boolean getstatus(){
        return enabled;
    }

}
