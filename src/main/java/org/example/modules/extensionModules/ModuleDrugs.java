package org.example.modules.extensionModules;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.example.concepts.Module;
import org.example.concepts.attributes.Drug;
import org.example.concepts.attributes.DrugEvent;
import org.example.concepts.attributes.Patient;
import org.example.concepts.attributes.Snp;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class ModuleDrugs extends Module {

    private final int maxDrugs;
    private final List<Snp> snps;
    private final int snpsPerDrugType;
    private final int percentageOfSnpsForDrugPerDrugType;

    // Protected constructor to enforce builder usage
    protected ModuleDrugs(Builder<?,?> builder) {
        super(builder); // Correctly passes the builder to the Module constructor

        this.snps = builder.snps;
        this.maxDrugs = builder.maxDrugs;
        this.snpsPerDrugType = builder.snpsPerDrugType;
        this.percentageOfSnpsForDrugPerDrugType = builder.percentageOfSnpsForDrugPerDrugType;
    }

    // Get drug events for a patient
    private List<DrugEvent> getDrugEvents(Patient patient, Map<Drug, List<Snp>> drugSnpMap) {
        List<DrugEvent> drugEvents = new ArrayList<>();
        return assignDrug(patient, drugEvents, 1, drugSnpMap);
    }

    // Recursive method to assign drugs
    private List<DrugEvent> assignDrug(Patient patient, List<DrugEvent> drugEvents, int drugEventCount,  Map<Drug, List<Snp>> drugSnpMap) {
        if (drugEvents.isEmpty()) {
            // Create the first drug event
            DrugEvent newDrugEvent = createDrugEvent(drugEvents, patient, drugSnpMap);
            newDrugEvent.setDrugEventCount(drugEventCount);
            drugEvents.add(newDrugEvent);
            return assignDrug(patient, drugEvents, drugEventCount + 1, drugSnpMap);
        } else {
            DrugEvent lastDrugEvent = drugEvents.get(drugEvents.size() - 1);
            double chance = lastDrugEvent.isResponse() ? 0.05 : 0.8;

            if (decideIfMoreDrugs(chance)) {
                if (drugEvents.size() < maxDrugs) {
                    // Create a new drug event
                    DrugEvent newDrugEvent = createDrugEvent(drugEvents, patient, drugSnpMap);
                    newDrugEvent.setDrugEventCount(drugEventCount);
                    drugEvents.add(newDrugEvent);
                    return assignDrug(patient, drugEvents, drugEventCount + 1, drugSnpMap);
                }
            }
            return drugEvents;
        }
    }

    // Abstract method to be implemented by subclasses
    public abstract DrugEvent createDrugEvent(
            List<DrugEvent> priorDrugEvents,
            Patient patient,
            Map<Drug, List<Snp>> drugSnpMap
    );

    // Determine if more drugs should be assigned
    private boolean decideIfMoreDrugs(double chance) {
        double randomValue = Math.random();
        return randomValue < chance;
    }

    public abstract  Map<Drug, List<Snp>> createDrugSnpMap(List<Snp> snps, int snpsPerDrugType, int percentageOfSnpsForDrugPerDrugType);

    // Override processData method
    @Override
    public Patient processData(Patient patient) {
        System.out.println("Processing patient data with ModuleDrugs");
        System.out.println("Size of snps: " + snps.size()); // Hier ist snps null aber warum ?
        patient.getAttributes().put("drugEvents", getDrugEvents(patient, createDrugSnpMap(snps, snpsPerDrugType, percentageOfSnpsForDrugPerDrugType)));
        return patient;
    }

    public static abstract class Builder<T extends ModuleDrugs, B extends Builder<T, B>>
            extends Module.ModuleBuilder<T, B> {

        public int maxDrugs;
        public List<Snp> snps;
        public int snpsPerDrugType;
        public int percentageOfSnpsForDrugPerDrugType;

        public B setMaxDrugs(int maxDrugs) {
            this.maxDrugs = maxDrugs;
            return self();
        }

        public B setSnps(List<Snp> snps) {
            this.snps = snps;
            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            try (FileWriter writer = new FileWriter("test.json")) {
                writer.write(gson.toJson(snps));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return self();
        }

        public B setSnpsPerDrugType(int snpsPerDrugType) {
            this.snpsPerDrugType = snpsPerDrugType;
            return self();
        }

        public B setPercentageOfSnpsForDrugPerDrugType(int percentageOfSnpsForDrugPerDrugType) {
            this.percentageOfSnpsForDrugPerDrugType = percentageOfSnpsForDrugPerDrugType;
            return self();
        }

        protected abstract B self(); // Subclasses must implement this to return the builder instance
    }
}

