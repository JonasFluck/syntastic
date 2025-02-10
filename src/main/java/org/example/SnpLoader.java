package org.example;

import java.io.*;
import java.util.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.example.concepts.Snp;

public class SnpLoader {
    public static Map<String, Map<String, Snp>> loadSnps(String csvFilePath) {
        Map<String, Map<String, Snp>> patientSnps = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            boolean headerSkipped = false;  // To skip the first line (header)

            while ((line = br.readLine()) != null) {
                // Skip header line
                if (!headerSkipped) {
                    headerSkipped = true;
                    continue;
                }

                // Split CSV line by comma
                String[] parts = line.split(",");

                // Extract fields
                String patientId = parts[0];
                int chromosome = Integer.parseInt(parts[1]);
                String snpId = parts[2];
                int position = Integer.parseInt(parts[3]);
                char reference = parts[4].charAt(0);
                char alternative = parts[5].charAt(0);
                String expression = parts[6];

                // Create SNP object
                Snp snp = new Snp();
                snp.setRsId(snpId);
                snp.setChromosome(chromosome);
                snp.setPosition(position);
                snp.setRef(reference);
                snp.setAlt(alternative);
                snp.setExpression(expression);

                // Add to patient SNP map
                patientSnps.computeIfAbsent(patientId, k -> new HashMap<>()).put(snpId, snp);
            }

            System.out.println("CSV successfully loaded into memory.");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error reading CSV file: " + csvFilePath);
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try (FileWriter writer = new FileWriter("test_patients.json")) {
            writer.write(gson.toJson(patientSnps));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return patientSnps;
    }
}
