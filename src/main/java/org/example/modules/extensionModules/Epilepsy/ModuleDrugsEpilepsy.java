package org.example.modules.extensionModules.Epilepsy;

import org.example.concepts.Drug;
import org.example.concepts.DrugEvent;
import org.example.concepts.Patient;
import org.example.concepts.Snp;
import org.example.modules.extensionModules.ModuleDrugs;

import java.util.*;
import java.util.stream.Collectors;

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

    /**
     * Determines the likelihood of a positive response to a drug based on the patient's SNP profile.
     * The response is influenced by the presence of mutations in relevant SNPs, and a level of uncertainty is added.
     *
     * @param drugEvent The drug event for which the response is being calculated.
     * @param patient The patient whose SNPs are being evaluated.
     * @param drugSnpMap A map of drugs to their relevant SNPs.
     * @return boolean True if the drug has a positive response for the patient, false otherwise.
     */
    private boolean getResponse(DrugEvent drugEvent, Patient patient, Map<Drug, List<Snp>> drugSnpMap) {
        boolean drugResponse = false;
        // Get the list of relevant SNPs for the given drug
        Drug drug = drugEvent.getDrug();
        List<Snp> relevantDrugSnpList = drugSnpMap.get(drug);
        Set<String> relevantDrugSnpSet = relevantDrugSnpList.stream().map(Snp::getRsId).collect(Collectors.toSet());

        double mutationPercentage = getMutationPercentage(patient, relevantDrugSnpList, relevantDrugSnpSet);

        // Add variability (like + or - 10%) to the mutation score for uncertainty
        Random random = new Random();
        double variability = (random.nextDouble() - 0.5) * 0.2; // 0 - 10% variability
        double adjustedMutationPercentage = mutationPercentage + variability;

        // Ensure the adjusted mutation percentage is between 0 and 1
        adjustedMutationPercentage = Math.max(0, Math.min(1, adjustedMutationPercentage));
        System.out.println(adjustedMutationPercentage);
        drugEvent.setSnpDrugMutationRate(adjustedMutationPercentage);
        // Randomly determine the drug response based on the adjusted mutation percentage
        // Likelihood to be randomly greater than adjustedMutationPercentage is:
        // high for a small adjustedMutationPercentage -> drug works -> return true
        // low for a high adjustedMutationPercentage -> drug does not work -> return false

        if (adjustedMutationPercentage < random.nextDouble()) {
            drugResponse = true;
        }
        /*
        // Other approach with a hard threshold of 0.25
        if(adjustedMutationPercentage < 0.25){
            drugResponse = true;
        }
         */

        return drugResponse;
    }

    private static double getMutationPercentage(Patient patient, List<Snp> relevantDrugSnpList, Set<String> relevantDrugSnpSet) {
        int mutatedCount = 0;  // Counter for mutated SNPs
        int totalRelevantSNPs = relevantDrugSnpList.size(); // Total number of relevant SNPs for the drug

        // Iterate through the patient's SNPs and count mutations in the relevant ones
        for (Map.Entry<String, Snp> patientSnpEntry : patient.getSnps().entrySet()) {
            String rsIdPatientSnp = patientSnpEntry.getKey();
            Snp patientSnp = patientSnpEntry.getValue();

            // Check if the SNP is relevant for the drug
            if (relevantDrugSnpSet.contains(rsIdPatientSnp)) {
                String expression = patientSnp.getExpression();

                // Evaluate the SNP mutation based on the patient's genotype
                switch (expression) {
                    case "0/0": // No mutation
                        break;
                    case "0/1":
                    case "1/1": // Mutation present
                        mutatedCount++;
                        break;
                }
            }
        }
        // Calculate the mutation percentage for the relevant SNPs
        return (double) mutatedCount / totalRelevantSNPs;
    }

}
