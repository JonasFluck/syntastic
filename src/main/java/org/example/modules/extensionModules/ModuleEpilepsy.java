package org.example.modules.extensionModules;

import org.example.concepts.*;
import org.example.concepts.Module;
import org.example.helper.TraitFileReader;

import java.util.Map;
import java.util.Random;

public class ModuleEpilepsy extends Module{
    private Trait epilepsyTrait;

    public ModuleEpilepsy() {
        System.out.println("Loading trait file...");
        epilepsyTrait = TraitFileReader.readTraitFromFile("traitFiles/Epilepsy.txt");
        if (epilepsyTrait == null) {
            System.err.println("Failed to load epilepsy trait data");
        }
        System.out.println("Loading trait file done");
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
