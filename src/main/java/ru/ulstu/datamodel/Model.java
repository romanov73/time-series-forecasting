package ru.ulstu.datamodel;

import ru.ulstu.datamodel.ts.TimeSeries;

public abstract class Model {
    protected final TimeSeries timeSeriesModel;

    protected Model(TimeSeries ts) {
        timeSeriesModel = new TimeSeries("Model of", ts.getKey());
    }

    public TimeSeries getTimeSeriesModel() {
        return timeSeriesModel;
    }
}
