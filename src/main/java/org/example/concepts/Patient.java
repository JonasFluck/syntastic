package org.example.concepts;

import java.util.*;

public class Patient {

    public Patient() {
    }
    public Patient(String id, Map<String, Snp> snps) {
        this.id = id;
        this.snps = snps;
    }

    private String id;
    private Map<String, Snp> snps;
    private Map<String, Object> attributes = new HashMap<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, Snp> getSnps() {
        return snps;
    }

    public void addSnp(Snp snp) {
        this.snps.put(snp.getRsId(), snp);
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }
}

