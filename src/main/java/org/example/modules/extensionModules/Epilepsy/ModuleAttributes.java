package org.example.modules.extensionModules.Epilepsy;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.example.concepts.Module;
import org.example.concepts.Patient;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ModuleAttributes extends Module {

    private final Map<String, Map<String, Double>> oddsRatios;

    public ModuleAttributes(Map<String, Map<String, Double>> oddsRatios) {
        this.oddsRatios = oddsRatios;
    }

    // Methode zum Laden der JSON-Daten in eine Map
    public static Map<String, Map<String, Double>> loadAttributes(String filePath) {
        Map<String, Map<String, Double>> oddsRatios = null;
        try {
            Gson gson = new Gson();
            FileReader reader = new FileReader(filePath);

            Type type = new TypeToken<Map<String, Map<String, Double>>>() {}.getType();
            oddsRatios = gson.fromJson(reader, type);

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return oddsRatios;
    }

    public void assignRandomAttributes(Patient patient) {
        Random random = new Random();

        // Durchlaufe alle Attribute in der Map
        for (Map.Entry<String, Map<String, Double>> entry : oddsRatios.entrySet()) {
            String attribute = entry.getKey();
            Map<String, Double> categories = entry.getValue();

            double attributeTotalOdds = 0.0;
            // Berechne die Summe der Odds Ratios für das Attribut
            for (Double value : categories.values()) {
                attributeTotalOdds += value;
            }

            // Erstelle eine kumulierte Liste der Wahrscheinlichkeiten
            double cumulativeProbability = 0.0;
            Map<String, Double> cumulativeProbabilities = new HashMap<>();
            int totalCategories = categories.size();
            int currentCategoryIndex = 0;

            for (Map.Entry<String, Double> categoryEntry : categories.entrySet()) {
                String category = categoryEntry.getKey();
                double probability = categoryEntry.getValue() / attributeTotalOdds;
                cumulativeProbability += probability;

                // Wenn es der letzte Eintrag ist, stelle sicher, dass die kumulierte Wahrscheinlichkeit genau 1 ist
                if (++currentCategoryIndex == totalCategories) {
                    // Normalisiere die kumulierte Wahrscheinlichkeit auf 1, falls nötig
                    if (cumulativeProbability > 1) {
                        cumulativeProbability = 1;
                    } else if (cumulativeProbability < 1) {
                        cumulativeProbability = 1;
                    }
                }
                System.out.println(cumulativeProbability);

                cumulativeProbabilities.put(category, cumulativeProbability);
            }

            // Ziehe eine Zufallszahl und weise die Kategorie basierend auf der kumulierten Wahrscheinlichkeit zu
            double randomValue = random.nextDouble();
            for (Map.Entry<String, Double> categoryEntry : cumulativeProbabilities.entrySet()) {
                if (randomValue < categoryEntry.getValue()) {
                    patient.getAttributes().put(attribute, categoryEntry.getKey());
                    break;
                }
            }
        }
    }
    // Überschreibe processData von der abstrakten Module-Klasse
    @Override
    public Patient processData(Patient patient) {
        assignRandomAttributes(patient); // Weist die zufälligen Attribute zu
        return patient; // Rückgabe des Patienten nach der Attributzuweisung
    }
}
