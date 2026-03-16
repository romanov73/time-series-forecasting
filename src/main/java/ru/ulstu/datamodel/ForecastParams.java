package ru.ulstu.datamodel;

import jakarta.validation.constraints.NotNull;
import ru.ulstu.datamodel.ts.TimeSeries;

public class ForecastParams {
    @NotNull
    private TimeSeries originalTimeSeries;
    @NotNull
    private int countForecast;
    private String methodClassName;

    public TimeSeries getOriginalTimeSeries() {
        return originalTimeSeries;
    }

    public void setOriginalTimeSeries(TimeSeries originalTimeSeries) {
        this.originalTimeSeries = originalTimeSeries;
    }

    public int getCountForecast() {
        return countForecast;
    }

    public void setCountForecast(int countForecast) {
        this.countForecast = countForecast;
    }

    public String getMethodClassName() {
        return methodClassName;
    }

    public void setMethodClassName(String methodClassName) {
        this.methodClassName = methodClassName;
    }

    @Override
    public String toString() {
        return "ForecastParams{" +
                "originalTimeSeries=" + originalTimeSeries +
                ", countForecast=" + countForecast +
                '}';
    }
}
