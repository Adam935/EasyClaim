package com.example.easyclaim.util;

import java.util.HashMap;
import java.util.Map;

public class NomenclatureDictionary {
    private Map<String, String> nomenclature;
    private Map<String, String> nomenclature_bis;

    public NomenclatureDictionary() {
        initializeNomenclature();
        initializeNomenclatureBis();
    }

    private void initializeNomenclature() {
        nomenclature = new HashMap<>();
        nomenclature.put("O", "Nom de l'assurance");
        nomenclature.put("LP", "Plaque d'immatriculation");
        nomenclature.put("N", "Numero d'assurer");
        nomenclature.put("W", "Localisation du choc sur le vehicule");
        nomenclature.put("V", "Vitesse du choc");
        nomenclature.put("F", "Prenom");
        nomenclature.put("S", "Nom");
        nomenclature.put("A", "Adresse mail de l'assureur");
        nomenclature.put("K", "DJ");

    }

    private void initializeNomenclatureBis() {
        nomenclature_bis = new HashMap<>();
        nomenclature_bis.put("Nom de l'assurance", "insuranceCompanyTextView");
        nomenclature_bis.put("Plaque d'immatriculation", "licensePlateNumberTextView");
        nomenclature_bis.put("Numero d'assurer", "insuranceNumberTextView");
        nomenclature_bis.put("Localisation du choc sur le vehicule", "vehicleDamageLocationTextView");
        nomenclature_bis.put("Vitesse du choc", "insuranceGreenCardNumberTextView");
        nomenclature_bis.put("Prenom","ownerFirstNameTextView");
        nomenclature_bis.put("Nom","ownerLastNameTextView");
        nomenclature_bis.put("Adresse mail de l'assureur", "insuranceGreenCardStartDateTextView");
        nomenclature_bis.put("DJ", "insuranceGreenCardEndDateTextView");
    }

    public Map<String, String> getNomenclature() {
        return nomenclature;
    }

    public Map<String, String> getNomenclatureBis() {
        return nomenclature_bis;
    }
}