package ru.ulstu.statistic;

import java.util.Optional;

public class TimeSeriesStatistic {
    private Optional<Double> min;
    private Optional<Double> max;
    private Optional<Double> average;
    private Optional<Double> length;

    private Optional<Double> dispersion;

    public TimeSeriesStatistic(Optional<Double> min,
                               Optional<Double> max,
                               Optional<Double> average,
                               Optional<Double> length,
                               Optional<Double> dispersion) {
        this.min = min;
        this.max = max;
        this.average = average;
        this.length = length;
        this.dispersion = dispersion;
    }

    public Optional<Double> getMin() {
        return min;
    }

    public Optional<Double> getMax() {
        return max;
    }

    public Optional<Double> getAverage() {
        return average;
    }

    public Optional<Double> getLength() {
        return length;
    }

    public Optional<Double> getDispersion() {
        return dispersion;
    }
}
