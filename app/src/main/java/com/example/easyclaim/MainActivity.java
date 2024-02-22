package com.example.easyclaim;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button Connection_to_the_device = findViewById(R.id.Connection_to_the_device);
        Button Configure_the_device = findViewById(R.id.Configure_the_device);
        Button How_to_use_EasyClaim = findViewById(R.id.How_to_use_EasyClaim);


        Connection_to_the_device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ConnectionDeviceActivity.class);
                startActivity(intent);
            }
        });

        How_to_use_EasyClaim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        Configure_the_device.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, Configure_device.class);
            startActivity(intent);
        }
    });




}


}