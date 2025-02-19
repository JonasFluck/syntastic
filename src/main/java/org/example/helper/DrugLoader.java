package org.example.helper;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.example.concepts.attributes.Drug;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.List;

public class DrugLoader {

    // Method to load drugs from the JSON file
    public static List<Drug> loadDrugs(String fileName) {
        Gson gson = new Gson();
        Type listType = new TypeToken<List<Drug>>() {
        }.getType();

        try (InputStream inputStream = DrugLoader.class.getClassLoader().getResourceAsStream(fileName)) {
            if (inputStream == null) {
                throw new IOException("File not found: " + fileName);
            }
            // Deserialize JSON into a List of Drug objects
            return gson.fromJson(new InputStreamReader(inputStream), listType);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load drugs from file: " + fileName, e);
        }
    }
}
