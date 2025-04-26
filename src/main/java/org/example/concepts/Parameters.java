package org.example.concepts;
import org.example.SnpLoader;
import org.example.concepts.attributes.Gender;
import org.example.concepts.attributes.Snp;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;

public class Parameters {
    public Map<String, Map<String, Snp>> patientData;

    public Integer minAge;
    public Integer maxAge;
    public List<Gender> gender = new ArrayList<>();
    public List<String> countryList = new ArrayList<>();

    public Integer maxDrugs;
    public Integer snpsPerDrugType;
    public Integer percentageOfSnpsForDrugPerDrugType;
    public Double baseDrugEffectiveness;
    public Double negativePriorDrugEvent;
    public Double positivePriorDrugEvent;
    public Double positiveResponseAnotherDrug;
    public Double negativeResponseAnotherDrug;

    public String url;
    public String user;
    public String password;

    private final Map<String, Consumer<String>> argumentHandlers = new HashMap<>();

    public Parameters(String[] args) {
        registerHandlers();
        parseArguments(args);
        patientData = SnpLoader.loadSnps(url, user, password);
        writeOutParameters();
    }

    private void registerHandlers() {
        argumentHandlers.put("db_Path", val -> url = val);
        argumentHandlers.put("db_User", val ->user = val);
        argumentHandlers.put("db_Password", val -> password = val);
        argumentHandlers.put("minAge", val -> minAge = Integer.parseInt(val));
        argumentHandlers.put("maxAge", val -> maxAge = Integer.parseInt(val));
        argumentHandlers.put("maxDrugs", val -> maxDrugs = Integer.parseInt(val));
        argumentHandlers.put("snpsPerDrugType", val -> snpsPerDrugType = Integer.parseInt(val));
        argumentHandlers.put("percentageOfSnpsForDrugPerDrugType", val -> percentageOfSnpsForDrugPerDrugType = Integer.parseInt(val));
        argumentHandlers.put("baseDrugEffectiveness", val -> baseDrugEffectiveness = Double.parseDouble(val));
        argumentHandlers.put("negativePriorDrugEvent", val -> negativePriorDrugEvent = Double.parseDouble(val));
        argumentHandlers.put("positivePriorDrugEvent", val -> positivePriorDrugEvent = Double.parseDouble(val));
        argumentHandlers.put("gender", val -> gender.add(Gender.valueOf(val.toUpperCase())));
        argumentHandlers.put("countries", val -> countryList = Arrays.asList(val.split(",")));
        argumentHandlers.put("positiveResponseAnotherDrug", val -> positiveResponseAnotherDrug = Double.parseDouble(val));
        argumentHandlers.put("negativeResponseAnotherDrug", val -> negativeResponseAnotherDrug = Double.parseDouble(val));
    }

    private void parseArguments(String[] args) {
        for (String arg : args) {
            if (arg.startsWith("--")) {
                String[] keyValue = arg.substring(2).split("=", 2);
                if (keyValue.length == 2 && argumentHandlers.containsKey(keyValue[0])) {
                    argumentHandlers.get(keyValue[0]).accept(keyValue[1]);
                } else {
                    System.out.println("Invalid argument: " + keyValue[0]);
                }
            }
        }
    }

    public void writeOutParameters() {
        try (FileWriter writer = new FileWriter("/app/data/output/parameters.txt")) {
            writer.write("Parameters Configuration\n");
            writer.write("=======================\n");
            writer.write("minAge: " + (minAge != null ? minAge : "Not Set") + "\n");
            writer.write("maxAge: " + (maxAge != null ? maxAge : "Not Set") + "\n");
            writer.write("Gender: " + (gender.isEmpty() ? "Not set": gender.toString()) + "\n");
            writer.write("Countries: " + (countryList.isEmpty() ? "Not set": countryList.toString()) + "\n");
            writer.write("maxDrugs: " + (maxDrugs != null ? maxDrugs : "Not Set") + "\n");
            writer.write("snpsPerDrugType: " + (snpsPerDrugType != null ? snpsPerDrugType : "Not Set") + "\n");
            writer.write("percentageOfSnpsForDrugPerDrugType: " + (percentageOfSnpsForDrugPerDrugType != null ? percentageOfSnpsForDrugPerDrugType : "Not Set") + "\n");
            writer.write("baseDrugEffectiveness: " + (baseDrugEffectiveness != null ? baseDrugEffectiveness : "Not Set") + "\n");
            writer.write("negativePriorDrugEvent: " + (negativePriorDrugEvent != null ? negativePriorDrugEvent : "Not Set") + "\n");
            writer.write("positivePriorDrugEvent: " + (positivePriorDrugEvent != null ? positivePriorDrugEvent : "Not Set") + "\n");
            writer.write("positiveResponseAnotherDrug: " + (positiveResponseAnotherDrug != null ? positiveResponseAnotherDrug : "Not Set") + "\n");
            writer.write("negativeResponseAnotherDrug: " + (negativeResponseAnotherDrug != null ? negativeResponseAnotherDrug : "Not Set") + "\n");

            System.out.println("Parameters written to parameters.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
