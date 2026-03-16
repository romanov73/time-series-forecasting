package ru.ulstu.method.ftransform.parameter;

import ru.ulstu.datamodel.ts.TimeSeries;
import ru.ulstu.method.MethodParameter;

import java.util.ArrayList;
import java.util.List;

public class NumberOfCoveredPoints extends MethodParameter {
    private final static int MIN_NUMBER_OF_COVERED_POINTS = 3;
    private final static int MIN_INCREASING_STEP_OF_NUMBER_OF_COVERED_POINTS = 2;

    public NumberOfCoveredPoints() {
        super("Number of covered points");
    }

    public static NumberOfCoveredPoints getInstance() {
        return new NumberOfCoveredPoints();
    }

    @Override
    public List<Number> getAvailableValues(TimeSeries timeSeries) {
        List<Number> values = new ArrayList<>();
        for (double i = MIN_NUMBER_OF_COVERED_POINTS; i <= (timeSeries.getLength() < 10 ? 7 : timeSeries.getLength() / 3.0); i += MIN_INCREASING_STEP_OF_NUMBER_OF_COVERED_POINTS) {
            values.add(i);
        }
        return values;
    }
}
