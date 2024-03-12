package com.example.easyclaim.view;

import android.app.Activity;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.easyclaim.R;


public class SpinnerHelper {

    public static void setupSpinners(Activity activity) {
        // Créer des tableaux de chaînes pour les plages de valeurs
        String[] days = new String[31];
        String[] months = new String[12];
        String[] years = new String[101]; // 2000 - 2100
        String[] hours = new String[24];
        String[] minutes = new String[60];

        // Remplir les tableaux avec les valeurs appropriées
        for (int i = 0; i < 31; i++) {days[i] = String.valueOf(i + 1);}

        for (int i = 0; i < 12; i++) {months[i] = String.valueOf(i + 1);}

        for (int i = 0; i < 101; i++) {years[i] = String.valueOf(2000 + i);}

        for (int i = 0; i < 24; i++) {hours[i] = String.valueOf(i);}

        for (int i = 0; i < 60; i++) {minutes[i] = String.valueOf(i);}

        // Créer des adaptateurs personnalisés pour les Spinners
        ArrayAdapter<String> dayAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_item, days);
        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_item, months);
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_item, years);
        ArrayAdapter<String> hourAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_item, hours);
        ArrayAdapter<String> minuteAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_item, minutes);

        // Récupérer les Spinners depuis le layout
        Spinner daySpinner = activity.findViewById(R.id.daySpinner);
        Spinner monthSpinner = activity.findViewById(R.id.monthSpinner);
        Spinner yearSpinner = activity.findViewById(R.id.yearSpinner);
        Spinner hourSpinner = activity.findViewById(R.id.hourSpinner);
        Spinner minuteSpinner = activity.findViewById(R.id.minuteSpinner);

        // Définir les adaptateurs pour les Spinners
        daySpinner.setAdapter(dayAdapter);
        monthSpinner.setAdapter(monthAdapter);
        yearSpinner.setAdapter(yearAdapter);
        hourSpinner.setAdapter(hourAdapter);
        minuteSpinner.setAdapter(minuteAdapter);
    }
}
