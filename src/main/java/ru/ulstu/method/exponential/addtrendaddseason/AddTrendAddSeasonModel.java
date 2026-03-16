package ru.ulstu.method.exponential.addtrendaddseason;

import ru.ulstu.datamodel.Model;
import ru.ulstu.datamodel.ts.TimeSeries;
import ru.ulstu.method.MethodParamValue;
import ru.ulstu.method.MethodParameter;
import ru.ulstu.method.exponential.parameter.Alpha;
import ru.ulstu.method.exponential.parameter.Beta;
import ru.ulstu.method.exponential.parameter.ExponentialMethodParamValue;
import ru.ulstu.method.exponential.parameter.Gamma;
import ru.ulstu.method.exponential.parameter.Season;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddTrendAddSeasonModel extends Model {
    private final ExponentialMethodParamValue<Alpha> alpha = new ExponentialMethodParamValue<>(Alpha.getInstance(), 0.5);
    private final ExponentialMethodParamValue<Beta> beta = new ExponentialMethodParamValue<>(Beta.getInstance(), 0.5);
    private final ExponentialMethodParamValue<Gamma> gamma = new ExponentialMethodParamValue<>(Gamma.getInstance(), 0.5);
    private final ExponentialMethodParamValue<Season> season = new ExponentialMethodParamValue<>(Season.getInstance(), 12);
    private final List<Double> smoothedComponent = new ArrayList<>();
    private final List<Double> trendComponent = new ArrayList<>();
    private final List<Double> seasonComponent = new ArrayList<>();

    public AddTrendAddSeasonModel(TimeSeries ts, List<MethodParamValue> parameters) {
        super(ts);
        for (MethodParamValue parameter : parameters) {
            if (parameter.getParameter() instanceof Alpha) {
                alpha.setValue(parameter.getValue());
            }
            if (parameter.getParameter() instanceof Beta) {
                beta.setValue(parameter.getValue());
            }
            if (parameter.getParameter() instanceof Gamma) {
                gamma.setValue(parameter.getValue());
            }
            if (parameter.getParameter() instanceof Season) {
                season.setValue(parameter.getValue());
            }
        }
    }

    public List<Double> getSmoothedComponent() {
        return smoothedComponent;
    }

    public List<Double> getTrendComponent() {
        return trendComponent;
    }

    public List<Double> getSeasonComponent() {
        return seasonComponent;
    }

    public ExponentialMethodParamValue<Alpha> getAlpha() {
        return alpha;
    }

    public ExponentialMethodParamValue<Beta> getBeta() {
        return beta;
    }

    public ExponentialMethodParamValue<Gamma> getGamma() {
        return gamma;
    }

    public ExponentialMethodParamValue<Season> getSeason() {
        return season;
    }

    public static List<MethodParameter> getAvailableParameters() {
        return Arrays.asList(Alpha.getInstance(), Beta.getInstance(), Gamma.getInstance(), Season.getInstance());
    }
}
