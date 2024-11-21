package org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.example.concepts.Module;
import org.example.concepts.Patient;
import org.example.modules.config.ModuleLoader;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        SnpLoader loader = new SnpLoader();
        ModuleLoader moduleLoader = new ModuleLoader();
        List<Module> activeModules = moduleLoader.loadModules("config.json");
        Generator generator = new Generator(activeModules);
        List<Patient> patients= generator.generatePatients(loader.loadSnps("all_patients.json"));
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try (FileWriter writer = new FileWriter("patients.json")) {
            // Serialize the list of patients to JSON and write to a file
            gson.toJson(patients, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        long endTime = System.currentTimeMillis();
        System.out.println("Execution time: " + (endTime - startTime) + " milliseconds");
    }
}