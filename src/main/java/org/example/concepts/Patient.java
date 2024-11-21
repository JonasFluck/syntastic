package org.example.concepts;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Patient {
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Snp> getSnps() {
        return snps;
    }

    public void setSnps(List<Snp> snps) {
        this.snps = snps;
    }
    private List<Snp> snps;

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    private Map<String, Object> attributes = new HashMap<>();
}
