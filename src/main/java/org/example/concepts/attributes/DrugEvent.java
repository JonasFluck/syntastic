package org.example.concepts.attributes;

public class DrugEvent {
    public Drug getDrug() {
        return drug;
    }

    public void setDrug(Drug drug) {
        this.drug = drug;
    }

    public boolean isResponse() {
        return response;
    }

    public void setResponse(boolean response) {
        this.response = response;
    }

    public void setSnpDrugMutationRate(double snpDrugMutationRate) {
        this.snpDrugMutationRate = snpDrugMutationRate;
    }

    public double getSnpDrugMutationRate(){
        return snpDrugMutationRate;
    }

    public int getDrugEventCount() {
        return drugEventCount;
    }

    public void setDrugEventCount(int drugEventCount) {
        this.drugEventCount = drugEventCount;
    }

    private Drug drug;
    private boolean response;
    private double snpDrugMutationRate;
    private int drugEventCount;


}
