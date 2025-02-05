package org.example.concepts.handlers;
import org.example.concepts.Parameters;

public interface ArgumentHandler {
    boolean handle(String name, String value, Parameters parameters);
}

