package com.example.easyclaim.util;

import android.util.Log;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfPTable;

public class PDFGenerator {
    // Méthode pour générer le PDF à partir des données collectées
    public void generatePDF(Map<String, String> data, String filePath) {
        Document document = new Document();

        try {
            // Création du fichier PDF
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            // Parcours de toutes les données et ajout au PDF sous forme de paragraphes
            for (Map.Entry<String, String> entry : data.entrySet()) {
                String title = entry.getKey();
                String content = entry.getValue();

                // Ajout du titre du fichier comme en-tête de section dans le PDF
                document.add(new Paragraph(title));
                // Ajout du contenu du fichier comme paragraphe dans le PDF
                document.add(new Paragraph(content));
                // Ajout d'un saut de ligne pour séparer les sections
                document.add(new Paragraph("\n"));
            }

            document.close();

            // Affichage d'un message indiquant que le PDF a été généré avec succès
            Log.v("testoma","PDF generated successfully at: " + filePath);
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }
    }

    public void generateSignaturePDF(String signatureAPath, String signatureBPath, String outputPath) {
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(outputPath));
            document.open();

            // Créer une table avec 2 colonnes
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100); // La table occupe 100% de la largeur de la page

            // Cellule pour "Signature du Conducteur A"
            PdfPCell cellA = new PdfPCell(new Phrase("Signature du Conducteur A", new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)));
            cellA.setBorder(Rectangle.NO_BORDER); // Pas de bordure pour cette cellule
            cellA.setHorizontalAlignment(Element.ALIGN_CENTER); // Centrer le texte
            table.addCell(cellA);

            // Cellule pour "Signature du Conducteur B"
            PdfPCell cellB = new PdfPCell(new Phrase("Signature du Conducteur B", new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)));
            cellB.setBorder(Rectangle.NO_BORDER); // Pas de bordure pour cette cellule
            cellB.setHorizontalAlignment(Element.ALIGN_CENTER); // Centrer le texte
            table.addCell(cellB);

            // Ajouter la signature du Conducteur A
            Image signatureA = Image.getInstance(signatureAPath);
            signatureA.scaleToFit(150, 150); // Ajuster la taille de l'image
            PdfPCell cellSignatureA = new PdfPCell(signatureA);
            cellSignatureA.setPaddingTop(25); // Espacement au-dessus de l'image
            table.addCell(cellSignatureA);

            // Ajouter la signature du Conducteur B
            Image signatureB = Image.getInstance(signatureBPath);
            signatureB.scaleToFit(150, 150); // Ajuster la taille de l'image
            PdfPCell cellSignatureB = new PdfPCell(signatureB);
            cellSignatureB.setPaddingTop(25); // Espacement au-dessus de l'image
            table.addCell(cellSignatureB);

            // Ajouter la table au document
            document.add(table);

            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
