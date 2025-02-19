package org.example.concepts.attributes;

public class AgeGroup {
    public int getPopulationMale() {
        return populationMale;
    }

    public void setPopulationMale(int populationMale) {
        this.populationMale = populationMale;
    }

    public int getPopulationFemale() {
        return populationFemale;
    }

    public void setPopulationFemale(int populationFemale) {
        this.populationFemale = populationFemale;
    }

    private int populationMale;

    private int populationFemale;

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    private int age;


    public AgeGroup(int age) {
        this.age = age;
    }

    public int getTotalPopulation() {
        return populationMale + populationFemale;
    }
}
