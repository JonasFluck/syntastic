package org.example.modules.extensionModules.Epilepsy;

import org.example.concepts.Drug;
import org.example.concepts.DrugEvent;
import org.example.concepts.Patient;
import org.example.concepts.Snp;
import org.example.helper.DrugLoader;
import org.example.modules.extensionModules.ModuleDrugs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ModuleDrugsEpilepsy extends ModuleDrugs {
    private final List<Drug> drugsInFrequency;

    public ModuleDrugsEpilepsy(Map<Drug, List<Snp>> drugSnpMap, int maxDrugs) {
        super(drugSnpMap, maxDrugs);
        List<Drug> drugs = drugSnpMap.keySet().stream().toList();
        this.drugsInFrequency = new ArrayList<>();
        for (Drug drug : drugs) {
            for (int i = 0; i < drug.getPrescriptionFrequency(); i++) {
                drugsInFrequency.add(drug);
            }
        }
    }

    @Override
    public DrugEvent createDrugEvent(List<DrugEvent> priorDrugEvents, Patient patient, Map<Drug, List<Snp>> drugSnpMap){
        DrugEvent drugEvent = new DrugEvent();
        if(priorDrugEvents.isEmpty()) {
            //First Drug is simply drawn randomly from the list of drugs
            Random random = new Random();
            Drug drug = drugsInFrequency.get(random.nextInt(drugsInFrequency.size()));
            drugEvent.setDrug(drug);
            drugEvent.setResponse(getResponse(drugEvent, patient, drugSnpMap));
        }
        else {
            List<Drug> notAlreadyPrescribedDrugs = new ArrayList<>(drugsInFrequency);
            notAlreadyPrescribedDrugs.removeAll(
                    priorDrugEvents.stream()
                            .map(DrugEvent::getDrug) // Extract drugs from priorDrugEvents
                            .toList()
            );
            Random random = new Random();
            Drug drug = notAlreadyPrescribedDrugs.get(random.nextInt(notAlreadyPrescribedDrugs.size()));
            drugEvent.setDrug(drug);
            drugEvent.setResponse(getResponse(drugEvent, patient, drugSnpMap));
        }
        return drugEvent;
    }

    private boolean getResponse(DrugEvent drugEvent, Patient patient,Map<Drug, List<Snp>> drugSnpMap){
        return false;
    }
}
