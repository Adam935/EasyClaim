package com.example.easyclaim;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.example.easyclaim.database.DataRecorder;

public class Report_page3 extends AppCompatActivity {

    private EditText editNomAssurance, editNumeroContrat, editImmatriculation, editMailAssureur, editMailProprietaire;
    private Button buttonContinuer;
    private DataRecorder dataRecorder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report3);

        // Initialisation des vues et du DataRecorder
        editNomAssurance = findViewById(R.id.editNomAssurance);
        editNumeroContrat = findViewById(R.id.editNumeroContrat);
        editImmatriculation = findViewById(R.id.editImmatriculation);
        editMailAssureur = findViewById(R.id.editMailAssureur);
        editMailProprietaire = findViewById(R.id.editMailProprietaire);
        buttonContinuer = findViewById(R.id.buttonContinuer);
        dataRecorder = new DataRecorder(this);

        // Ajout d'un OnClickListener au bouton Continuer
        buttonContinuer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Récupération des informations saisies par l'utilisateur
                String nomAssurance = editNomAssurance.getText().toString();
                String numeroAssurance = editNumeroContrat.getText().toString();
                String immatriculation = editImmatriculation.getText().toString();
                String mailAssureur = editMailAssureur.getText().toString();
                String mailProprietaire = editMailProprietaire.getText().toString();

                // Concaténation des données dans une seule chaîne
                StringBuilder data = new StringBuilder();
                data.append("Name of insurance company: ").append(nomAssurance).append("\n");
                data.append("Insurance number: ").append(numeroAssurance).append("\n");
                data.append("Immatriculation : ").append(immatriculation).append("\n");
                data.append("Insurer's e-mail address: ").append(mailAssureur).append("\n");
                data.append("E-mail address of the owner of B's car: ").append(mailProprietaire).append("\n");

                // Enregistrement des données dans un fichier
                dataRecorder.clearData("report_page3_data.txt");
                dataRecorder.saveDataToFile("report_page3_data.txt", data.toString());
                Intent intent = new Intent(Report_page3.this, Report_page4.class);
                startActivity(intent);
            }
        });
    }
}
