package com.dit.iot23;

import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.slider.Slider;

import java.util.ArrayList;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SensorAdapter extends RecyclerView.Adapter<SensorAdapter.Viewholder> {
    //After project switch to recycler view interface
    //See more at https://developer.android.com/develop/ui/views/layout/recyclerview
    //Several default functions have been overriden.
    ArrayList<Sensor> SensorsDisplay;

    public SensorAdapter(ArrayList<Sensor> sensorList) {
        SensorsDisplay = sensorList;
    }


    @NonNull
    @Override
    public SensorAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_sensor, parent, false);
        Viewholder viewholder = new Viewholder(view);
        return viewholder;
    }

    @Override
    public void onBindViewHolder(@NonNull SensorAdapter.Viewholder holder, int position) {
        //Binding the view with the sensor values. View is describbed bellow...
        Sensor cur_sensor = SensorsDisplay.get(position);
        holder.cur_sensor = cur_sensor;
        holder.stype.setText(cur_sensor.getType());
        holder.svalue.setValue(cur_sensor.getval());
        holder.position = holder.getAdapterPosition();
        Log.e("SENSTYPE",cur_sensor.getType());
        switch (cur_sensor.getType()){
            case "Smoke Sensor":
                holder.svalue.setValueFrom(0);
                holder.svalue.setValueTo(0.25F);
                break;
            case "Gas Sensor":
                holder.svalue.setValueFrom(0);
                holder.svalue.setValueTo(11.0F);
                break;
            case "UV radiation Sensor":
                holder.svalue.setValueFrom(0);
                holder.svalue.setValueTo(11.0F);
                break;
            case "Thermal Sensor":
                holder.svalue.setValueFrom(-5);
                holder.svalue.setValueTo(80F);
                break;
            default:
                break;
        }

    }
    @Override
    public int getItemCount() {
        return SensorsDisplay.size();
    }
    public static class Viewholder extends RecyclerView.ViewHolder{
        public int position;
        public Sensor cur_sensor;
        public View view;
        public TextView stype;
        public Slider svalue;
        public CheckBox senable;
        public Viewholder(View itemView) {
            super(itemView);
            view = itemView;
            stype = itemView.findViewById(R.id.stype);
            svalue = itemView.findViewById(R.id.svalue);
            senable = itemView.findViewById(R.id.senable);
            itemView.findViewById(R.id.senable).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Checkbox inverts status. Important to note that in the begging, the sensor is DISABLED
                    //until user enables it!! Otherwise, default status of checkbox will need to change asw.
                    if((cur_sensor.getType()!="Smoke Sensor") && (cur_sensor.getType()!="Gas Sensor")) {
                        cur_sensor.ChangeStatus(!cur_sensor.getstatus());
                    }
                    else {
                        senable.setChecked(true);
                    }
                }
            });

            svalue.addOnChangeListener(new Slider.OnChangeListener(){
                @Override
                public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                    cur_sensor.ChangeVal(value);
                }
            } );
        }
    }
}