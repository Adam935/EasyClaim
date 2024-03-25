package com.example.easyclaim.database;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.example.easyclaim.R;
import com.example.easyclaim.util.AppPermission;
import com.example.easyclaim.util.EmailSender;
import com.example.easyclaim.util.NomenclatureDictionary;
import com.example.easyclaim.util.PDFGenerator;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    public String getDirectoryPath() {
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
    public String readFile(String fileName) {
        File file = new File(getDirectoryPath(), fileName);
        StringBuilder content = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                content.append(line).append("\n");
            }

            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return content.toString();
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
            if (minute.length() == 1) {
                minute = "0" + minute;
            }
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

    public void Reformate_data_from_device(String fileName, TextView[] textViews, NomenclatureDictionary nomenclatureDictionary) {
        Log.d("DataRecorder", "Reading data from file: " + fileName);
        File file = new File(getDirectoryPath(), fileName);

        // Create a HashSet to track updated TextViews
        Set<String> updatedTextViews = new HashSet<>();

        String emailStart = "";
        String emailEnd = "";
        String ChocSpeed = "";

        try {
            Log.d("DataRecorder", "File path: " + file.getAbsolutePath());
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            Log.d("DataRecorder", "Reading lines from file");
            // Retrieve the nomenclature dictionary
            Map<String, String> nomenclature = nomenclatureDictionary.getNomenclature();
            while ((line = br.readLine()) != null) {
                Log.d("DataRecorder", "Line: " + line);
                String[] parts = line.split(": ");
                String label = parts[0];
                String value = parts[1];
                Log.d("DataRecorder", "Label: " + label + ", Value: " + value);

                // Loop through all TextViews
                for (TextView textView : textViews) {
                    String currentName = activity.getResources().getResourceEntryName(textView.getId());
                    if (nomenclature.containsKey(label) && textView.getTag().equals(nomenclature.get(label))) {
                        if (currentName.equals("insuranceGreenCardStartDateTextView")) {
                            emailStart = value;
                            Log.d("test", "Email start: " + emailStart);
                        } else if (currentName.equals("insuranceGreenCardEndDateTextView")) {
                            emailEnd = value;
                            Log.e("test", "Email end: " + emailEnd);
                        } else if (currentName.equals("insuranceGreenCardNumberTextView")) {
                            ChocSpeed = value;
                            Log.e("test", "Choc speed: " + ChocSpeed);
                        } else if (!updatedTextViews.contains(textView.getTag())) {
                            String updatedText = textView.getTag() + " : " + value;
                            textView.setText(updatedText);
                            Log.v("test", "Updated text: " + updatedText);
                            updatedTextViews.add((String) textView.getTag());
                            saveDataToFile("Easyclaim_data_reformat.txt", updatedText);
                        }
                    }
                }
            }

            if (!emailStart.isEmpty() && !emailEnd.isEmpty()) {
                Log.v("test", "Email start: " + emailStart + ", Email end: " + emailEnd);
                String email = emailStart + "@" + emailEnd;
                Log.w("test", "Email: " + email);
                // Retrieve the nomenclature dictionary
                Map<String, String> nomenclature_bis = nomenclatureDictionary.getNomenclatureBis();
                for (TextView textView : textViews) {
                    String currentName = activity.getResources().getResourceEntryName(textView.getId());
                    if (currentName.equals(nomenclature_bis.get("Insurer's e-mail address")) && !updatedTextViews.contains(textView.getTag())) {
                        String updatedText = textView.getTag() + " : " + email;
                        textView.setText(updatedText);
                        Log.v("DataRecorder", "Updated text: " + updatedText);
                        updatedTextViews.add((String) textView.getTag());
                        saveDataToFile("Easyclaim_data_reformat.txt", updatedText);
                    } else if (currentName.equals(nomenclature_bis.get("Shock speed")) && !updatedTextViews.contains(textView.getTag())) {
                        String updatedText = textView.getTag() + " : " + ChocSpeed + " km/h";
                        textView.setText(updatedText);
                        Log.v("euh", "Updated text: " + updatedText);
                        updatedTextViews.add((String) textView.getTag());
                        saveDataToFile("Easyclaim_data_reformat.txt", updatedText);
                    }
                }
            }

            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveSignatureImage(String fileName, Bitmap signatureImage) {

            // Nom du fichier avec un horodatage pour le rendre unique
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            //String fileName = "signature_" + timeStamp + ".png";
            File file = new File(getDirectoryPath(), fileName);

            try {
                // Créer le fichier
                if (!file.exists()) {
                    file.createNewFile();
                }

                // Ouvrir un flux de sortie vers le fichier
                FileOutputStream fos = new FileOutputStream(file);
                // Enregistrer l'image de signature dans le fichier
                signatureImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.flush();
                fos.close(); // Fermer le flux de sortie

                // Afficher un message de succès avec le chemin du fichier
                Log.i("DataRecorder", "Signature image saved to file: " + file.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


    // Méthode pour générer le PDF à partir des données collectées
    public void generatePDFFromData() {
        // Création d'une instance de PDFGenerator
        PDFGenerator pdfGenerator = new PDFGenerator();
        Log.v("testoma", "PDFGenerator instance created.");
        // Chemin du fichier où le PDF sera enregistré
        String filePath = getDirectoryPath() + "/constat_amiable.pdf";

        // Création d'une Map pour stocker toutes les données collectées
        Map<String, String> allData = new HashMap<>();

        // Ajout des données de chaque fichier à la Map
        allData.put("Information Driver A : ", readFile("Easyclaim_data_reformat.txt"));
        allData.put("Information about the accident : ", readFile("report_page2_test.txt"));
        allData.put("Information Driver B : ", readFile("report_page3_data.txt"));
        allData.put("Circonstances of the accident : ", readFile("Circonstances.txt"));

        // Génération du PDF avec les données collectées
        pdfGenerator.generatePDF(allData, filePath);
    }


    public boolean doSignaturesExist() {
        // Chemins vers les fichiers de signature
        String signatureAPath = getDirectoryPath() + "/Conducteur A_signature.png";
        String signatureBPath = getDirectoryPath() + "/Conducteur B_signature.png";
        File signatureAFile = new File(signatureAPath);
        File signatureBFile = new File(signatureBPath);
        return signatureAFile.exists() && signatureBFile.exists();

    }
    public String getPDFFilePath() {

        boolean signaturesExist = doSignaturesExist();

        // Check if the signatures exist
        if (signaturesExist) {
            return getDirectoryPath() + "/constat_amiable_signed.pdf";
        } else {
            // Chemin du fichier où le PDF sera enregistré
            return getDirectoryPath() + "/constat_amiable.pdf";
        }
    }


    // Méthode pour fusionner les deux PDF en un seul fichier
    public void mergePDFs() {
        try {
            // Créer un nouveau document
            Document document = new Document();
            // Créer un PdfCopy
            PdfCopy copy = new PdfCopy(document, new FileOutputStream(getDirectoryPath() + "/amicable observation.pdf"));
            // Ouvrir le document
            document.open();
            // Liste des fichiers PDF à fusionner
            String[] files = {getDirectoryPath() + "/constat_amiable.pdf", getDirectoryPath() + "/signature_deux_partie.pdf"};
            // Parcourir la liste des fichiers
            for (String file : files) {
                // Créer un PdfReader
                PdfReader reader = new PdfReader(file);
                // Parcourir les pages du fichier PDF
                for (int i = 1; i <= reader.getNumberOfPages(); i++) {
                    // Ajouter la page au PdfCopy
                    copy.addPage(copy.getImportedPage(reader, i));
                }
                // Fermer le PdfReader
                reader.close();
            }
            // Fermer le document
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void addSignaturesToPDF(String signatureAPath, String signatureBPath) {
        try {
            String originalPdfPath = getDirectoryPath() + "/constat_amiable.pdf";
            PdfReader reader = new PdfReader(originalPdfPath);
            int n = reader.getNumberOfPages();
            String outputPdfPath = getDirectoryPath() + "/constat_amiable_signed.pdf";
            PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(outputPdfPath));

            PdfContentByte overContent = stamper.getOverContent(n);

            // Charger les images des signatures
            Image sigA = Image.getInstance(signatureAPath);
            Image sigB = Image.getInstance(signatureBPath);

            // Calcul de l'échelle pour les signatures
            float scalePercent = ((reader.getPageSize(n).getHeight() * 0.1f) / sigA.getHeight() * 100) * 1.30f;
            sigA.scalePercent(scalePercent);
            sigB.scalePercent(scalePercent);

            // Calculer les positions x et y
            float width = reader.getPageSize(n).getWidth();
            float signatureHeight = sigA.getScaledHeight();
            float height = reader.getPageSize(n).getBottom(30);

            // Positionnement des signatures
            float aX = width * 0.25f - sigA.getScaledWidth() / 2;
            float bX = width * 0.75f - sigB.getScaledWidth() / 2;
            sigA.setAbsolutePosition(aX, height);
            sigB.setAbsolutePosition(bX, height);

            // Add border to the signatures
            Rectangle borderA = new Rectangle(aX, height, aX + sigA.getScaledWidth(), height + sigA.getScaledHeight());
            borderA.setBorder(Rectangle.BOX);
            borderA.setBorderWidth(2);
            overContent.rectangle(borderA);

            Rectangle borderB = new Rectangle(bX, height, bX + sigB.getScaledWidth(), height + sigB.getScaledHeight());
            borderB.setBorder(Rectangle.BOX);
            borderB.setBorderWidth(2);
            overContent.rectangle(borderB);

            // Ajouter les signatures
            overContent.addImage(sigA);
            overContent.addImage(sigB);

            // Ajouter les textes
            Font font = new Font(Font.FontFamily.HELVETICA, 12);
            Chunk signatureAText = new Chunk("Signature du Conducteur A", font);
            Chunk signatureBText = new Chunk("Signature du Conducteur B", font);
            ColumnText.showTextAligned(overContent, Element.ALIGN_CENTER, new Phrase(signatureAText), aX + sigA.getScaledWidth() / 2, height + signatureHeight + 5, 0);
            ColumnText.showTextAligned(overContent, Element.ALIGN_CENTER, new Phrase(signatureBText), bX + sigB.getScaledWidth() / 2, height + signatureHeight + 5, 0);

            stamper.close();
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("DataRecorder", "Erreur lors de l'ajout des signatures: " + e.getMessage());
        }
    }

    public boolean deleteFile(String filename) {
        String filePath = getDirectoryPath() + "/" + filename;
        File file = new File(filePath);
        if (file.exists()) {
            return file.delete();
        } else {
            System.out.println("File does not exist: " + filename);
            return false;
        }
    }

    // Dans DataRecorder.java
    public Set<String> extractEmails(String data) {
        Set<String> emails = new HashSet<>();
        Matcher m = Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}").matcher(data);
        while (m.find()) {
            emails.add(m.group());
        }
        return emails;
    }

    // If we want to automatically send the email without user interaction
    public void sendEmailWithPDF_automatically() {
        // Extraction des adresses e-mail
        Set<String> emailAddresses = extractEmailAddressesFromFiles();

        // Sujet de l'e-mail avec la date du jour
        String emailSubject = "Easyclaim " + new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date()) + " accident report";

        // Chemin d'accès au fichier PDF
        String pdfFilePath = getDirectoryPath() + "/constat_amiable_signed.pdf";

        // Création et envoi de l'e-mail
        EmailSender emailSender = new EmailSender();
        emailSender.sendEmailWithAttachment(
                emailSubject,
                "Veuillez trouver ci-joint le rapport d'accident.",
                emailAddresses,
                pdfFilePath
        );
    }

    private Set<String> extractEmailAddressesFromFiles() {
        String reformattedData = readFile("Easyclaim_data_reformat.txt");
        String page3Data = readFile("report_page3_data.txt");
        return extractEmails(reformattedData + page3Data);
    }

    // If we want to send the email using an Intent (so the user need to choose a do a action)
    public void sendEmailWithPDF_old() {
        String reformattedData = readFile("Easyclaim_data_reformat.txt");
        String page3Data = readFile("report_page3_data.txt");
        Set<String> emailAddresses = extractEmails(reformattedData + page3Data);
        Log.e("DataRecorder", "Email addresses: dssssssssssssss  " + emailAddresses);
        String emailSubject = "Easyclaim " + new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date()) + " accident report";

        File pdfFile = new File(getDirectoryPath(), "constat_amiable_signed.pdf");
        Uri pdfUri = FileProvider.getUriForFile(activity, "com.example.easyclaim.fileprovider", pdfFile);

        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("vnd.android.cursor.dir/email");
        String[] to = emailAddresses.toArray(new String[0]);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, to);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, emailSubject);
        emailIntent.putExtra(Intent.EXTRA_STREAM, pdfUri);

        if (emailIntent.resolveActivity(activity.getPackageManager()) != null) {
            Log.d("DataRecorder", "Email intent resolved");
            activity.startActivity(Intent.createChooser(emailIntent, "Send email using..."));
        } else {
            Toast.makeText(activity, "No email clients installed.", Toast.LENGTH_SHORT).show();
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