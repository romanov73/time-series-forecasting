package ru.ulstu.method.fuzzy.parameter;

import ru.ulstu.datamodel.ts.TimeSeries;
import ru.ulstu.method.MethodParameter;

import java.util.ArrayList;
import java.util.List;

public class NumberOfFuzzyTerms extends MethodParameter {
    private final static int MIN_NUMBER_OF_FUZZY_TERMS = 2;
    private final static int MIN_INCREASING_STEP_OF_NUMBER_OF_FUZZY_TERMS = 1;
    private final static int MAX_NUMBER_OF_FUZZY_TERMS = 7;

    public NumberOfFuzzyTerms() {
        super("Number of fuzzy terms");
    }

    public static NumberOfFuzzyTerms getInstance() {
        return new NumberOfFuzzyTerms();
    }

    @Override
    public List<Number> getAvailableValues(TimeSeries timeSeries) {
        List<Number> values = new ArrayList<>();
        for (double i = MIN_NUMBER_OF_FUZZY_TERMS;
             i <= MAX_NUMBER_OF_FUZZY_TERMS;
             i += MIN_INCREASING_STEP_OF_NUMBER_OF_FUZZY_TERMS) {
            values.add(i);
        }
        return values;
    }
}
