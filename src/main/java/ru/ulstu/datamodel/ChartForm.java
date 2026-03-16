package ru.ulstu.datamodel;

import ru.ulstu.db.model.TimeSeriesMeta;
import ru.ulstu.db.model.TimeSeriesSet;

public class ChartForm {
    private TimeSeriesSet set;
    private TimeSeriesMeta timeSeriesMeta = null;
    private String methodClassName = null;

    private boolean needForecast;

    public TimeSeriesSet getSet() {
        return set;
    }

    public void setSet(TimeSeriesSet set) {
        this.set = set;
    }

    public TimeSeriesMeta getTimeSeriesMeta() {
        return timeSeriesMeta;
    }

    public void setTimeSeriesMeta(TimeSeriesMeta timeSeriesMeta) {
        this.timeSeriesMeta = timeSeriesMeta;
    }

    public String getMethodClassName() {
        return methodClassName;
    }

    public void setMethodClassName(String methodClassName) {
        this.methodClassName = methodClassName;
    }

    public boolean isNeedForecast() {
        return needForecast;
    }

    public void setNeedForecast(boolean needForecast) {
        this.needForecast = needForecast;
    }
}
