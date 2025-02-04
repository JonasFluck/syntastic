package org.example.modules.baseModules;

import com.opencsv.exceptions.CsvValidationException;
import org.example.concepts.*;
import org.example.concepts.Module;
import org.example.helper.CsvLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ModuleBaseAttributes extends Module {

    private int minAge;
    private int maxAge;
    private final List<Gender> genders;
    private final List<Country> countries;
    private final List<String[]> csvData;
    private final int totalPopulation;

    // Private constructor to enforce builder usage
    private ModuleBaseAttributes(Builder builder) {
        super(builder); // Pass the builder to the parent class constructor
        this.minAge = builder.minAge;
        this.maxAge = builder.maxAge;
        this.genders = builder.genders;

        List<String> inputCountries = builder.countries;

        try {
            this.csvData = CsvLoader.readCSVFromResources("config/euro_pop.csv");
            csvData.remove(0); // Remove the header row
            countries = parseCsvData(csvData);
            if(inputCountries != null)
                countries.removeIf(country -> !inputCountries.contains(country.getName()));
            totalPopulation = countries.stream().mapToInt(Country::getPopulation).sum();
        } catch (IOException | CsvValidationException e) {
            throw new RuntimeException("Failed to load CSV data", e);
        }
    }

    // Builder class extending Module.ModuleBuilder
    public static class Builder extends Module.ModuleBuilder<ModuleBaseAttributes, Builder> {

        private int minAge; // Default value
        private int maxAge; // Default value
        private List<String> countries = new ArrayList<>();
        private List<Gender> genders = new ArrayList<>();

        // Builder setter methods
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

        public Builder setGenders(List<Gender> genders) {
            this.genders = genders;
            return this;
        }

        @Override
        protected Builder self() {
            return this;
        }

        @Override
        public ModuleBaseAttributes build() {
            return new ModuleBaseAttributes(this);
        }
    }

    // Parsing CSV data
    private List<Country> parseCsvData(List<String[]> input) {
        List<Country> countries = new ArrayList<>();
        for (String[] row : input) {
            String name = row[6];
            countries.stream()
                    .filter(country -> country.getName().equals(name))
                    .findFirst()
                    .ifPresent(country -> {
                        country.getAgeGroups().stream()
                                .filter(group -> group.getAge() == getAge(row[4]))
                                .findFirst()
                                .ifPresentOrElse(group -> {
                                    // Age group already exists
                                    if ("Female".equals(row[5])) {
                                        group.setPopulationFemale(Integer.parseInt(row[8]));
                                    } else {
                                        group.setPopulationMale(Integer.parseInt(row[8]));
                                    }
                                }, () -> {
                                    // Add new age group
                                    AgeGroup group = new AgeGroup(getAge(row[4]));
                                    if ("Female".equals(row[5])) {
                                        group.setPopulationFemale(Integer.parseInt(row[8]));
                                    } else {
                                        group.setPopulationMale(Integer.parseInt(row[8]));
                                    }
                                    country.getAgeGroups().add(group);
                                });
                    });

            // Add a new country if it does not exist
            if (countries.stream().noneMatch(country -> country.getName().equals(name))) {
                Country country = new Country(name);
                AgeGroup group = new AgeGroup(getAge(row[4]));
                if ("Female".equals(row[5])) {
                    group.setPopulationFemale(Integer.parseInt(row[8]));
                } else {
                    group.setPopulationMale(Integer.parseInt(row[8]));
                }
                country.getAgeGroups().add(group);
                countries.add(country);
            }
        }
        return countries;
    }

    private int getAge(String input) {
        if ("Open-ended age class".equals(input)) {
            return 100;
        }
        if ("Less than 1 year".equals(input)) {
            return 0;
        }
        String numericPart = input.replaceAll("\\D", "");
        try {
            return Integer.parseInt(numericPart);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid age format: " + input, e);
        }
    }

    // Method to select a random country
    private Country getRandomCountry() {
        Random random = new Random();
        int randomValue = random.nextInt(totalPopulation);
        int currentSum = 0;
        for (Country country : countries) {
            currentSum += country.getPopulation();
            if (randomValue < currentSum) {
                return country;
            }
        }
        throw new IllegalStateException("Failed to select a random country");
    }

    // Method to select a random age within a country's age groups
    private int getRandomAge(Country country, int minAge, int maxAge) {
        Random random = new Random();

        List<AgeGroup> filteredAgeGroups = country.getAgeGroups().stream()
                .filter(ageGroup ->
                        (minAge == -1 || ageGroup.getAge() >= minAge) &&
                                (maxAge == -1 || ageGroup.getAge() <= maxAge))
                .toList();

        int totalPopulation = filteredAgeGroups.stream()
                .mapToInt(AgeGroup::getTotalPopulation)
                .sum();

        if (totalPopulation == 0) {
            throw new IllegalArgumentException("No age groups match the specified criteria.");
        }

        int randomValue = random.nextInt(totalPopulation);
        int currentSum = 0;

        for (AgeGroup ageGroup : filteredAgeGroups) {
            currentSum += ageGroup.getTotalPopulation();
            if (randomValue < currentSum) {
                return ageGroup.getAge();
            }
        }

        throw new IllegalStateException("Failed to select a random age group");
    }

    private Gender getRandomGender(AgeGroup ageGroup) {
        if (genders != null && !genders.isEmpty()) {
            Random random = new Random();
            return genders.get(random.nextInt(genders.size())); // Randomly select from the provided list
        }

        // Default behavior: Randomly choose based on the population distribution
        Random random = new Random();
        int randomValue = random.nextInt(ageGroup.getTotalPopulation());
        return randomValue < ageGroup.getPopulationMale() ? Gender.Male : Gender.Female;
    }


    // Implementation of processData method
    @Override
    public Patient processData(Patient patient) {
        Country country = getRandomCountry();
        int age = getRandomAge(country, minAge, maxAge);
        Gender gender = getRandomGender(
                country.getAgeGroups().stream()
                        .filter(group -> group.getAge() == age)
                        .findFirst()
                        .orElseThrow(() -> new IllegalStateException("Age group not found"))
        );

        patient.getAttributes().put("country", country.getName());
        patient.getAttributes().put("age", age);
        patient.getAttributes().put("gender", gender);
        return patient;
    }
}
