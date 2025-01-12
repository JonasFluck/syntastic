package org.example.concepts;
import org.example.concepts.handlers.*;
import java.util.*;

public class Parameters {
    public int minAge;
    public int maxAge;
    public Gender gender;
    public List<Country> countryList = new ArrayList<>();

    // Map to store argument handlers
    private final List<ArgumentHandler> argumentHandlers = new ArrayList<>();

    public Parameters(String[] args) {
        // Register handlers
        registerHandlers();

        // Parse arguments
        for (String arg : args) {
            if (arg.startsWith("--")) {
                String[] keyValue = arg.substring(2).split("=", 2);
                if (keyValue.length == 2) {
                    String name = keyValue[0];
                    String value = keyValue[1];
                    boolean handled = false;

                    for (ArgumentHandler handler : argumentHandlers) {
                        try {
                            handler.handle(name, value, this);
                            handled = true;
                            break;
                        } catch (IllegalArgumentException e) {
                            // Continue to the next handler
                        }
                    }

                    if (!handled) {
                        System.out.println("Invalid argument: " + name);
                    }
                } else {
                    System.out.println("Invalid argument: " + arg);
                }
            }
        }
    }

    private void registerHandlers() {
        argumentHandlers.add(new BaseAttributeHandler());
        // Add more handlers as needed
    }
}
