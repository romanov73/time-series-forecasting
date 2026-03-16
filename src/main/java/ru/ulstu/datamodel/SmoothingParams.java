package ru.ulstu.datamodel;

import jakarta.validation.constraints.NotNull;
import ru.ulstu.datamodel.ts.TimeSeries;

public class SmoothingParams {
    @NotNull
    private TimeSeries originalTimeSeries;
    private String methodClassName;

    public TimeSeries getOriginalTimeSeries() {
        return originalTimeSeries;
    }

    public void setOriginalTimeSeries(TimeSeries originalTimeSeries) {
        this.originalTimeSeries = originalTimeSeries;
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
                ", methodClassName=" + methodClassName +
                '}';
    }
}
