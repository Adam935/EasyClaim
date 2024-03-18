package com.example.easyclaim;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.easyclaim.database.DataRecorder;
import com.example.easyclaim.util.EmailConfig;
import com.example.easyclaim.util.EmailSender;
import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Report_page5 extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report5);

        // Create an instance of DataRecorder
        DataRecorder dataRecorder = new DataRecorder(this);
        // Retrieve the PDFView from the XML layout
        PDFView pdfView = findViewById(R.id.pdfView);
        Button buttonSignatureA = findViewById(R.id.buttonSignatureA);
        Button buttonSignatureB = findViewById(R.id.buttonSignatureB);
        Button buttonValidate = findViewById(R.id.buttonValidate);

        // Retrieve the path of the PDF file from DataRecorder
        String pdfPath = dataRecorder.getPDFFilePath();
        Log.d("Report_page5", "PDF Path: " + pdfPath);

        // Load the PDF into the PDFView from the retrieved path
        File pdfFile = new File(pdfPath);
        if (pdfFile.exists()) {
            pdfView.fromFile(pdfFile).load();
        } else {
            Log.e("Report_page5", "PDF file does not exist: " + pdfPath);
            dataRecorder.generatePDFFromData(); // Call the method here
            Log.v("Report_page5", "PDF generated successfully");
            pdfView.fromFile(pdfFile).load();
        }

        // Check if both signatures exist
        if (dataRecorder.doSignaturesExist()) {
            // If both signatures exist, show the validate button and set the PDFView height to 559dp
            buttonValidate.setVisibility(View.VISIBLE);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, convertDpToPx(559));
            pdfView.setLayoutParams(params);
        } else {
            // If not, hide the validate button and set the PDFView height to 657dp
            buttonValidate.setVisibility(View.GONE);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, convertDpToPx(657));
            pdfView.setLayoutParams(params);
        }

        // Set an OnClickListener for the validate button
        buttonSignatureA.setOnClickListener(v -> {
            Intent intent = new Intent(this, SignatureActivity.class);
            intent.putExtra("driverName", "Conducteur A");
            startActivity(intent);
            finish();
        });

        buttonSignatureB.setOnClickListener(v -> {
            Intent intent = new Intent(this, SignatureActivity.class);
            intent.putExtra("driverName", "Conducteur B");
            startActivity(intent);
            finish();
        });

        buttonValidate.setOnClickListener(v -> {

            dataRecorder.sendEmailWithPDF_old();
        });


    }
    // Method to convert dp to pixels
    private int convertDpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }
}