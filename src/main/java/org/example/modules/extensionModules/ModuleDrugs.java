package org.example.modules.extensionModules;

import org.example.concepts.*;
import org.example.concepts.Module;
import org.example.modules.extensionModules.Epilepsy.ModuleDrugsEpilepsy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class ModuleDrugs extends Module {

    private final Map<Drug, List<Snp>> drugSnpMap;
    private final int maxDrugs;
    private final List<Snp> snps;

    // Protected constructor to enforce builder usage
    protected ModuleDrugs(Builder<?,?> builder) {
        super(builder); // Correctly passes the builder to the Module constructor
        this.snps = builder.snps;
        this.drugSnpMap = createDrugSnpMap(this.snps); // Ensure drugSnpMap is created using the same SNP list
        this.maxDrugs = builder.maxDrugs;
    }

    // Get drug events for a patient
    private List<DrugEvent> getDrugEvents(Patient patient) {
        List<DrugEvent> drugEvents = new ArrayList<>();
        return assignDrug(patient, drugEvents, 1);
    }

    // Recursive method to assign drugs
    private List<DrugEvent> assignDrug(Patient patient, List<DrugEvent> drugEvents, int drugEventCount) {
        if (drugEvents.isEmpty()) {
            // Create the first drug event
            DrugEvent newDrugEvent = createDrugEvent(drugEvents, patient, drugSnpMap);
            newDrugEvent.setDrugEventCount(drugEventCount);
            drugEvents.add(newDrugEvent);
            return assignDrug(patient, drugEvents, drugEventCount + 1);
        } else {
            DrugEvent lastDrugEvent = drugEvents.get(drugEvents.size() - 1);
            double chance = lastDrugEvent.isResponse() ? 0.05 : 0.8;

            if (decideIfMoreDrugs(chance)) {
                if (drugEvents.size() < maxDrugs) {
                    // Create a new drug event
                    DrugEvent newDrugEvent = createDrugEvent(drugEvents, patient, drugSnpMap);
                    newDrugEvent.setDrugEventCount(drugEventCount);
                    drugEvents.add(newDrugEvent);
                    return assignDrug(patient, drugEvents, drugEventCount + 1);
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

    public abstract Map<Drug, List<Snp>> createDrugSnpMap(List<Snp> snps);

    // Override processData method
    @Override
    public Patient processData(Patient patient) {
        patient.getAttributes().put("drugEvents", getDrugEvents(patient));
        return patient;
    }

    public static abstract class Builder<T extends ModuleDrugs, B extends Builder<T, B>>
            extends Module.ModuleBuilder<T, B> {

        public int maxDrugs;
        public List<Snp> snps;

        public B setMaxDrugs(int maxDrugs) {
            this.maxDrugs = maxDrugs;
            return self();
        }

        public B setSnps(List<Snp> snps) {
            this.snps = snps;
            return self();
        }

        @Override
        public abstract T build(); // Subclasses must implement this to return a concrete instance of ModuleDrugs

        @Override
        protected abstract B self(); // Subclasses must implement this to return the builder instance
    }
}

