package com.example.easyclaim;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import androidx.appcompat.app.AppCompatActivity;
import com.example.easyclaim.database.DataRecorder;
import com.example.easyclaim.util.NomenclatureDictionary;

import java.util.Map;

public class Report_page4 extends AppCompatActivity {

    private DataRecorder dataRecorder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report4);
        // Initialiser le DataRecorder
        dataRecorder = new DataRecorder(this);
        // Initialiser le bouton
        Button buttonEndReport = findViewById(R.id.buttonEndReport);

        // Ajouter un OnClickListener au bouton
        buttonEndReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Appeler la méthode pour enregistrer les données dans le fichier
                dataRecorder.clearData("Circonstances.txt");
                saveCircumstancesToFile();
                // Clear all data that shouldn't be here
                dataRecorder.deleteFile("Conducteur A_signature.png");
                dataRecorder.deleteFile("Conducteur B_signature.png");
                dataRecorder.deleteFile("constat_amiable_modified.pdf");
                dataRecorder.deleteFile("constat_amiable.pdf");
                Intent intent = new Intent(Report_page4.this, Report_page5.class);
                startActivity(intent);
            }
        });
    }

    // Méthode pour enregistrer les circonstances dans le fichier "Circonstances.txt"
    private void saveCircumstancesToFile() {
        NomenclatureDictionary nomenclatureDictionary = new NomenclatureDictionary();
        Map<String, String> circumstancesNomenclature = nomenclatureDictionary.getCircumstancesNomenclature();

        StringBuilder circumstancesData = new StringBuilder();
        for (int i = 1; i <= 6; i++) {
            int checkBoxIdA = getResources().getIdentifier("checkBoxVehicleA" + i, "id", getPackageName());
            CheckBox checkBoxVehicleA = findViewById(checkBoxIdA);
            if (checkBoxVehicleA.isChecked()) {
                String circumstanceDescriptionA = circumstancesNomenclature.get("A" + i); // Assuming "A1", "A2",... are the keys for vehicle A circumstances
                circumstancesData.append("Véhicule A - ").append(circumstanceDescriptionA).append("\n");
            }

            int checkBoxIdB = getResources().getIdentifier("checkBoxVehicleB" + i, "id", getPackageName());
            CheckBox checkBoxVehicleB = findViewById(checkBoxIdB);
            if (checkBoxVehicleB.isChecked()) {
                String circumstanceDescriptionB = circumstancesNomenclature.get("B" + i); // Assuming "B1", "B2",... are the keys for vehicle B circumstances
                circumstancesData.append("Véhicule B - ").append(circumstanceDescriptionB).append("\n");
            }
        }

        dataRecorder.saveDataToFile("Circonstances.txt", circumstancesData.toString());
    }

}
