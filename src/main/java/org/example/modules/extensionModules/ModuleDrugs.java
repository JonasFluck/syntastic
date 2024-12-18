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
        return assignDrug(patient, drugEvents);
    }

    private List<DrugEvent> assignDrug(Patient patient, List<DrugEvent> drugEvents){
        if(drugEvents.isEmpty()){
            drugEvents.add(createDrugEvent(drugEvents, patient, drugSnpMap));
            return assignDrug(patient, drugEvents);
        }
        else {
            DrugEvent lastDrugEvent = drugEvents.getLast();
            if(lastDrugEvent.isResponse() ? decideIfMoreDrugs(0.1) : decideIfMoreDrugs(0.8)){
                if(drugEvents.size() < maxDrugs){
                    drugEvents.add(createDrugEvent(drugEvents, patient, drugSnpMap));
                    return assignDrug(patient, drugEvents);
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
