package org.example.concepts;

import java.util.List;

public class Patient {
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public List<Snp> getSnps() {
        return snps;
    }

    public void setSnps(List<Snp> snps) {
        this.snps = snps;
    }

    private String id;
    private String name;
    private int age;
    private List<Snp> snps;
}
