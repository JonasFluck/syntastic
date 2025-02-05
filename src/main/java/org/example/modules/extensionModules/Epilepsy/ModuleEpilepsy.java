package org.example.modules.extensionModules.Epilepsy;

import org.example.concepts.*;
import org.example.concepts.Module;
import org.example.helper.TraitFileReader;

import java.util.*;

public class ModuleEpilepsy extends Module {

    private final Trait epilepsyTrait;
    private final Module moduleDrugsEpilepsy;

    // Private constructor to enforce builder usage
    private ModuleEpilepsy(Builder builder) {
        super(builder); // Pass the builder to the parent class constructor
        this.epilepsyTrait = builder.epilepsyTrait;
        this.moduleDrugsEpilepsy = builder.moduleDrugsEpilepsy;
    }

    // Builder class extending Module.ModuleBuilder
    public static class Builder extends Module.ModuleBuilder<ModuleEpilepsy, Builder> {

        private Trait epilepsyTrait;
        private ModuleDrugsEpilepsy moduleDrugsEpilepsy;

        // Builder setter methods
        public Builder setEpilepsyTrait(String traitFilePath) {
            System.out.println("Loading trait file...");
            this.epilepsyTrait = TraitFileReader.readTraitFromFile(traitFilePath);
            if (this.epilepsyTrait == null) {
                throw new IllegalStateException("Failed to load epilepsy trait data from " + traitFilePath);
            }
            System.out.println("Loading trait file done");
            return this;
        }

        public Builder setModuleDrugsEpilepsy(ModuleDrugsEpilepsy moduleDrugsEpilepsy) {
            this.moduleDrugsEpilepsy = moduleDrugsEpilepsy;
            return this;
        }

        @Override
        protected Builder self() {
            return this;
        }

        @Override
        public ModuleEpilepsy build() {
            if (epilepsyTrait == null || moduleDrugsEpilepsy == null) {
                throw new IllegalStateException("All properties must be set before building ModuleEpilepsy");
            }
            return new ModuleEpilepsy(this);
        }
    }

    // Implementation of processData method
    @Override
    public Patient processData(Patient patient) {
        // Preload Variants for quick lookup
        Map<String, Variant> variantMap = epilepsyTrait.getVariants();

        double epilepsyRisk = 0;
        int processedCount = 0;
        int totalSnpCount = patient.getSnps().size();

        // Iterate through the patient's SNPs and calculate epilepsy risk
        for (Map.Entry<String, Snp> snpEntry : patient.getSnps().entrySet()) {
            Variant variant = variantMap.get(snpEntry.getKey());
            if (variant != null) {
                epilepsyRisk += calculateEpilepsyRisk(snpEntry.getValue(), variant);
            }
            processedCount++;
            //System.out.println("Processed " + processedCount + " out of " + totalSnpCount + " SNPs");
        }

        // Store the epilepsy risk in the patient's attributes
        patient.getAttributes().put("epilepsy", epilepsyRisk);

        // Add drug-related data using the ModuleDrugsEpilepsy module
        moduleDrugsEpilepsy.processData(patient);

        return patient;
    }

    private double calculateEpilepsyRisk(Snp snp, Variant variant) {
        char refAllele = snp.getRef();
        String genotype = snp.getExpression();
        char altAllele = snp.getAlt();

        switch (genotype) {
            case "0/0":
                // Two copies of the reference allele
                if (variant.getEffectAllele().equals(String.valueOf(refAllele))) {
                    return variant.getEffectWeight() * 2;
                }
                break;

            case "0/1":
            case "1/0":
                // One reference allele and one alternative allele
                return variant.getEffectWeight();

            case "1/1":
                // Two copies of the alternative allele
                if (variant.getEffectAllele().equals(String.valueOf(altAllele))) {
                    return variant.getEffectWeight() * 2;
                }
                break;
        }

        return 0;
    }
}
