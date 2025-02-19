package org.example.helper;

import org.example.concepts.attributes.Trait;
import org.example.concepts.attributes.Variant;

import java.io.BufferedReader;
import java.io.IOException;

import java.io.InputStream;
import java.io.InputStreamReader;

public class TraitFileReader {

    public static Trait readTraitFromFile(String resourcePath) {
        Trait trait = new Trait();

        // Use ClassLoader to load the file from the resources folder
        try (InputStream inputStream = TraitFileReader.class.getClassLoader().getResourceAsStream(resourcePath);
             BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {

            String line;
            boolean isHeader = true;

            while ((line = br.readLine()) != null) {
                line = line.trim();

                // Skip comments
                if (line.startsWith("#")) {
                    if (line.startsWith("#trait_reported=")) {
                        trait.setTraitReported(line.split("=")[1]);
                    }
                    continue;
                }

                // Skip the table header row
                if (isHeader) {
                    isHeader = false;
                    continue;
                }

                // Parse data rows
                String[] columns = line.split("\t");
                String rsID = columns[0];
                String chrName = columns[1];
                int chrPosition = Integer.parseInt(columns[2]);
                String effectAllele = columns[3];
                String otherAllele = columns[4];
                double effectWeight = Double.parseDouble(columns[5]);

                Variant variant = new Variant(rsID, chrName, chrPosition, effectAllele, otherAllele, effectWeight);
                trait.addVariant(rsID,variant);
            }

        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }

        return trait;
    }
}

