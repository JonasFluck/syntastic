package org.example.modules.extensionModules.Epilepsy;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.example.concepts.attributes.Drug;
import org.example.concepts.attributes.DrugEvent;
import org.example.concepts.attributes.Patient;
import org.example.concepts.attributes.Snp;
import org.example.helper.DrugLoader;
import org.example.modules.extensionModules.ModuleDrugs;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class ModuleDrugsEpilepsy extends ModuleDrugs {

    private final List<Drug> drugsInFrequency;
    private final List<Snp> snps;

    private final int snpsPerDrugType;
    private final int percentageOfSnpsForDrugPerDrugType;
    private final double baseDrugEffectiveness;
    private final double negativePriorDrugEvent;
    private final double positivePriorDrugEvent;
    private final  Map<Drug, List<Snp>> drugSnpMap;

    // Constructor using the builder pattern
    public ModuleDrugsEpilepsy(Builder builder) {
        super(builder); // Handles shared variables in parent class
        this.snps = Objects.requireNonNull(builder.snps, "SNPs cannot be null");
        this.snpsPerDrugType = builder.snpsPerDrugType;
        this.percentageOfSnpsForDrugPerDrugType = builder.percentageOfSnpsForDrugPerDrugType;
        this.baseDrugEffectiveness = builder.baseDrugEffectiveness;
        this.negativePriorDrugEvent = builder.negativePriorDrugEvent;
        this.positivePriorDrugEvent = builder.positivePriorDrugEvent;
        // Initialize drugsInFrequency with validated data
        this.drugSnpMap = createDrugSnpMap(snps, snpsPerDrugType, percentageOfSnpsForDrugPerDrugType);
        this.drugsInFrequency = initializeDrugsInFrequency(drugSnpMap);
        ExportDrugSnpMap(drugSnpMap,"drugSnpMap_Epilepsy.json");

    }

    private List<Drug> initializeDrugsInFrequency(Map<Drug, List<Snp>> drugSnpMap) {
        List<Drug> frequencyList = new ArrayList<>();
        drugSnpMap.keySet().forEach(drug -> {
            for (int i = 0; i < drug.getPrescriptionFrequency(); i++) {
                frequencyList.add(drug);
            }
        });
        return Collections.unmodifiableList(frequencyList); // Immutable for safety
    }

    @Override
    public Map<Drug, List<Snp>> createDrugSnpMap(List<Snp> snps, int snpsPerDrugType, int percentageOfSnpsForDrugPerDrugType) {
        // Ensure SNPs are used consistently when creating the drug-SNP map
        Map<String, List<Snp>> drugTypeSnpMap = new HashMap<>();
        for (String drugType : getDistinctDrugFamilies(getDrugs())) {
            drugTypeSnpMap.put(drugType, new ArrayList<>());
        }
        // Shuffling and adding SNPs from the provided snps list
        Random random = new Random();
        int i = 0;
        for (String drugType : drugTypeSnpMap.keySet()) {
            i++;
            if(snps == null) {
                System.out.println( i+" "+ drugType);
            }
            List<Snp> shuffledSnps = new ArrayList<>(snps);
            Collections.shuffle(shuffledSnps, random);
            drugTypeSnpMap.put(drugType, shuffledSnps.subList(0, Math.min(snpsPerDrugType, shuffledSnps.size())));
        }
        Map<Drug, List<Snp>> drugSnpMap = new HashMap<>();
        for (Drug drug : getDrugs()) {
            List<Snp> relevantSnps = new ArrayList<>();
            if(drug.getFamily() == null || drug.getFamily().length == 0) {
                List<Snp> allSnps = new ArrayList<>(snps);
                Collections.shuffle(allSnps);

                // Take a distinct set of random SNPs up to the 'variable' cap
                relevantSnps = allSnps.stream()
                        .distinct()  // Ensure distinct SNPs
                        .limit(percentageOfSnpsForDrugPerDrugType)  // Limit to the variable size
                        .collect(Collectors.toList()); // Collect into the list
            }
            else {
                int numberOfFamilies = drug.getFamily().length;
                for (String family : drug.getFamily()) {
                    relevantSnps.addAll(drawPercentageOfSnps(drugTypeSnpMap.get(family), percentageOfSnpsForDrugPerDrugType/numberOfFamilies));

                }
            }
            drugSnpMap.put(drug, relevantSnps);
        }
        return drugSnpMap;
    }


    private List<String> getDistinctDrugFamilies(List<Drug> drugs) {
        return drugs.stream()
                .flatMap(drug -> Arrays.stream(drug.getFamily()))
                .distinct()
                .toList();
    }

    private List<Snp> drawPercentageOfSnps(List<Snp> snps, int percentage) {
        if (snps == null || snps.isEmpty()) {
            return Collections.emptyList();
        }

        List<Snp> shuffledSnps = new ArrayList<>(snps);
        Collections.shuffle(shuffledSnps);

        int drawCount = (int) Math.ceil((percentage / 100.0) * snps.size());
        return shuffledSnps.subList(0, Math.min(drawCount, shuffledSnps.size()));
    }

    private List<Drug> getDrugs() {
        return DrugLoader.loadDrugs("config/drugs.json");
    }

    @Override
    public DrugEvent createDrugEvent(List<DrugEvent> priorDrugEvents, Patient patient, Map<Drug, List<Snp>> drugSnpMap) {
        Random random = new Random();
        DrugEvent drugEvent = new DrugEvent();

        // Ensure available drugs are calculated properly
        List<Drug> availableDrugs = new ArrayList<>(drugsInFrequency);
        if (!priorDrugEvents.isEmpty()) {
            availableDrugs.removeAll(priorDrugEvents.stream()
                    .map(DrugEvent::getDrug)
                    .toList());
        }

        Drug selectedDrug = availableDrugs.get(random.nextInt(availableDrugs.size()));
        drugEvent.setDrug(selectedDrug);
        drugEvent.setResponse(getResponse(priorDrugEvents, drugEvent, patient, drugSnpMap));

        return drugEvent;
    }

    private boolean getResponse(List<DrugEvent> priorDrugEvents, DrugEvent drugEvent, Patient patient, Map<Drug, List<Snp>> drugSnpMap) {
        Drug drug = drugEvent.getDrug();
        List<Snp> relevantSnps = drugSnpMap.getOrDefault(drug, Collections.emptyList());
        Set<String> relevantSnpIds = relevantSnps.stream().map(Snp::getRsId).collect(Collectors.toSet());

        double mutationRate = getMutationPercentage(patient, relevantSnps, relevantSnpIds);
        double variability = (new Random().nextDouble() - 0.5) * 0.2;
        double adjustedRate = Math.max(0, Math.min(1, mutationRate + variability));

        drugEvent.setSnpDrugMutationRate(adjustedRate);

        if (!priorDrugEvents.isEmpty() && !evaluatePriorDrugEvents(priorDrugEvents, drug)) {
            return false;
        }

        return new Random().nextDouble() > adjustedRate;
    }

    private boolean evaluatePriorDrugEvents(List<DrugEvent> priorDrugEvents, Drug drug) {
        double chance = baseDrugEffectiveness; // Initial neutral chance

        for (DrugEvent event : priorDrugEvents) {
            boolean isPositiveResponse = event.isResponse();
            List<String> drugFamily = Arrays.asList(drug.getFamily());
            List<String> priorDrugFamily = Arrays.asList(event.getDrug().getFamily());

            // Adjust chance based on overlapping drug families
            for (String family : priorDrugFamily) {
                if (drugFamily.contains(family)) {
                    chance += isPositiveResponse ? positivePriorDrugEvent : negativePriorDrugEvent;
                }
            }

            // Penalize further for negative responses
            if (!isPositiveResponse) {
                chance -= negativePriorDrugEvent;
            }
        }

        // Return true if the calculated chance indicates likelihood of success
        return new Random().nextDouble() < chance;
    }


    private double getMutationPercentage(Patient patient, List<Snp> relevantSnps, Set<String> relevantSnpIds) {
        long mutatedCount = patient.getSnps().entrySet().stream()
                .filter(entry -> relevantSnpIds.contains(entry.getKey()))
                .filter(entry -> {
                    String expression = entry.getValue().getExpression();
                    return expression.equals("0/1") || expression.equals("1/1");
                })
                .count();

        return (double) mutatedCount / relevantSnps.size();
    }

    private void ExportDrugSnpMap(Map<Drug, List<Snp>> drugSnpMap, String filename) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        // Convert Drug keys to a JSON-compatible format (String)
        Map<String, List<Snp>> jsonCompatibleMap = new HashMap<>();
        for (Map.Entry<Drug, List<Snp>> entry : drugSnpMap.entrySet()) {
            jsonCompatibleMap.put(entry.getKey().getName(), entry.getValue());
        }
        try (FileWriter writer = new FileWriter(filename)) {
            gson.toJson(jsonCompatibleMap, writer);
            System.out.println("Exported JSON successfully to: " + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Builder implementation for ModuleDrugsEpilepsy
    public static class Builder extends ModuleDrugs.Builder<ModuleDrugsEpilepsy, Builder> {
        private double baseDrugEffectiveness;
        private double negativePriorDrugEvent;
        private double positivePriorDrugEvent;

        @Override
        public ModuleDrugsEpilepsy build() {
            return new ModuleDrugsEpilepsy(this);
        }

        public Builder setBaseDrugEffectiveness(double baseDrugEffectiveness) {
            this.baseDrugEffectiveness = baseDrugEffectiveness;
            return self();
        }

        public Builder setNegativePriorDrugEvent(double negativePriorDrugEvent) {
            this.negativePriorDrugEvent = negativePriorDrugEvent;
            return self();
        }

        public Builder setPositivePriorDrugEvent(double positivePriorDrugEvent) {
            this.positivePriorDrugEvent = positivePriorDrugEvent;
            return self();
        }

        @Override
        protected Builder self() {
            return this;
        }
    }
}
