package org.example.modules.extensionModules;

import org.example.concepts.Drug;
import org.example.concepts.DrugEvent;
import org.example.concepts.Patient;
import org.example.helper.DrugLoader;
import org.example.concepts.Module;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ModuleDrugs extends Module{
    private List<Drug> drugs;
    public ModuleDrugs() {
        List<Drug> drugs = DrugLoader.loadDrugs("drugs.json");
        this.drugs = new ArrayList<>();
        for (Drug drug : drugs) {
            for (int i = 0; i < drug.getPrescriptionFrequency(); i++) {
                this.drugs.add(drug);
            }
        }
        System.out.println("Loaded " + this.drugs.size() + " drugs");
    }

    @Override
    public Patient processData(Patient patient) {

        List<DrugEvent> drugEvents = new ArrayList<>();
        for(int i = 0; i < 5; i++){
            DrugEvent drugEvent = new DrugEvent();
            Random random = new Random();
            Drug drug = drugs.get(random.nextInt(drugs.size()));
            drugEvent.setDrug(drug);
            drugEvents.add(drugEvent);
        }
        patient.getAttributes().put("drugEvents", drugEvents);
        return patient;
    }
}
