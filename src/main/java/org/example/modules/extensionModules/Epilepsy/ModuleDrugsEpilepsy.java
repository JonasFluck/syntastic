package org.example.modules.extensionModules.Epilepsy;

import org.example.concepts.*;
import org.example.helper.DrugLoader;
import org.example.modules.extensionModules.ModuleDrugs;

import java.util.*;
import java.util.stream.Collectors;

public class ModuleDrugsEpilepsy extends ModuleDrugs {

    private final List<Drug> drugsInFrequency;
    private final List<Snp> snps;

    // Constructor using the builder pattern
    public ModuleDrugsEpilepsy(Builder builder) {
        super(builder); // Handles shared variables in parent class
        this.snps = Objects.requireNonNull(builder.snps, "SNPs cannot be null");

        // Initialize drugsInFrequency with validated data
        this.drugsInFrequency = initializeDrugsInFrequency();
    }

    private List<Drug> initializeDrugsInFrequency() {
        List<Drug> frequencyList = new ArrayList<>();
        createDrugSnpMap(snps).keySet().forEach(drug -> {
            for (int i = 0; i < drug.getPrescriptionFrequency(); i++) {
                frequencyList.add(drug);
            }
        });
        return Collections.unmodifiableList(frequencyList); // Immutable for safety
    }

    @Override
    public Map<Drug, List<Snp>> createDrugSnpMap(List<Snp> snps) {
        // Ensure SNPs are used consistently when creating the drug-SNP map
        Map<String, List<Snp>> drugTypeSnpMap = new HashMap<>();
        for (String drugType : getDistinctDrugFamilies(getDrugs())) {
            drugTypeSnpMap.put(drugType, new ArrayList<>());
        }
        // Shuffling and adding SNPs from the provided snps list
        Random random = new Random();
        for (String drugType : drugTypeSnpMap.keySet()) {
            List<Snp> shuffledSnps = new ArrayList<>(snps);
            Collections.shuffle(shuffledSnps, random);
            drugTypeSnpMap.put(drugType, shuffledSnps.subList(0, Math.min(100, shuffledSnps.size())));
        }
        Map<Drug, List<Snp>> drugSnpMap = new HashMap<>();
        for (Drug drug : getDrugs()) {
            List<Snp> relevantSnps = new ArrayList<>();
            for (String family : drug.getFamily()) {
                relevantSnps.addAll(drawPercentageOfSnps(drugTypeSnpMap.get(family), 80));
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
        return DrugLoader.loadDrugs("drugs.json");
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
        double chance = 0.5; // Initial neutral chance

        for (DrugEvent event : priorDrugEvents) {
            boolean isPositiveResponse = event.isResponse();
            List<String> drugFamily = Arrays.asList(drug.getFamily());
            List<String> priorDrugFamily = Arrays.asList(event.getDrug().getFamily());

            // Adjust chance based on overlapping drug families
            for (String family : priorDrugFamily) {
                if (drugFamily.contains(family)) {
                    chance += isPositiveResponse ? 0.1 : -0.1;
                }
            }

            // Penalize further for negative responses
            if (!isPositiveResponse) {
                chance -= 0.1;
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

    // Builder implementation for ModuleDrugsEpilepsy
    public static class Builder extends ModuleDrugs.Builder<ModuleDrugsEpilepsy, Builder> {
        private List<Snp> snps = new ArrayList<>();

        @Override
        public ModuleDrugsEpilepsy build() {
            this.snps = super.snps;
            return new ModuleDrugsEpilepsy(this);
        }

        @Override
        protected Builder self() {
            return this;
        }
    }
}
