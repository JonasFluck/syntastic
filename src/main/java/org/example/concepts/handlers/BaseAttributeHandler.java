package org.example.concepts.handlers;

import java.util.Arrays;
import java.util.List;
import org.example.concepts.*;

public class BaseAttributeHandler implements ArgumentHandler {

    @Override
    public void handle(String name, String value, Parameters parameters) {
        switch (name) {
            case "country":
                parameters.countryList = handleCountry(value);
                break;
            case "maxAge":
                parameters.maxAge = Integer.parseInt(value);
                break;
            case "minAge":
                parameters.minAge = Integer.parseInt(value);
                break;
            case "gender":
                parameters.gender = Gender.valueOf(value);
                break;
            default:
                System.out.println("Invalid argument: " + name);
        }
    }

    private List<String> handleCountry(String value) {
        String[] countries = value.split(",");
        return Arrays.asList(countries);
    }
}

