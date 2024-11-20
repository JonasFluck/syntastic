package org.example;

import org.example.concepts.Patient;
import org.example.concepts.Snp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        SnpLoader loader = new SnpLoader();
        List<Patient> patients = new ArrayList<>();
        for (Map.Entry<String, List<Snp>> entry : loader.loadSnps("all_patients.json").entrySet()) {
            Patient patient = new Patient();
            patient.setId(entry.getKey());
            patient.setSnps(entry.getValue());
            patients.add(patient);
        }
    }
}