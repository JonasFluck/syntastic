package org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.example.concepts.*;
import org.example.concepts.Module;
import org.example.modules.baseModules.ModuleBaseAttributes;
import org.example.modules.extensionModules.Epilepsy.ModuleEpilepsy;
import org.example.modules.extensionModules.Epilepsy.ModuleAttributes;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        Parameters parameters = new Parameters(args);
        ModuleFactory moduleFactory = new ModuleFactory(parameters);
        List<Module> activeModules = moduleFactory.generateModules(new ArrayList<>(List.of(ModuleBaseAttributes.class, ModuleEpilepsy.class, ModuleAttributes.class)));
        PatientFactory patientFactory = new PatientFactory(activeModules);
        List<Patient> patients = patientFactory.generatePatients(parameters.patientData);
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