package org.example.modules.extensionModules;

import org.example.concepts.Module;
import org.example.concepts.attributes.Drug;
import org.example.concepts.attributes.DrugEvent;
import org.example.concepts.attributes.Patient;
import org.example.concepts.attributes.Snp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class ModuleDrugs extends Module {

    private final int maxDrugs;
    private final List<Snp> snps;
    private final int snpsPerDrugType;
    private final int percentageOfSnpsForDrugPerDrugType;
    private final double positiveResponseAnotherDrug;
    private final double negativeResponseAnotherDrug;

    // Protected constructor to enforce builder usage
    protected ModuleDrugs(Builder<?,?> builder) {
        super(builder); // Correctly passes the builder to the Module constructor
        this.snps = builder.snps;

        //Instantiate variables with default values if not provided with parameters
        this.maxDrugs = builder.maxDrugs;
        this.snpsPerDrugType = builder.snpsPerDrugType;
        this.percentageOfSnpsForDrugPerDrugType = builder.percentageOfSnpsForDrugPerDrugType;
        this.positiveResponseAnotherDrug = builder.positiveResponseAnotherDrug;
        this.negativeResponseAnotherDrug = builder.negativeResponseAnotherDrug;
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
            //chance of assigning more drugs based on the last drug event
            double chance = lastDrugEvent.isResponse() ? positiveResponseAnotherDrug : negativeResponseAnotherDrug;

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
        patient.getAttributes().put("drugEvents", getDrugEvents(patient, createDrugSnpMap(snps, snpsPerDrugType, percentageOfSnpsForDrugPerDrugType)));
        return patient;
    }

    public static abstract class Builder<T extends ModuleDrugs, B extends Builder<T, B>>
            extends Module.ModuleBuilder<T, B> {

        public Integer maxDrugs;
        public List<Snp> snps;
        public Integer snpsPerDrugType;
        public Integer percentageOfSnpsForDrugPerDrugType;
        public Double positiveResponseAnotherDrug;
        public Double negativeResponseAnotherDrug;

        public B setMaxDrugs(int maxDrugs) {
            this.maxDrugs = maxDrugs;
            return self();
        }

        public B setSnps(List<Snp> snps) {
            this.snps = snps;
            return self();
        }

        public B setPositiveResponseAnotherDrug(double positiveResponseAnotherDrug) {
            this.positiveResponseAnotherDrug = positiveResponseAnotherDrug;
            return self();
        }

        public B setNegativeResponseAnotherDrug(double negativeResponseAnotherDrug) {
            this.negativeResponseAnotherDrug = negativeResponseAnotherDrug;
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

