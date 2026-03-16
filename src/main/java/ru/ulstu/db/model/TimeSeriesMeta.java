package ru.ulstu.db.model;

import ru.ulstu.datamodel.ts.TimeSeries;

public class TimeSeriesMeta implements Comparable<TimeSeriesMeta> {
    private String key;
    private int size;
    private boolean hasDateTime;

    public TimeSeriesMeta() {
    }

    public TimeSeriesMeta(String key) {
        this.key = key;
    }

    public TimeSeriesMeta(TimeSeries timeSeries) {
        this.key = timeSeries.getKey();
        this.hasDateTime = timeSeries.getValues().stream().anyMatch(v -> v.getDate() != null);
        this.size = timeSeries.getLength();
    }

    public String getKey() {
        return key;
    }

    public int getSize() {
        return size;
    }

    public boolean isHasDateTime() {
        return hasDateTime;
    }

    @Override
    public int compareTo(TimeSeriesMeta o) {
        return o != null ? key.compareTo(o.getKey()) : 0;
    }
}
