package com.example.easyclaim.database;

import android.app.Activity;
import android.util.Log;

import com.example.easyclaim.R;
import com.example.easyclaim.util.AppPermission;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

// ENREGISTE LES DONNÉES DANS UN FICHIER TEXTE DANS LE DOSSIER INTERNE DE L'APPLICATION
// EXAMPLE /storage/emulated/0/Android/data/com.example.testblueapp/files/Easyclaim/data_for_easyclaim.txt
public class DataRecorder {
    private final Activity activity;
    private final AppPermission appPermission; // Adding AppPermission instance

    public DataRecorder(Activity activity) {
        this.activity = activity;
        this.appPermission = new AppPermission(activity); // Initializing AppPermission
        createDirectory();
    }
    private String getDirectoryPath() {
        // Utilisez getExternalFilesDir pour supporter Android 10+ en respectant la politique de stockage
        return this.activity.getExternalFilesDir(null) + "/Easyclaim";
    }
    public void createDirectory() {
        if (appPermission.isExternalStorageWritable()) {
            if (appPermission.checkStoragePermission()) {
                File directory = new File(getDirectoryPath());
                if (!directory.exists()) {
                    if (directory.mkdirs()) {
                        Log.d("DataRecorder", "Directory created successfully. init");
                    } else {
                        Log.e("DataRecorder", "Failed to create directory init");
                    }
                }
            } else {
                appPermission.requestStoragePermission();
            }
        } else {
            Log.e("DataRecorder", "External storage not writable.");
        }
    }

    public void saveDataToFile(String fileName, String data) {
        createDirectory();

        File file = new File(getDirectoryPath(), fileName);

        try {
            FileOutputStream fos = new FileOutputStream(file, true);
            fos.write(data.getBytes());
            fos.write("\n".getBytes());
            fos.close();

            Log.i("DataRecorder", "Data saved to file: " + file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void clearData(String fileName) {
        File file = new File(getDirectoryPath(), fileName);

        if (file.exists()) {
            file.delete();
            Log.i("DataRecorder", "Data file deleted.");
        }
    }

    public void saveReportPage2(Activity activity) {
        try {
            // Assuming EditText and Spinner IDs from the layout file report2.xml
            Spinner daySpinner = activity.findViewById(R.id.daySpinner);
            Spinner monthSpinner = activity.findViewById(R.id.monthSpinner);
            Spinner yearSpinner = activity.findViewById(R.id.yearSpinner);
            Spinner hourSpinner = activity.findViewById(R.id.hourSpinner);
            Spinner minuteSpinner = activity.findViewById(R.id.minuteSpinner);
            EditText exactLocationEditText = activity.findViewById(R.id.exactLocationEditText);
            RadioGroup injuryRadioGroup = activity.findViewById(R.id.injuryRadioGroup);
            RadioGroup damageRadioGroup = activity.findViewById(R.id.damageRadioGroup);

            // Retrieve the selected values
            String day = daySpinner.getSelectedItem().toString();
            String month = monthSpinner.getSelectedItem().toString();
            String year = yearSpinner.getSelectedItem().toString();
            String hour = hourSpinner.getSelectedItem().toString();
            String minute = minuteSpinner.getSelectedItem().toString();
            String exactLocation = exactLocationEditText.getText().toString();
            int selectedInjuryId = injuryRadioGroup.getCheckedRadioButtonId();
            int selectedDamageId = damageRadioGroup.getCheckedRadioButtonId();

            String injury = selectedInjuryId == R.id.injuryYes ? "yes" : "no";
            String damage = selectedDamageId == R.id.damageYes ? "yes" : "no";

            // Use StringBuilder to create the string to save
            String dataBuilder = "Accident_date: " + day + "/" + month + "/" + year + "\n" +
                    "Hour_accident: " + hour + "h" + minute + "\n" +
                    "Exact Location: " + exactLocation + "\n" +
                    "Injury: " + injury + "\n" +
                    "Damage: " + damage + "\n";

            // Save the string to the file
            saveDataToFile("report_page2_test.txt", dataBuilder);
            Log.d("DataRecorder", "Report page 2 data saved.");
        } catch (Exception e) {
            Log.e("DataRecorder", "Error saving report page 2", e);
        }
    }

    public void Reformate_data_from_device(String fileName, TextView[] textViews) {
        Log.d("DataRecorder", "Reading data from file: " + fileName);
        File file = new File(getDirectoryPath(), fileName);

        // Créer un HashSet pour suivre les TextViews mis à jour
        Set<String> updatedTextViews = new HashSet<>();

        try {
            Log.d("DataRecorder", "File path: " + file.getAbsolutePath());
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            Log.d("DataRecorder", "Reading lines from file");
            while ((line = br.readLine()) != null) {
                Log.d("DataRecorder", "Line: " + line);
                String[] parts = line.split(": ");
                String label = parts[0];
                String value = parts[1];
                Log.d("DataRecorder", "Label: " + label + ", Value: " + value);

                // Parcours de tous les TextViews
                for (TextView textView : textViews) {
                    String currentText = textView.getText().toString();
                    Log.i("DataRecorder", "Tag: " + textView.getTag() + ", Label: " + label);
                    Log.w("DataRecorder", "Current text: " + currentText);
                    // Vérification si le tag correspond à la nomenclature actuelle
                    if (currentText.startsWith(label) && !updatedTextViews.contains(textView.getTag())) {
                        // Récupération du texte actuel du TextView
                        Log.e("DataRecorder", "Current text: " + currentText);
                        // Mise à jour du texte du TextView
                        String updatedText = textView.getTag() + " : " + value;
                        textView.setText(updatedText);
                        Log.v("DataRecorder", "Updated text: " + updatedText);
                        // Ajouter le tag du TextView au HashSet
                        updatedTextViews.add((String) textView.getTag());

                        // Enregistrer les données dans le fichier
                        saveDataToFile("Easyclaim_data_reformat.txt", updatedText);
                        break;
                    }
                }
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




}





/*
 public class DataRecorder {
 private Context context;

 public DataRecorder(Context context) {
 this.context = context;
 }
 // Méthode pour enregistrer les données dans un fichier texte
 public static void saveDataToFile(String data) {
 // Vérifier si le stockage externe est disponible en écriture
 String state = Environment.getExternalStorageState();
 if (Environment.MEDIA_MOUNTED.equals(state)) {
 // Répertoire où le fichier sera enregistré
 File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
 if (!dir.exists()) {
 dir.mkdirs(); // Créer le répertoire s'il n'existe pas
 }

 // Nom du fichier
 File file = new File(dir, "data_for_esayclaim.txt");

 try {
 // Vérifier si le fichier existe déjà
 if (!file.exists()) {
 // Créer le fichier s'il n'existe pas
 file.createNewFile();
 }

 // Ouvrir un flux de sortie vers le fichier
 FileOutputStream fos = new FileOutputStream(file, true);
 fos.write(data.getBytes());
 fos.write("\n".getBytes()); // Ajouter un saut de ligne pour séparer les données
 fos.close(); // Fermer le flux de sortie

 // Afficher un message de succès
 Log.i("DataRecorder", "Data saved to file: " + file.getAbsolutePath());
 } catch (IOException e) {
 e.printStackTrace();
 }

 } else {
 Log.e("DataRecorder", "External storage not available for writing.");
 }
 }


 // Méthode pour effacer les données enregistrées
 public void clearData() {
 // Vérifier si le stockage externe est disponible en écriture
 String state = Environment.getExternalStorageState();
 if (Environment.MEDIA_MOUNTED.equals(state)) {
 // Répertoire où le fichier est enregistré
 File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);

 // Nom du fichier
 File file = new File(dir, "data_for_esayclaim.txt");
 if (file.exists()) {
 file.delete(); // Supprimer le fichier
 Log.i("DataRecorder", "Data file deleted.");
 }
 } else {
 Log.e("DataRecorder", "External storage not available for writing.");
 }
 }

 }


 */