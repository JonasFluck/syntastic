package org.example.concepts.handlers;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.example.concepts.*;

public class BaseAttributeHandler implements ArgumentHandler {

    @Override
    public boolean handle(String name, String value, Parameters parameters) {
        switch (name) {
            case "countryList":
                parameters.countryList = handleCountry(value);
                return true;
            case "maxAge":
                parameters.maxAge = Integer.parseInt(value);
                return true;
            case "minAge":
                parameters.minAge = Integer.parseInt(value);
                return true;
            case "gender":
                parameters.gender = handleGender(value);
                return true;
            default:
                return false;
        }
    }

    private List<String> handleCountry(String value) {
        String[] countries = value.split(",");
        return Arrays.asList(countries);
    }

    private List<Gender> handleGender(String value) {
        String[] genders = value.split(",");
        try {
            // Convert each gender string to a Gender enum and return as a list
            return Arrays.stream(genders)
                    .map(String::trim) // Remove leading/trailing spaces
                    .map(Gender::valueOf)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid gender value(s): " + value, e);
        }
    }
}

