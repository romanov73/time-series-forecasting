package ru.ulstu.method.exponential.addtrendnoseason;

import ru.ulstu.datamodel.Model;
import ru.ulstu.datamodel.ts.TimeSeries;
import ru.ulstu.method.MethodParamValue;
import ru.ulstu.method.MethodParameter;
import ru.ulstu.method.exponential.parameter.Alpha;
import ru.ulstu.method.exponential.parameter.Beta;
import ru.ulstu.method.exponential.parameter.ExponentialMethodParamValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddTrendNoSeasonModel extends Model {
    private final ExponentialMethodParamValue<Alpha> alpha = new ExponentialMethodParamValue<>(Alpha.getInstance(), 0.5);
    private final ExponentialMethodParamValue<Beta> beta = new ExponentialMethodParamValue<>(Beta.getInstance(), 0.5);
    private final List<Double> smoothedComponent = new ArrayList<>();
    private final List<Double> trendComponent = new ArrayList<>();

    public AddTrendNoSeasonModel(TimeSeries ts, List<MethodParamValue> parameters) {
        super(ts);
        for (MethodParamValue parameter : parameters) {
            if (parameter.getParameter() instanceof Alpha) {
                alpha.setValue(parameter.getValue());
            }
            if (parameter.getParameter() instanceof Beta) {
                beta.setValue(parameter.getValue());
            }
        }
    }

    public List<Double> getSmoothedComponent() {
        return smoothedComponent;
    }

    public List<Double> getTrendComponent() {
        return trendComponent;
    }

    public ExponentialMethodParamValue<Alpha> getAlpha() {
        return alpha;
    }

    public ExponentialMethodParamValue<Beta> getBeta() {
        return beta;
    }

    public static List<MethodParameter> getAvailableParameters() {
        return Arrays.asList(Alpha.getInstance(), Beta.getInstance());
    }
}
