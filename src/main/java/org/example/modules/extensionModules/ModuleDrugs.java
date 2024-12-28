package org.example.modules.extensionModules;

import org.example.concepts.*;
import org.example.concepts.Module;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class ModuleDrugs extends Module {

    private final Map<Drug, List<Snp>> drugSnpMap;
    private final int maxDrugs;
    public ModuleDrugs(Map<Drug, List<Snp>> drugSnpMap, int maxDrugs) {
        this.drugSnpMap = drugSnpMap;
        this.maxDrugs = maxDrugs;
    }


    private List<DrugEvent> getDrugEvents(Patient patient){
        List<DrugEvent> drugEvents= new ArrayList<>();
        return assignDrug(patient, drugEvents, 1);
    }

    private List<DrugEvent> assignDrug(Patient patient, List<DrugEvent> drugEvents, int drugEventCount){
        if(drugEvents.isEmpty()){
            // create a new drug event
            DrugEvent newDrugEvent = createDrugEvent(drugEvents, patient, drugSnpMap);
            newDrugEvent.setDrugEventCount(drugEventCount);
            drugEvents.add(newDrugEvent);
            return assignDrug(patient, drugEvents, drugEventCount+1);
        }
        else {
            DrugEvent lastDrugEvent = drugEvents.getLast();
            double chance = lastDrugEvent.isResponse() ? 0.05 : 0.8;
            if (decideIfMoreDrugs(chance)) {
                if (drugEvents.size() < maxDrugs) {
                    // create a new drug event
                    DrugEvent newDrugEvent = createDrugEvent(drugEvents, patient, drugSnpMap);
                    newDrugEvent.setDrugEventCount(drugEventCount);
                    drugEvents.add(newDrugEvent);
                    return assignDrug(patient, drugEvents, drugEventCount + 1);
                }
            }
            return drugEvents;
        }
    }

    public DrugEvent createDrugEvent(List<DrugEvent> priorDrugEvents, Patient patient, Map<Drug, List<Snp>> drugSnpMap){
        return null;
    }

    private boolean decideIfMoreDrugs(double chance){
        double randomValue = Math.random();
        return randomValue < chance;
    }

    @Override
    public Patient processData(Patient patient) {
        patient.getAttributes().put("drugEvents", getDrugEvents(patient));
        return patient;
    }
}
