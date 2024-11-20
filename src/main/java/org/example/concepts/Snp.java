package org.example.concepts;

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
}
