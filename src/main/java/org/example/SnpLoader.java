package org.example;

import java.io.FileReader;
import java.util.*;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.example.concepts.Snp;

public class SnpLoader {
    public Map<String, Map<String,Snp>> loadSnps(String filePath) {
        try {
            Gson gson = new Gson();
            JsonArray jsonArray = gson.fromJson(new FileReader(filePath), JsonArray.class);
            Map<String, Map<String,Snp>> patientSnps = new HashMap<>();

            for (JsonElement patientElement : jsonArray) {
                JsonObject patientObject = patientElement.getAsJsonObject();
                String patientId = patientObject.get("patient_id").getAsString();
                JsonArray snpsArray = patientObject.getAsJsonArray("snps");

                Map<String,Snp> snpsList = new HashMap<>();
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

                    snpsList.put(snp.getRsId(),snp);
                }

                // Map patient ID to the list of SNPs
                patientSnps.put(patientId, snpsList);
            }

            return patientSnps;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
