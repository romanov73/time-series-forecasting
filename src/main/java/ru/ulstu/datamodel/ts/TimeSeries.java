package ru.ulstu.datamodel.ts;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class TimeSeries {
    private List<TimeSeriesValue> values = new ArrayList<>();
    private String key;

    public TimeSeries(String key) {
        this.key = key;
    }

    public TimeSeries(String prefix, String suffix) {
        this.key = String.format("%s %s", prefix, suffix);
    }

    @JsonCreator
    public TimeSeries(@JsonProperty(value = "values") List<TimeSeriesValue> values, @JsonProperty(value = "name") String key) {
        this.values = values;
        this.key = key;
    }

    public TimeSeries() {

    }

    public TimeSeries(List<TimeSeriesValue> values) {
        this.values = values;
    }

    public List<TimeSeriesValue> getValues() {
        return values;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public boolean isEmpty() {
        return values.isEmpty();
    }

    public void addValue(TimeSeriesValue timeSeriesValue) {
        values.add(timeSeriesValue);
    }

    public void addValue(TimeSeriesValue basedOnValue, Double value) {
        values.add(new TimeSeriesValue(basedOnValue.getDate(), value));
    }

    @JsonIgnore
    public TimeSeriesValue getLastValue() {
        return values.get(values.size() - 1);
    }

    public int getLength() {
        return values.size();
    }

    @JsonIgnore
    public TimeSeriesValue getFirstValue() {
        if ((values.size() > 0)) {
            return values.get(0);
        }
        throw new RuntimeException("Временной ряд пуст");
    }

    public Double getNumericValue(int t) {
        if ((values.size() > t) && (t >= 0)) {
            return values.get(t).getValue();
        }
        throw new RuntimeException("Индекс выходит за границы временного ряда");
    }

    public TimeSeriesValue getValue(int t) {
        if ((values.size() > t) && (t >= 0)) {
            return values.get(t);
        }
        throw new RuntimeException("Индекс выходит за границы временного ряда");
    }

    @Override
    public String toString() {
        return "TimeSeries{" +
                "values=" + values +
                ", name='" + key + '\'' +
                '}';
    }
}
