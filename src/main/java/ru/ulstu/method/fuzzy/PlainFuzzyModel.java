package ru.ulstu.method.fuzzy;

import ru.ulstu.datamodel.Model;
import ru.ulstu.datamodel.ts.TimeSeries;
import ru.ulstu.method.MethodParamValue;
import ru.ulstu.method.MethodParameter;
import ru.ulstu.method.fuzzy.parameter.NumberOfFuzzyTerms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlainFuzzyModel extends Model {
    private final MethodParamValue numberOfFuzzyTerms = new MethodParamValue(NumberOfFuzzyTerms.getInstance(), 2);
    private final List<Triangle> fuzzyTimeSeries = new ArrayList<>();
    private List<Triangle> fuzzySets = new ArrayList<>();

    protected PlainFuzzyModel(TimeSeries ts, List<MethodParamValue> parameters) {
        super(ts);
        for (MethodParamValue parameter : parameters) {
            if (parameter.getParameter() instanceof NumberOfFuzzyTerms) {
                numberOfFuzzyTerms.setValue(parameter.getValue());
            }
        }
    }

    public static List<MethodParameter> getAvailableParameters() {
        return Collections.singletonList(NumberOfFuzzyTerms.getInstance());
    }

    public MethodParamValue getNumberOfFuzzyTerms() {
        return numberOfFuzzyTerms;
    }

    public List<Triangle> getFuzzyTimeSeries() {
        return fuzzyTimeSeries;
    }

    public void setFuzzySets(List<Triangle> fuzzySets) {
        this.fuzzySets = fuzzySets;
    }

    public List<Triangle> getFuzzySets() {
        return fuzzySets;
    }

    public double getTopValueByLabel(String label) {
        return fuzzySets
                .stream()
                .filter(fs -> fs.getLabel().equals(label))
                .findAny()
                .orElseThrow(() -> new RuntimeException("Неизвестная нечеткая метка"))
                .getTop();
    }
}
