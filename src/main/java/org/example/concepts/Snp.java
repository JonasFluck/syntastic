package org.example.concepts;

import java.util.Objects;

public class Snp {
    public int getChromosome() {
        return chromosome;
    }

    public void setChromosome(int chromosome) {
        this.chromosome = chromosome;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public char getRef() {
        return ref;
    }

    public void setRef(char ref) {
        this.ref = ref;
    }

    public char getAlt() {
        return alt;
    }

    public void setAlt(char alt) {
        this.alt = alt;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    private int chromosome;
    private int position;
    private char ref;
    private char alt;
    private String expression;

    public String getRsId() {
        return rsId;
    }

    public void setRsId(String rsId) {
        this.rsId = rsId;
    }

    private String rsId;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true; // Check if same reference
        if (obj == null || getClass() != obj.getClass()) return false; // Ensure same type

        Snp snp = (Snp) obj;
        return chromosome == snp.chromosome &&
                position == snp.position &&
                ref == snp.ref &&
                alt == snp.alt &&
                Objects.equals(rsId, snp.rsId); // Include rsId for uniqueness
    }

    @Override
    public int hashCode() {
        return Objects.hash(chromosome, position, ref, alt, rsId);
    }
}
