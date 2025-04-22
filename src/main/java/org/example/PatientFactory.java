package org.example;

import org.example.concepts.Module;
import org.example.concepts.attributes.Patient;
import org.example.concepts.attributes.Snp;
import java.util.concurrent.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class PatientFactory {
    private final List<Module> modules;

    public PatientFactory(List<Module> modules){
        this.modules = modules;
    }

    public List<Patient> generatePatients(Map<String, Map<String, Snp>> snpData) {
        List<CompletableFuture<Patient>> futureTasks = new ArrayList<>();
        Map<String, Integer> progressMap = new ConcurrentHashMap<>();

        for (Map.Entry<String, Map<String, Snp>> entry : snpData.entrySet()) {
            String patientId = entry.getKey();
            progressMap.put(patientId, 0); // Initialize progress

            CompletableFuture<Patient> futureTask = CompletableFuture.supplyAsync(() -> {
                Map<String, Snp> snps = entry.getValue();
                Patient patient = new Patient(patientId, snps);

                for (int i = 0; i < modules.size(); i++) {
                    Module module = modules.get(i);
                    patient = module.processData(patient);

                    // Update progress
                    progressMap.put(patientId, i + 1);
                    printProgress(progressMap, modules.size());
                }

                return patient;
            });

            futureTasks.add(futureTask);
        }

        return futureTasks.stream()
                .map(CompletableFuture::join) // Wait for each task to complete
                .collect(Collectors.toList());
    }

    private synchronized void printProgress(Map<String, Integer> progressMap, int totalModules) {
        for (Map.Entry<String, Integer> entry : progressMap.entrySet()) {
            String patientId = entry.getKey();
            int currentModule = entry.getValue();
            System.out.println(String.format("Patient %s is at module (%d of %d)", patientId, currentModule, totalModules));
        }
    }
}
