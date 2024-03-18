package com.example.easyclaim.util;

import java.util.HashMap;
import java.util.Map;

public class NomenclatureDictionary {
    private Map<String, String> nomenclature;
    private Map<String, String> nomenclature_bis;
    private Map<String, String> circumstancesNomenclature;

    public NomenclatureDictionary() {
        initializeNomenclature();
        initializeNomenclatureBis();
        initializeCircumstancesNomenclature();
    }

    private void initializeNomenclature() {
        nomenclature = new HashMap<>();
        nomenclature.put("O", "Name of insurance company");
        nomenclature.put("LP", "Immatriculation number");
        nomenclature.put("N", "Insurance number");
        nomenclature.put("W", "Location of impact on the vehicle");
        nomenclature.put("V", "Shock speed");
        nomenclature.put("F", "First name");
        nomenclature.put("S", "last name");
        nomenclature.put("A", "Insurer's e-mail address");
        nomenclature.put("K", "DJ");

    }

    private void initializeNomenclatureBis() {
        nomenclature_bis = new HashMap<>();
        nomenclature_bis.put("Name of insurance company", "insuranceCompanyTextView");
        nomenclature_bis.put("Immatriculation number", "licensePlateNumberTextView");
        nomenclature_bis.put("Insurance number", "insuranceNumberTextView");
        nomenclature_bis.put("Location of impact on the vehicle", "vehicleDamageLocationTextView");
        nomenclature_bis.put("Shock speed", "insuranceGreenCardNumberTextView");
        nomenclature_bis.put("First name","ownerFirstNameTextView");
        nomenclature_bis.put("last name","ownerLastNameTextView");
        nomenclature_bis.put("Insurer's e-mail address", "insuranceGreenCardStartDateTextView");
        nomenclature_bis.put("DJ", "insuranceGreenCardEndDateTextView");
    }
    private void initializeCircumstancesNomenclature() {
        circumstancesNomenclature = new HashMap<>();
        circumstancesNomenclature.put("A1", "Leaving a parking lot / opening the car door");
        circumstancesNomenclature.put("B1", "Leaving a parking lot / opening the car door");
        circumstancesNomenclature.put("A2", "Took a parking space");
        circumstancesNomenclature.put("B2", "Took a parking space");
        circumstancesNomenclature.put("A3", "Coming out of a parking lot, a private place");
        circumstancesNomenclature.put("B3", "Coming out of a parking lot, a private place");
        circumstancesNomenclature.put("A4", "Rear-end collision, driving in the same direction and in the same lane");
        circumstancesNomenclature.put("B4", "Rear-end collision, driving in the same direction and in the same lane");
        circumstancesNomenclature.put("A5", "Step back");
        circumstancesNomenclature.put("B5", "Step back");
        circumstancesNomenclature.put("A6", "Failed to observe a right-of-way signal or a red light");
        circumstancesNomenclature.put("B6", "Failed to observe a right-of-way signal or a red light");
    }
    public Map<String, String> getNomenclature() {
        return nomenclature;
    }

    public Map<String, String> getNomenclatureBis() {
        return nomenclature_bis;
    }
    public Map<String, String> getCircumstancesNomenclature() {return circumstancesNomenclature;}
}