package ru.ulstu.datamodel.ts;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.Objects;

public class TimeSeriesValue {
    private LocalDateTime date;
    private Double value;

    @JsonCreator
    public TimeSeriesValue(@JsonProperty(value = "date") LocalDateTime date, @JsonProperty(value = "value") Double value) {
        this.date = date;
        this.value = value;
    }

    public TimeSeriesValue(LocalDateTime date) {
        this.date = date;
    }

    public TimeSeriesValue(Double value) {
        this.value = value;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "TimeSeriesValue{" +
                "date=" + date +
                ", value=" + value +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimeSeriesValue that = (TimeSeriesValue) o;
        return Objects.equals(date, that.date) &&
                Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, value);
    }

}
