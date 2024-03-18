package com.example.easyclaim;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.easyclaim.database.DataRecorder;
import com.example.easyclaim.util.FixedSignaturePad;

import java.io.File;

public class SignatureActivity extends AppCompatActivity {
    private FixedSignaturePad signaturePad;
    private DataRecorder dataRecorder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signature);

        dataRecorder = new DataRecorder(this);

        TextView textViewMessage = findViewById(R.id.textViewMessage);
        signaturePad = findViewById(R.id.signaturePad);
        Button buttonClear = findViewById(R.id.buttonClear);
        Button buttonValidate = findViewById(R.id.buttonValidate);

        String driverName = getIntent().getStringExtra("driverName");
        textViewMessage.setText(driverName + ", please sign to validate the report.");

        buttonClear.setOnClickListener(v -> signaturePad.clear());

        buttonValidate.setOnClickListener(v -> {
            if (signaturePad.isEmpty()) {
                Toast.makeText(this, "Please sign first", Toast.LENGTH_SHORT).show();
            } else {
                Bitmap signature = signaturePad.getTransparentSignatureBitmap();
                Log.e("SignatureActivity", "LE NOM DU MEC: " + driverName);
                String driverNameSuffix = driverName.equals("Conducteur A") ? "A" : "B";
                dataRecorder.saveSignatureImage("Conducteur " + driverNameSuffix + "_signature.png", signature);

                // Chemins vers les fichiers de signature
                String signatureAPath = dataRecorder.getDirectoryPath() + "/Conducteur A_signature.png";
                String signatureBPath = dataRecorder.getDirectoryPath() + "/Conducteur B_signature.png";

                // check if the signatures exist
                boolean signaturesExist = dataRecorder.doSignaturesExist();

                if (signaturesExist) {
                    // Add the signatures to the PDF
                    dataRecorder.addSignaturesToPDF(signatureAPath, signatureBPath);
                    // Generate the final PDF
                    finish();
                    Intent intent = new Intent(this, Report_page5.class);
                    startActivity(intent);
                } else {
                    // Affichez un message si une ou les deux signatures sont manquantes
                    Toast.makeText(this, "The signatures of both drivers are required.", Toast.LENGTH_LONG).show();
                }

                finish();
                Intent intent = new Intent(this, Report_page5.class);
                startActivity(intent);
            }
        });
    }
}