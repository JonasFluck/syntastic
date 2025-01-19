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

    // Constructor updated to use the parent class's builder pattern (if applicable)
    private ModuleAttributes(ModuleBuilder<?, ?> builder, Map<String, Map<String, Double>> oddsRatios) {
        super(builder);
        this.oddsRatios = oddsRatios;
    }

    // Method to load JSON data into a Map
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

    // Assign random attributes to the patient based on the odds ratios
    public void assignRandomAttributes(Patient patient) {
        Random random = new Random();

        // Loop through all attributes in the map
        for (Map.Entry<String, Map<String, Double>> entry : oddsRatios.entrySet()) {
            String attribute = entry.getKey();
            Map<String, Double> categories = entry.getValue();

            double attributeTotalOdds = 0.0;
            // Calculate the sum of odds ratios for the attribute
            for (Double value : categories.values()) {
                attributeTotalOdds += value;
            }

            // Create a cumulative list of probabilities
            double cumulativeProbability = 0.0;
            Map<String, Double> cumulativeProbabilities = new HashMap<>();
            int totalCategories = categories.size();
            int currentCategoryIndex = 0;

            for (Map.Entry<String, Double> categoryEntry : categories.entrySet()) {
                String category = categoryEntry.getKey();
                double probability = categoryEntry.getValue() / attributeTotalOdds;
                cumulativeProbability += probability;

                // Ensure the cumulative probability is exactly 1 for the last category
                if (++currentCategoryIndex == totalCategories) {
                    if (cumulativeProbability > 1) {
                        cumulativeProbability = 1;
                    } else if (cumulativeProbability < 1) {
                        cumulativeProbability = 1;
                    }
                }

                cumulativeProbabilities.put(category, cumulativeProbability);
            }

            // Generate a random value and assign a category based on cumulative probability
            double randomValue = random.nextDouble();
            for (Map.Entry<String, Double> categoryEntry : cumulativeProbabilities.entrySet()) {
                if (randomValue < categoryEntry.getValue()) {
                    patient.getAttributes().put(attribute, categoryEntry.getKey());
                    break;
                }
            }
        }
    }

    // Override processData method from the abstract Module class
    @Override
    public Patient processData(Patient patient) {
        assignRandomAttributes(patient); // Assign random attributes to the patient
        return patient; // Return the patient after assigning the attributes
    }

    // Module-specific builder
    public static class Builder extends Module.ModuleBuilder<ModuleAttributes, Builder> {

        private Map<String, Map<String, Double>> oddsRatios;

        public Builder setOddsRatios(Map<String, Map<String, Double>> oddsRatios) {
            this.oddsRatios = oddsRatios;
            return this;
        }

        @Override
        protected Builder self() {
            return null;
        }

        @Override
        public ModuleAttributes build() {
            return new ModuleAttributes(this, oddsRatios);
        }
    }
}
