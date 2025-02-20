package org.example.concepts.attributes;

public class Variant {
    private String rsID;
    private String chrName;
    private int chrPosition;
    private String effectAllele;
    private String otherAllele;
    private double effectWeight;

    public Variant(String rsID, String chrName, int chrPosition, String effectAllele, String otherAllele, double effectWeight) {
        this.rsID = rsID;
        this.chrName = chrName;
        this.chrPosition = chrPosition;
        this.effectAllele = effectAllele;
        this.otherAllele = otherAllele;
        this.effectWeight = effectWeight;
    }

    // Getters and setters
    public String getRsID() {
        return rsID;
    }

    public String getChrName() {
        return chrName;
    }

    public int getChrPosition() {
        return chrPosition;
    }

    public String getEffectAllele() {
        return effectAllele;
    }

    public String getOtherAllele() {
        return otherAllele;
    }

    public double getEffectWeight() {
        return effectWeight;
    }
}
