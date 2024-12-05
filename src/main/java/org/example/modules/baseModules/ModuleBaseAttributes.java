package org.example.modules.baseModules;

import com.opencsv.exceptions.CsvValidationException;
import org.example.concepts.*;
import org.example.concepts.Module;
import org.example.helper.CsvLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class ModuleBaseAttributes extends Module {

    private int minAge;
    private int maxAge;
    private Gender gender;
    private List<Country> countries;
    private List<String[]> csvData;
    private int totalPopulation;

    // Constructor is private to prevent direct instantiation
    private ModuleBaseAttributes(Builder builder) {
        this.minAge = builder.minAge;
        this.maxAge = builder.maxAge;
        this.gender = builder.gender;
        List<String> inputCountries = builder.countries;
        Gender gender = builder.gender;
        try{
            this.csvData = CsvLoader.readCSVFromResources("euro_pop.csv");
            csvData.remove(0); // Remove the header row
            countries = parseCsvData(csvData);
            countries.removeIf(country -> !inputCountries.contains(country.getName()));
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
        private List<String> countries = new ArrayList<>();
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

    private int getRandomAge(Country country, Optional<Integer> minAge, Optional<Integer> maxAge) {
        Random random = new Random();

        // Filter age groups based on optional minAge and maxAge
        List<AgeGroup> filteredAgeGroups = country.getAgeGroups().stream()
                .filter(ageGroup ->
                        (!minAge.isPresent() || ageGroup.getAge() >= minAge.get()) &&
                                (!maxAge.isPresent() || ageGroup.getAge() <= maxAge.get()))
                .toList();

        // Calculate the total population for the filtered age groups
        int totalPopulation = filteredAgeGroups.stream()
                .mapToInt(AgeGroup::getTotalPopulation)
                .sum();

        // If no age groups match the criteria, return a default value (e.g., 0 or throw an exception)
        if (totalPopulation == 0) {
            throw new IllegalArgumentException("No age groups match the specified criteria.");
        }

        // Generate a random value and find the corresponding age group
        int randomValue = random.nextInt(totalPopulation);
        int currentSum = 0;

        for (AgeGroup ageGroup : filteredAgeGroups) {
            currentSum += ageGroup.getTotalPopulation();
            if (randomValue < currentSum) {
                return ageGroup.getAge();
            }
        }

        return 0; // This line is technically unreachable but included for completeness
    }


    private Gender getRandomGender(AgeGroup ageGroup){
        if(gender != null)
            return gender;
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
        int age = getRandomAge(country, Optional.of(minAge), Optional.of(maxAge));
        patient.getAttributes().put("country", country.getName());
        patient.getAttributes().put("age",age);
        patient.getAttributes().put("gender", getRandomGender(country.getAgeGroups().stream().filter(group -> group.getAge() == age).findFirst().get()));
        return patient;
    }
}
