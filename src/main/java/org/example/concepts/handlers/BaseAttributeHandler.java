package org.example.concepts.handlers;

import java.util.List;
import org.example.concepts.*;

public class BaseAttributeHandler implements ArgumentHandler {

    @Override
    public void handle(String name, String value, Parameters parameters) {
        switch (name) {
            case "country":
                handleCountry(value, parameters.countryList);
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

    private void handleCountry(String value, List<Country> countryList) {
        String[] countries = value.split(",");
        for (String country : countries) {
            countryList.add(new Country(country));
        }
    }
}

