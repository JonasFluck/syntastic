package org.example.concepts;

public class Drug {
    private String name;
    private String [] family;
    private int prescriptionFrequency;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getFamily() {
        return family;
    }

    public void setFamily(String[] family) {
        this.family = family;
    }

    public int getPrescriptionFrequency() {
        return prescriptionFrequency;
    }

    public void setPrescriptionFrequency(int prescriptionFrequency) {
        this.prescriptionFrequency = prescriptionFrequency;
    }
}
