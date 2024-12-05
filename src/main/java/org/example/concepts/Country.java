package org.example.concepts;

import java.util.ArrayList;
import java.util.List;

public class Country {
    public Country(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    private String name;

    public List<AgeGroup> getAgeGroups() {
        return ageGroups;
    }

    public void setAgeGroups(List<AgeGroup> ageGroups) {
        this.ageGroups = ageGroups;
    }

    public int getPopulation(){
        return ageGroups.stream().mapToInt(AgeGroup::getTotalPopulation).sum();
    }

    private List<AgeGroup> ageGroups = new ArrayList<>();
}


