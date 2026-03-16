package ru.ulstu.statistic;

import org.springframework.stereotype.Service;
import ru.ulstu.datamodel.ts.TimeSeries;
import ru.ulstu.datamodel.ts.TimeSeriesValue;

import java.util.Optional;
import java.util.OptionalDouble;
import java.util.stream.DoubleStream;

@Service
public class StatisticService {
    public Optional<Double> getAverage(TimeSeries timeSeries) {
        return getOptionalValue(getDoubleStream(timeSeries).average());
    }

    public Optional<Double> getMin(TimeSeries timeSeries) {
        return getOptionalValue(getDoubleStream(timeSeries).min());
    }

    public Optional<Double> getMax(TimeSeries timeSeries) {
        return getOptionalValue(getDoubleStream(timeSeries).max());
    }

    public Optional<Double> getLength(TimeSeries timeSeries) {
        return getOptionalValue(Double.valueOf(timeSeries.getLength()));
    }

    public Optional<Double> getDispersion(TimeSeries timeSeries) {
        Optional<Double> maybeAverage = getAverage(timeSeries);
        return getOptionalValue(maybeAverage.isPresent()
                ? timeSeries.getValues().stream().mapToDouble(v -> Math.pow(v.getValue() - maybeAverage.get(), 2)).sum() / timeSeries.getLength()
                : null);
    }

    private DoubleStream getDoubleStream(TimeSeries timeSeries) {
        return timeSeries.getValues().stream().mapToDouble(TimeSeriesValue::getValue);
    }

    private Optional<Double> getOptionalValue(OptionalDouble optionalDouble) {
        return getOptionalValue(optionalDouble.isPresent()
                ? optionalDouble.getAsDouble()
                : null);
    }

    private Optional<Double> getOptionalValue(Double value) {
        value = (value == null)
                ? null
                : ((double) Math.round(value * 100) / 100);
        return Optional.ofNullable(value);
    }

    public TimeSeriesStatistic getStatistic(TimeSeries timeSeries) {
        return new TimeSeriesStatistic(getMin(timeSeries),
                getMax(timeSeries),
                getAverage(timeSeries),
                getLength(timeSeries),
                getDispersion(timeSeries));
    }
}
