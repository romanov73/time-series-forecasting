package ru.ulstu.method.exponential.notrendnoseason;

import ru.ulstu.datamodel.Model;
import ru.ulstu.datamodel.ts.TimeSeries;
import ru.ulstu.method.MethodParamValue;
import ru.ulstu.method.MethodParameter;
import ru.ulstu.method.exponential.parameter.Alpha;
import ru.ulstu.method.exponential.parameter.ExponentialMethodParamValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NoTrendNoSeasonModel extends Model {
    private final ExponentialMethodParamValue<Alpha> alpha = new ExponentialMethodParamValue<>(Alpha.getInstance(), 0.5);
    private final List<Double> smoothedComponent = new ArrayList<>();

    public NoTrendNoSeasonModel(TimeSeries ts, List<MethodParamValue> parameters) {
        super(ts);
        for (MethodParamValue parameter : parameters) {
            if (parameter.getParameter() instanceof Alpha) {
                alpha.setValue(parameter.getValue());
            }
        }
    }

    public List<Double> getSmoothedComponent() {
        return smoothedComponent;
    }

    public ExponentialMethodParamValue<Alpha> getAlpha() {
        return alpha;
    }

    public static List<MethodParameter> getAvailableParameters() {
        return Collections.singletonList(Alpha.getInstance());
    }
}
