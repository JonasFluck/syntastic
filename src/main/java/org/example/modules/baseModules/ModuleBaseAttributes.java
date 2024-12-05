package org.example.modules.baseModules;

import com.opencsv.exceptions.CsvValidationException;
import org.example.concepts.*;
import org.example.concepts.Module;
import org.example.helper.CsvLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class ModuleBaseAttributes extends Module {
    private int minAge;
    private int maxAge;
    private List<String> inputCountries;
    private List<Country> countries;
    private Gender gender;
    private List<String[]> csvData;
    private int totalPopulation;

    // Constructor is private to prevent direct instantiation
    private ModuleBaseAttributes(Builder builder) {
        this.minAge = builder.minAge;
        this.maxAge = builder.maxAge;
        this.inputCountries = builder.countries;
        this.gender = builder.gender;
        try{
            this.csvData = CsvLoader.readCSVFromResources("euro_pop.csv");
            csvData.remove(0); // Remove the header row
            countries = parseCsvData(csvData);
            totalPopulation = countries.stream().mapToInt(Country::getPopulation).sum();
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }
    }
    private List<Country> parseCsvData (List<String[]> input){
        List<Country> countries = new ArrayList<>();
        for (String[] row : input){
            String name = row[6];
            countries.stream()
                    .filter(country -> country.getName().equals(name))
                    .findFirst() // Get the first match (if any)
                    .ifPresent(country -> {
                        // Update the country data (e.g., add or update some attribute)
                        country.getAgeGroups().stream().filter(group -> group.getAge() == getAge(row[4]))
                                .findFirst()
                                .ifPresent(group -> {
                                    //Age group already present
                                    if(row[5] == "Female")
                                        group.setPopulationFemale(Integer.parseInt(row[8]));
                                    else
                                        group.setPopulationMale(Integer.parseInt(row[8]));
                                });
                        if (country.getAgeGroups().stream().noneMatch(group -> group.getAge() == getAge(row[4]))){
                            //adding new Age group
                            AgeGroup group = new AgeGroup(getAge(row[4]));
                            if(row[5] == "Female")
                                group.setPopulationFemale(Integer.parseInt(row[8]));
                            else
                                group.setPopulationMale(Integer.parseInt(row[8]));
                            country.getAgeGroups().add(group);
                        }
                    });

            // If no match is found, you can add a new country (optional)
            if (countries.stream().noneMatch(country -> country.getName().equals(name))) {
                Country country = new Country(name);
                AgeGroup group = new AgeGroup(getAge(row[4]));
                if(row[5] == "Female")
                    group.setPopulationFemale(Integer.parseInt(row[8]));
                else
                    group.setPopulationMale(Integer.parseInt(row[8]));
                country.getAgeGroups().add(group);
                countries.add(country);
            }
        }
        return countries;
    }

    private int getAge (String input) {
        int age = 0;
        if(input.equals("Open-ended age class"))
            return 100;
        if(input== "Less than 1 year")
            return 0;
        String numericPart = input.replaceAll("\\D", "");  // "\\D" matches non-digits
        try{
                      age = Integer.parseInt(numericPart);
        }
        catch (Exception e) {
            e.printStackTrace();
        }                                 // If the key exists, add to the existing list, otherwise create a new list and add the element
        return age;
    }
    // The nested Builder class
    public static class Builder {
        private int minAge;
        private int maxAge;
        private List<String> countries;
        private Gender gender;

        // Optional fields can have defaults
        public Builder() {
            // Default values (if needed, can be left blank or have specific defaults)
            this.minAge = 0;
            this.maxAge = 100;
        }

        // Setter-like methods for each field
        public Builder setMinAge(int minAge) {
            this.minAge = minAge;
            return this;
        }

        public Builder setMaxAge(int maxAge) {
            this.maxAge = maxAge;
            return this;
        }

        public Builder setCountries(List<String> countries) {
            this.countries = countries;
            return this;
        }

        public Builder setGender(Gender gender) {
            this.gender = gender;
            return this;
        }

        // Build method to return the final object
        public ModuleBaseAttributes build() {
            return new ModuleBaseAttributes(this);
        }
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

    private int getRandomCAge(Country country) {
        Random random = new Random();
        int totalPopulation =  country.getAgeGroups().stream().mapToInt(AgeGroup::getTotalPopulation).sum();
        int randomValue = random.nextInt(totalPopulation);
        int currentSum = 0;
        for(AgeGroup ageGroup : country.getAgeGroups()){
            currentSum += ageGroup.getTotalPopulation();
            if(randomValue < currentSum){
                return ageGroup.getAge();
            }
        }
        return 0;
    }

    private Gender getRandomGender(AgeGroup ageGroup){
        Random random = new Random();
        int totalPopulation =  ageGroup.getTotalPopulation();
        int randomValue = random.nextInt(totalPopulation);
        int currentSum = 0;
        if(randomValue < ageGroup.getPopulationMale()){
            return Gender.Male;
        }
        else
            return Gender.Female;
    }

    // The processData method as you have it
    @Override
    public Patient processData(Patient patient) {
        Country country = getRandomCountry();
        int age = getRandomCAge(country);
        patient.getAttributes().put("country", country.getName());
        patient.getAttributes().put("age",age);
        patient.getAttributes().put("gender", getRandomGender(country.getAgeGroups().stream().filter(group -> group.getAge() == age).findFirst().get()));
        //patient = moduleGender.processData(patient);
        return patient;
    }
}
