package ru.ulstu.method.exponential.parameter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ru.ulstu.datamodel.ts.TimeSeries;
import ru.ulstu.method.MethodParameter;

import java.util.ArrayList;
import java.util.List;

public abstract class ExponentialMethodParameter extends MethodParameter {
    public static final Float DEFAULT_OPTIMIZATION_STEP = 0.1f;
    public static final Float DEFAULT_MIN_VALUE = 0.1f;
    public static final Float DEFAULT_MAX_VALUE = 0.99f;
    private final Number minValue;
    private final Number maxValue;
    private final Number optimizationStep;

    public ExponentialMethodParameter(String name, Number minValue, Number maxValue, Number optimizationStep) {
        super(name);
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.optimizationStep = optimizationStep;
    }

    public Number getMinValue() {
        return minValue;
    }

    public Number getMaxValue() {
        return maxValue;
    }

    public Number getOptimizationStep() {
        return optimizationStep;
    }

    @Override
    @JsonIgnore
    public List<Number> getAvailableValues(TimeSeries timeSeries) {
        List<Number> values = new ArrayList<>();
        for (double i = minValue.doubleValue(); i <= maxValue.doubleValue(); i += optimizationStep.doubleValue()) {
            values.add(i);
        }
        return values;
    }

    @Override
    public String toString() {
        return "TimeSeriesMethodParam{" +
                "name='" + name + '\'' +
                ", minValue=" + minValue +
                ", maxValue=" + maxValue +
                ", delta=" + optimizationStep +
                '}';
    }
}
