package org.example.concepts;
import org.example.SnpLoader;
import org.example.concepts.attributes.Gender;
import org.example.concepts.attributes.Snp;
import java.util.*;
import java.util.function.Consumer;

public class Parameters {
    public Map<String, Map<String, Snp>> patientData = SnpLoader.loadSnps("./src/main/resources/all_patients.csv");

    public Integer minAge;
    public Integer maxAge;
    public List<Gender> gender = new ArrayList<>();
    public List<String> countryList = new ArrayList<>();

    public Integer maxDrugs = 5;
    public Integer snpsPerDrugType = 100;
    public Integer percentageOfSnpsForDrugPerDrugType = 80;
    public Double baseDrugEffectiveness = 0.5;
    public Double negativePriorDrugEvent = -0.1;
    public Double positivePriorDrugEvent = 0.1;

    private final Map<String, Consumer<String>> argumentHandlers = new HashMap<>();

    public Parameters(String[] args) {
        registerHandlers();
        parseArguments(args);
    }

    private void registerHandlers() {
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
}
