package org.example;

import java.io.File;
import java.io.FileReader;
import java.util.*;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.example.concepts.Snp;

public class SnpLoader {
    public static Map<String, Map<String, Snp>> loadSnps(String folderPath) {
        try {
            Gson gson = new Gson();
            Map<String, Map<String, Snp>> patientSnps = new HashMap<>();

            // List all JSON files in the folder
            File folder = new File(folderPath);
            File[] jsonFiles = folder.listFiles((dir, name) -> name.endsWith(".json"));

            if (jsonFiles == null || jsonFiles.length == 0) {
                System.out.println("No JSON files found in folder: " + folderPath);
                return patientSnps;
            }

            //Initial patient ID
            int patientId = 1;
            // Process each JSON file
            for (File jsonFile : jsonFiles) {
                JsonElement rootElement = gson.fromJson(new FileReader(jsonFile), JsonElement.class);

                JsonArray snpsArray = rootElement.getAsJsonArray();

                Map<String, Snp> snpsList = new HashMap<>();
                for (JsonElement snpElement : snpsArray) {
                    JsonObject snpObject = snpElement.getAsJsonObject();

                    // Create an SNP object
                    Snp snp = new Snp();
                    snp.setChromosome(Integer.parseInt(snpObject.get("chromosome").getAsString()));
                    snp.setPosition(Integer.parseInt(snpObject.get("position").getAsString()));
                    snp.setRef(snpObject.get("reference").getAsString().charAt(0));
                    snp.setAlt(snpObject.get("alternative").getAsString().charAt(0));
                    snp.setExpression(snpObject.get("expression").getAsString());
                    snp.setRsId(snpObject.get("id").getAsString());

                    snpsList.put(snp.getRsId(), snp);
                }
                String patientIdString = String.valueOf(patientId);
                // Map patient ID to the list of SNPs
                patientSnps.put(patientIdString, snpsList);
                patientId++;
            }

            return patientSnps;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
