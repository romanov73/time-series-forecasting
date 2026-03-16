package ru.ulstu.method.fuzzy;

public class FuzzyRuleDataDto {
    private String[] fuzzyTerms;
    private int window = 3;
    private int horizon = 1;

    public FuzzyRuleDataDto(String[] fuzzyTimeSeries, int window, int horizon) {
        this.fuzzyTerms = fuzzyTimeSeries;
        this.window = window;
        this.horizon = horizon;
    }

    public String[] getFuzzyTerms() {
        return fuzzyTerms;
    }

    public void setFuzzyTerms(String[] fuzzyTerms) {
        this.fuzzyTerms = fuzzyTerms;
    }

    public int getWindow() {
        return window;
    }

    public void setWindow(int window) {
        this.window = window;
    }

    public int getHorizon() {
        return horizon;
    }
}
