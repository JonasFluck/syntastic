package org.example;

import org.example.concepts.Module;
import org.example.concepts.Patient;
import org.example.concepts.Snp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class Generator {
    private List<Module> modules = new ArrayList<>();
    public Generator(List<Module> modules){
        this.modules = modules;
    }

    public Generator(){}

    public List<Patient> generatePatients(Map<String, List<Snp>> snpData){
        List<CompletableFuture<Patient>> futureTasks = new ArrayList<>();
        for (Map.Entry<String, List<Snp>> entry : snpData.entrySet()) {
            CompletableFuture<Patient> futureTask = CompletableFuture.supplyAsync(() -> {
                // Perform the task asynchronously for each entry
                String patientId = entry.getKey();
                List<Snp> snps = entry.getValue();
                Patient patient = new Patient();
                patient.setId(patientId);
                patient.setSnps(snps);
                for(Module module : modules){
                    patient = module.processData(patient);
                }
                return patient;
            });

            futureTasks.add(futureTask);
        }

        // Wait for all tasks to complete and collect the results
        return futureTasks.stream()
                .map(CompletableFuture::join) // Wait for each task to complete
                .collect(Collectors.toList());

        }
    }
