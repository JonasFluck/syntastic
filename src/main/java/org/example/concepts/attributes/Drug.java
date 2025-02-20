package org.example.concepts.attributes;

import java.util.Arrays;
import java.util.Objects;

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
    // Override equals
    @Override
    public boolean equals(Object obj) {
        // Check for self-comparison
        if (this == obj) {
            return true;
        }

        // Check if obj is of the correct type
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        // Cast the object to Drug and compare fields
        Drug otherDrug = (Drug) obj;
        return Objects.equals(this.name, otherDrug.name) &&
                Arrays.equals(this.family, otherDrug.family);
    }

    // Override hashCode
    @Override
    public int hashCode() {
        return Objects.hash(name, Arrays.hashCode(family));
    }
}
