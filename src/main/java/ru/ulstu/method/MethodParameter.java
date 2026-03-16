package ru.ulstu.method;

import ru.ulstu.datamodel.ts.TimeSeries;

import java.util.List;

public abstract class MethodParameter implements Comparable<MethodParameter> {
    protected String name;

    public MethodParameter(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract List<Number> getAvailableValues(TimeSeries timeSeries);

    @Override
    public int compareTo(MethodParameter o) {
        return this.name.compareTo(o.getName());
    }
}
