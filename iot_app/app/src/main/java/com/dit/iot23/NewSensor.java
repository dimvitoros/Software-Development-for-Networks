package com.dit.iot23;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class NewSensor extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newsensor);
        //Create sensor based on selected button
        Button thermalbtn = findViewById(R.id.newthermal);
        Button uvbtn = findViewById((R.id.newuv));
        thermalbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("key","thermal");
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        uvbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("key","uv");
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

}
