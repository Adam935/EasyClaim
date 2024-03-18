package com.example.easyclaim;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.example.easyclaim.view.SpinnerHelper;
import com.example.easyclaim.database.DataRecorder;

public class Report_page2 extends AppCompatActivity {
    private DataRecorder dataRecorder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report2); // Assurez-vous que c'est le bon layout
        SpinnerHelper.setupSpinners(this);

        Button Continu_the_report = findViewById(R.id.continueButton);
        Continu_the_report.setOnClickListener(v -> {
            // Initialize DataRecorder
            dataRecorder = new DataRecorder(this);
            dataRecorder.clearData("report_page2_test.txt"); // This line is missing in the original code
            // Save the report
            dataRecorder.saveReportPage2(this);
            Intent intent = new Intent(Report_page2.this, Report_page3.class);
            startActivity(intent);
        });



    }


}