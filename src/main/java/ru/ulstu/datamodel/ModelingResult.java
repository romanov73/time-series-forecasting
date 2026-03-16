package ru.ulstu.datamodel;

import ru.ulstu.datamodel.ts.TimeSeries;
import ru.ulstu.method.Method;
import ru.ulstu.method.MethodParamValue;

import java.util.List;

public class ModelingResult {
    private final TimeSeries timeSeries;
    private final TimeSeries testForecast;
    private final List<MethodParamValue> paramValues;
    private final Score score;
    private final Method method;

    public ModelingResult(TimeSeries timeSeries,
                          TimeSeries testForecast,
                          List<MethodParamValue> paramValues,
                          Score score,
                          Method method) {
        this.timeSeries = timeSeries;
        this.testForecast = testForecast;
        this.paramValues = paramValues;
        this.score = score;
        this.method = method;
    }

    public TimeSeries getTimeSeries() {
        return timeSeries;
    }

    public List<MethodParamValue> getParamValues() {
        return paramValues;
    }

    public Score getScore() {
        return score;
    }

    public Method getTimeSeriesMethod() {
        return method;
    }

    public TimeSeries getTestForecast() {
        return testForecast;
    }
}
