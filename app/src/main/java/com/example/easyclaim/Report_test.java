package com.example.easyclaim;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
        //TextView ownerAddressTextView = findViewById(R.id.ownerAddressTextView);
        //ownerAddressTextView.setTag("Adresse du proprietaire");
        //TextView ownerPhoneNumberTextView = findViewById(R.id.ownerPhoneNumberTextView);
        //ownerPhoneNumberTextView.setTag("numero de telephone");

        // Liste des TextViews à remplir
        TextView[] textViews = {
                insuranceCompanyTextView,
                licensePlateNumberTextView,
                insuranceNumberTextView,
                insuranceGreenCardNumberTextView,
                insuranceGreenCardStartDateTextView,
                insuranceGreenCardEndDateTextView,
                vehicleDamageLocationTextView,
                ownerFirstNameTextView,
                ownerLastNameTextView,
                //ownerAddressTextView,
                //ownerPhoneNumberTextView
        };


        // Lire les données depuis le fichier et les afficher dans les TextViews
        dataRecorder.clearData("Easyclaim_data_reformat.txt");

        dataRecorder.Reformate_data_from_device("data_for_easyclaim_for_test.txt", textViews, nomenclatureDictionary);


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
