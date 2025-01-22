package org.example.concepts;
import org.example.SnpLoader;
import org.example.concepts.handlers.*;
import java.util.*;

public class Parameters {
    public Map<String,Map<String, Snp>> patientData = SnpLoader.loadSnps("all_patients.json");

    //Base attributes
    public int minAge = -1;
    public int maxAge = -1;
    public Gender gender;
    public List<String> countryList;

    //Epilepsy attributes
    public int maxDrugs = 5;
    public int snpsPerDrugType = 100;
    public int percentageOfSnpsForDrugPerDrugType = 80;
    public double baseDrugEffectiveness = 0.5;
    public double negativePriorDrugEvent = -0.1;
    public double positivePriorDrugEvent= 0.1;




    // Map to store argument handlers
    private final List<ArgumentHandler> argumentHandlers = new ArrayList<>();

    public Parameters(String[] args) {
        // Register handlers
        registerHandlers();

        // Parse arguments
        for (String arg : args) {
            if (arg.startsWith("--")) {
                String[] keyValue = arg.substring(2).split("=", 2);
                if (keyValue.length == 2) {
                    String name = keyValue[0];
                    String value = keyValue[1];
                    boolean handled = false;

                    for (ArgumentHandler handler : argumentHandlers) {
                        try {
                            handler.handle(name, value, this);
                            handled = true;
                            break;
                        } catch (IllegalArgumentException e) {
                            // Continue to the next handler
                        }
                    }

                    if (!handled) {
                        System.out.println("Invalid argument: " + name);
                    }
                } else {
                    System.out.println("Invalid argument: " + arg);
                }
            }
        }
    }

    private void registerHandlers() {
        argumentHandlers.add(new BaseAttributeHandler());
        argumentHandlers.add(new EpilepsyHandler());
        // Add more handlers as needed
    }
}
