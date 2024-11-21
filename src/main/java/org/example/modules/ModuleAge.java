package org.example.modules;

import org.example.concepts.Attribute;
import org.example.concepts.Module;
import org.example.concepts.Patient;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ModuleAge extends Module {
    @Override
    public Patient processData(Patient patient) {
        Map<String, Object> attributes = patient.getAttributes();
        Random random = new Random();
        attributes.put("age", random.nextInt(100));
        patient.setAttributes(attributes);
        return patient;
    }
}
