package com.example.easyclaim;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.easyclaim.database.DataRecorder;
import com.example.easyclaim.util.NomenclatureDictionary;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Report_test extends AppCompatActivity {
    private DataRecorder dataRecorder;
    private NomenclatureDictionary nomenclatureDictionary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report);

        // Initialize DataRecorder
        dataRecorder = new DataRecorder(this);

        // Initialize NomenclatureDictionary
        nomenclatureDictionary = new NomenclatureDictionary();

// Initialize TextViews
        TextView insuranceCompanyTextView = findViewById(R.id.insuranceCompanyTextView);
        insuranceCompanyTextView.setTag("Nom de l'assurance");
        TextView licensePlateNumberTextView = findViewById(R.id.licensePlateNumberTextView);
        licensePlateNumberTextView.setTag("Plaque d'immatriculation");
        TextView insuranceNumberTextView = findViewById(R.id.insuranceNumberTextView);
        insuranceNumberTextView.setTag("Numero d'assurer");
        TextView insuranceGreenCardNumberTextView = findViewById(R.id.insuranceGreenCardNumberTextView);
        insuranceGreenCardNumberTextView.setTag("Vitesse du choc");
        TextView insuranceGreenCardStartDateTextView = findViewById(R.id.insuranceGreenCardStartDateTextView);
        insuranceGreenCardStartDateTextView.setTag("Adresse mail de l'assureur");
        TextView insuranceGreenCardEndDateTextView = findViewById(R.id.insuranceGreenCardEndDateTextView);
        insuranceGreenCardEndDateTextView.setTag("DJ");
        TextView vehicleDamageLocationTextView = findViewById(R.id.vehicleDamageLocationTextView);
        vehicleDamageLocationTextView.setTag("Localisation du choc sur le vehicule");
        TextView ownerFirstNameTextView = findViewById(R.id.ownerFirstNameTextView);
        ownerFirstNameTextView.setTag("Prenom");
        TextView ownerLastNameTextView = findViewById(R.id.ownerLastNameTextView);
        ownerLastNameTextView.setTag("Nom");

// Liste des TextViews à remplir
        TextView[] textViews = {
                insuranceCompanyTextView,
                licensePlateNumberTextView,
                insuranceNumberTextView,
                insuranceGreenCardNumberTextView,
                insuranceGreenCardStartDateTextView,
                vehicleDamageLocationTextView,
                ownerFirstNameTextView,
                ownerLastNameTextView,
                insuranceGreenCardEndDateTextView
        };


        // Lire les données depuis le fichier et les afficher dans les TextViews
        dataRecorder.clearData("Easyclaim_data_reformat.txt");

        dataRecorder.Reformate_data_from_device("data_for_easyclaim_for_test.txt", textViews, nomenclatureDictionary);

// Initialize EditTexts
        EditText insuranceCompanyEditText = findViewById(R.id.insuranceCompanyEditText);
        EditText licensePlateNumberEditText = findViewById(R.id.licensePlateNumberEditText);
        EditText insuranceNumberEditText = findViewById(R.id.insuranceNumberEditText);
        EditText insuranceGreenCardNumberEditText = findViewById(R.id.insuranceGreenCardNumberEditText);
        EditText insuranceGreenCardStartDateEditText = findViewById(R.id.insuranceGreenCardStartDateEditText);
        EditText vehicleDamageLocationEditText = findViewById(R.id.vehicleDamageLocationEditText);
        EditText ownerFirstNameEditText = findViewById(R.id.ownerFirstNameEditText);
        EditText ownerLastNameEditText = findViewById(R.id.ownerLastNameEditText);

        EditText[] editTexts = {
                insuranceCompanyEditText,
                licensePlateNumberEditText,
                insuranceNumberEditText,
                insuranceGreenCardNumberEditText,
                insuranceGreenCardStartDateEditText,
                vehicleDamageLocationEditText,
                ownerFirstNameEditText,
                ownerLastNameEditText
        };

        Button editButton = findViewById(R.id.editButton);
        editButton.setOnClickListener(new View.OnClickListener() {
            boolean isEditing = false;

            @Override
            public void onClick(View v) {
                if (!isEditing) {
                    // Convert TextViews to EditTexts
                    for (int i = 0; i < textViews.length; i++) {
                        if (i < editTexts.length && !textViews[i].getTag().equals("DJ")) {
                            String[] parts = textViews[i].getText().toString().split(" : ");
                            String value = parts.length > 1 ? parts[1] : "";
                            editTexts[i].setText(value);
                            editTexts[i].setVisibility(View.VISIBLE);
                        }
                        if (!textViews[i].getTag().equals("DJ")) {
                            textViews[i].setVisibility(View.GONE);
                        }
                    }
                    editButton.setText("Save");
                } else {
                    // Convert EditTexts back to TextViews
                    for (int i = 0; i < textViews.length; i++) {
                        if (i < editTexts.length && !textViews[i].getTag().equals("DJ")) {
                            String label = textViews[i].getTag().toString();
                            String value = editTexts[i].getText().toString();
                            textViews[i].setText(label + " : " + value);
                            editTexts[i].setVisibility(View.GONE);
                        }
                        if (!textViews[i].getTag().equals("DJ")) {
                            textViews[i].setVisibility(View.VISIBLE);
                        }
                    }
                    editButton.setText("Edit");

                    // Clear and rewrite the data in the file
                    dataRecorder.clearData("Easyclaim_data_reformat.txt");
                    for (TextView textView : textViews) {
                        String updatedText = textView.getText().toString();
                        dataRecorder.saveDataToFile("Easyclaim_data_reformat.txt", updatedText);
                    }
                }
                isEditing = !isEditing;
            }
        });




        Button completeReportButton = findViewById(R.id.completeReportButton);

        completeReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Report_test.this, Report_test2.class);
                startActivity(intent);
            }
        });
    }

}
