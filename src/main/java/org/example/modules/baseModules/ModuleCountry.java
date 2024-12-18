package org.example.modules.baseModules;
import org.example.concepts.Country;
import org.example.concepts.Module;
import org.example.concepts.Patient;
import com.opencsv.exceptions.CsvValidationException;
import org.example.helper.CsvLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ModuleCountry extends Module{
private List<String[]> csvData;
private List<Country> countries = new ArrayList<>();

int totalPopulation = 0;
    public ModuleCountry() {

    }
    @Override
    public Patient processData(Patient patient) {
        patient.getAttributes().put("country", getRandomCountry().getName());
        return patient;
    }

    private Country getRandomCountry() {
        Random random = new Random();
        int randomValue = random.nextInt(totalPopulation);
        int currentSum = 0;
        for (Country country : countries) {
            currentSum += country.getPopulation();
            if (randomValue < currentSum) {
                return country; // Found the country based on population probability
            }
        }
        return null;
    }
}
