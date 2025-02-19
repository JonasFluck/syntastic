package org.example.concepts.attributes;

import java.util.*;

public class Trait {
    private String traitReported;
    private Map<String,Variant> variants;

    public Trait() {
        variants = new HashMap<>();
    }

    // Getters and setters
    public String getTraitReported() {
        return traitReported;
    }

    public void setTraitReported(String traitReported) {
        this.traitReported = traitReported;
    }

    public Map<String,Variant> getVariants() {
        return variants;
    }

    public void addVariant(String rsId, Variant variant) {
        this.variants.put(rsId,variant);
    }
}

