package ru.ulstu.db.model;

import java.io.File;

public class TimeSeriesSet implements Comparable<TimeSeriesSet> {
    private final String key;

    public TimeSeriesSet(File dir) {
        this.key = dir.getName();
    }

    public TimeSeriesSet(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    @Override
    public int compareTo(TimeSeriesSet o) {
        return o != null ? key.compareTo(o.getKey()) : 0;
    }
}
