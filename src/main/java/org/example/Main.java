package org.example;

import org.example.concepts.*;
import org.example.concepts.Module;
import org.example.modules.baseModules.ModuleBaseAttributes;
import org.example.modules.extensionModules.Epilepsy.ModuleEpilepsy;
import org.example.modules.extensionModules.Epilepsy.ModuleAttributes;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        Parameters parameters = new Parameters(args);
        ModuleFactory moduleFactory = new ModuleFactory(parameters);
        List<Module> activeModules = moduleFactory.generateModules(new ArrayList<>(List.of(ModuleBaseAttributes.class, ModuleEpilepsy.class, ModuleAttributes.class)));
        PatientFactory patientFactory = new PatientFactory(activeModules);
        List<Patient> patients = patientFactory.generatePatients(parameters.patientData);

        // export the results in two separated CSV files
        exportPatientsSnpsToCsv(patients, "patients.csv");
        exportPatientsDrugEventsToCsv(patients, "patients_drug_events.csv");
        long endTime = System.currentTimeMillis();
        System.out.println("Execution time: " + (endTime - startTime) + " milliseconds");
    }

    private static void exportPatientsSnpsToCsv(List<Patient> patients, String filename) {
        try (FileWriter writer = new FileWriter(filename)) {
            // Define exact base attribute order
            List<String> baseAttributes = List.of("gender", "age", "country");

            // Extract all attributes except "drugEvents"
            Set<String> attributeKeys = new HashSet<>();
            for (Patient patient : patients) {
                for (String key : patient.getAttributes().keySet()) {
                    if (!key.equals("drugEvents")) { // Exclude "drugEvents"
                        attributeKeys.add(key);
                    }
                }
            }

            // Preserve order of base attributes, then append other attributes
            LinkedHashSet<String> orderedAttributes = new LinkedHashSet<>(baseAttributes);

            // Add remaining attributes in a sorted order (excluding already included base attributes)
            attributeKeys.stream()
                    .filter(attr -> !baseAttributes.contains(attr)) // Keep only unknown attributes
                    .sorted() // Sort alphabetically
                    .forEach(orderedAttributes::add); // Add to ordered set

            // Write CSV header
            writer.write("patient_id,snp_id,chromosome,position,reference,alternative,expression");
            for (String key : orderedAttributes) {
                writer.write("," + key);
            }
            writer.write("\n");

            // Iterate through patients
            for (Patient patient : patients) {
                String patientId = patient.getId();
                Map<String, Snp> snps = patient.getSnps();
                Map<String, Object> attributes = patient.getAttributes();

                // Iterate through SNPs (long format)
                for (Snp snp : snps.values()) {
                    writer.write(String.format("%s,%s,%d,%d,%s,%s,%s",
                            patientId,
                            snp.getRsId(),
                            snp.getChromosome(),
                            snp.getPosition(),
                            snp.getRef(),
                            snp.getAlt(),
                            snp.getExpression()
                    ));

                    // Write attribute values in the correct order
                    for (String key : orderedAttributes) {
                        writer.write("," + attributes.getOrDefault(key, ""));
                    }
                    writer.write("\n");
                }
            }
            System.out.println("Successfully written SNP data to " + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static void exportPatientsDrugEventsToCsv(List<Patient> patients, String filename) {
        try (FileWriter writer = new FileWriter(filename)) {
            // Write CSV header
            writer.write("patient_id,drug_name,response,mutation_rate,prescription_count,drug_family\n");

            for (Patient patient : patients) {
                String patientId = patient.getId();
                Object drugEventsObj = patient.getAttributes().get("drugEvents");

                if (drugEventsObj instanceof List<?>) {
                    List<?> drugEventsList = (List<?>) drugEventsObj;
                    for (Object obj : drugEventsList) {
                        if (obj instanceof DrugEvent) {
                            DrugEvent event = (DrugEvent) obj;
                            Drug drug = event.getDrug(); // Assuming Drug object exists in DrugEvent
                            writer.write(String.format("%s,%s,%b,%.4f,%d,%s\n",
                                    patientId,
                                    drug.getName(),
                                    event.isResponse(),
                                    event.getSnpDrugMutationRate(),
                                    drug.getPrescriptionFrequency(),
                                    String.join(" | ", drug.getFamily()) // Joining drug families with "|"
                            ));
                        }
                    }
                }
            }
            System.out.println("Successfully written drug event data to " + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}