package org.example;

import org.example.concepts.Module;
import org.example.concepts.Patient;
import org.example.concepts.Snp;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class PatientFactory {
    private List<Module> modules = new ArrayList<>();
    public PatientFactory(List<Module> modules){
        this.modules = modules;
    }

    public PatientFactory()
    {

    }

    public List<Patient> generatePatients(Map<String, Map<String,Snp>> snpData){
        List<CompletableFuture<Patient>> futureTasks = new ArrayList<>();
        for (Map.Entry<String, Map<String,Snp>> entry : snpData.entrySet()) {
            CompletableFuture<Patient> futureTask = CompletableFuture.supplyAsync(() -> {
                // Perform the task asynchronously for each entry
                String patientId = entry.getKey();
                Map<String,Snp> snps = entry.getValue();
                Patient patient = new Patient(patientId, snps);
                for(Module module : modules){
                    patient = module.processData(patient);
                }
                System.out.println("Patient " + patientId + " processing is done.");
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
