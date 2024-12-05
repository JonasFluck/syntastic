package org.example.helper;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CsvLoader {
    public static List<String[]> readCSVFromResources(String fileName) throws IOException, CsvValidationException {
        // Get the file as an InputStream from the resources folder
        ClassLoader classLoader = CsvLoader.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(fileName);

        if (inputStream == null) {
            throw new IOException("File not found in resources folder: " + fileName);
        }

        // Read the CSV data using OpenCSV
        List<String[]> data = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new InputStreamReader(inputStream))) {
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                data.add(nextLine);
            }
        }

        return data;
    }
}
