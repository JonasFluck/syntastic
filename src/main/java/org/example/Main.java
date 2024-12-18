package org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.example.concepts.Module;
import org.example.concepts.Patient;
import org.example.concepts.Gender;
import org.example.concepts.Snp;
import org.example.modules.baseModules.ModuleBaseAttributes;
import org.example.modules.config.ModuleLoader;
import org.example.modules.extensionModules.Epilepsy.ModuleEpilepsy;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        //SnpLoader loader = new SnpLoader();
        //ModuleLoader moduleLoader = new ModuleLoader();
        //List<Module> activeModules = moduleLoader.loadModules("config.json");
        Map<String,Map<String, Snp>> patientData = SnpLoader.loadSnps("all_patients.json");
        List<Module> activeModules = new ArrayList<>();
        activeModules.add(new ModuleBaseAttributes.Builder()
                        .setMinAge(25)
                        .setMaxAge(75)
                        .setGender(Gender.Male)
                        .setCountries(List.of("Germany", "France", "Italy"))
                .build());

        List<Snp> relevantSnpEpilepsy = patientData.values().stream() // Stream<Map<String, Snp>>
                .flatMap(innerMap -> innerMap.values().stream()) // Stream<Snp>
                .distinct() // Ensure uniqueness
                .toList();
        activeModules.add(new ModuleEpilepsy(relevantSnpEpilepsy));

        Generator generator = new Generator(activeModules);
        List<Patient> patients= generator.generatePatients(patientData);
        exportResults(patients, "patients.json");
        long endTime = System.currentTimeMillis();
        System.out.println("Execution time: " + (endTime - startTime) + " milliseconds");
    }

    private static void exportResults(List<Patient> patients, String filename) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(filename)) {
            // Serialize the list of patients to JSON and write to a file
            gson.toJson(patients, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}