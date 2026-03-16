package ru.ulstu.method.fuzzy;

public class OutputValue {
    private String variable;
    private String fuzzyTerm;
    private Double degree;

    public OutputValue(String variable, String fuzzyTerm, Double degree) {
        this.variable = variable;
        this.fuzzyTerm = fuzzyTerm;
        this.degree = degree;
    }

    public String getFuzzyTerm() {
        return fuzzyTerm;
    }

    public void setFuzzyTerm(String fuzzyTerm) {
        this.fuzzyTerm = fuzzyTerm;
    }

    public Double getDegree() {
        return degree;
    }

    public void setDegree(Double degree) {
        this.degree = degree;
    }

    public String getVariable() {
        return variable;
    }

    public void setVariable(String variable) {
        this.variable = variable;
    }
}
