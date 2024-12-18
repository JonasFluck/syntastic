package org.example.modules.extensionModules.Epilepsy;

import org.example.SnpLoader;
import org.example.concepts.*;
import org.example.concepts.Module;
import org.example.helper.DrugLoader;
import org.example.helper.TraitFileReader;

import java.util.*;
import java.util.stream.Collectors;

public class ModuleEpilepsy extends Module{
    private Trait epilepsyTrait;
    private Module moduleDrugsEpilepsy;
    private List<Snp> snps;

    public ModuleEpilepsy(List<Snp> snps){
        System.out.println("Loading trait file...");
        epilepsyTrait = TraitFileReader.readTraitFromFile("traitFiles/Epilepsy.txt");
        if (epilepsyTrait == null) {
            System.err.println("Failed to load epilepsy trait data");
        }
        System.out.println("Loading trait file done");
        this.snps = snps;
        this.moduleDrugsEpilepsy = new ModuleDrugsEpilepsy(loadDrugSnpMap(), 5);

    }

    private List<String> getDistinctFamilies(List<Drug> drugs) {
        Set<String> familySet = new HashSet<>();

        // Add all families from all drugs to the set
        for (Drug drug : drugs) {
            familySet.addAll(Arrays.asList(drug.getFamily()));
        }

        // Convert the set to a list (if a list is required)
        return new ArrayList<>(familySet);
    }

    private Map<Drug, List<Snp>> loadDrugSnpMap(){

        //First we assign random snps to each drug type
        Map<String, List<Snp>> drugTypesWithSnp = new HashMap<>();
        for(String drugType: getDistinctFamilies(getDrugs()))
            drugTypesWithSnp.put(drugType, new ArrayList<>());
        Random random = new Random();
        for(String key : drugTypesWithSnp.keySet()){
            int numberOfSnps = 100;
            List<Snp> shuffledList = new ArrayList<>(snps);
            Collections.shuffle(shuffledList, random);
            List<Snp> randomSnps = shuffledList.subList(0, Math.min(numberOfSnps, shuffledList.size()));
            drugTypesWithSnp.put(key, randomSnps);
        }
        //Now we assign the snps to the drugs according to the drug type
        Map<Drug, List<Snp>> drugSnpMap = new HashMap<>();
        for(Drug drug : getDrugs()){
            List<Snp> relevantSnps = new ArrayList<>();
            for(String family : drug.getFamily()){
                List<Snp> snps = drugTypesWithSnp.get(family);
                relevantSnps.addAll(drawPercentageOfSnps(snps, 80));
            }
        }
        return drugSnpMap;
    }

    private List<Snp> drawPercentageOfSnps(List<Snp> snps, int percentage) {
        if (snps.isEmpty()) {
            return Collections.emptyList();
        }

        // Shuffle the SNP list to randomize selection
        List<Snp> shuffledSnps = new ArrayList<>(snps);
        Collections.shuffle(shuffledSnps);

        // Calculate the number of SNPs to draw
        int drawCount = (int) Math.ceil((percentage / 100.0) * snps.size());

        // Return the first 'drawCount' SNPs
        return shuffledSnps.subList(0, Math.min(drawCount, shuffledSnps.size()));
    }

    private List<Drug> getDrugs() {
        return DrugLoader.loadDrugs("drugs.json");
    }

    @Override
    public Patient processData(Patient patient) {
        // Preload Variants into a Map for quick lookup
        Map<String, Variant> variantMap = epilepsyTrait.getVariants();

        double epilepsyRisk = 0;
        int count = 0;
        int total = patient.getSnps().size();

        // Iterate over SNPs and directly fetch corresponding Variant
        for (Map.Entry<String, Snp> snpEntry : patient.getSnps().entrySet()) {
            Variant variant = variantMap.get(snpEntry.getKey());
            if (variant != null) {
                epilepsyRisk += getEpilepsyRisk(snpEntry.getValue(), variant);
            }
            count++;
            System.out.println("Processed " + count + " out of " + total + " snps");
        }

        // Store the result in the patient's attributes
        patient.getAttributes().put("epilepsy", epilepsyRisk);
        // Add medication data
        moduleDrugsEpilepsy.processData(patient);
        return patient;
    }


    public double getEpilepsyRisk(Snp snp, Variant variant){
        char allele = snp.getRef();
        String expression = snp.getExpression();
        char alternative = snp.getAlt();
        switch (expression){
            case "0/0":
                //Two times major allele
                if(variant.getEffectAllele().equals(String.valueOf(allele))){
                    return variant.getEffectWeight() * 2;
                }
                break;
            case "0/1", "1/0":
                return variant.getEffectWeight();
            case "1/1":
                if(variant.getEffectAllele().equals(String.valueOf(alternative))){
                    return variant.getEffectWeight() * 2;
                }
                break;
        }
        return 0;
    }
}
