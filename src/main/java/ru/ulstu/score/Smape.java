package ru.ulstu.score;

import ru.ulstu.datamodel.exception.ModelingException;
import ru.ulstu.datamodel.ts.TimeSeries;
import ru.ulstu.datamodel.ts.TimeSeriesValue;

import java.time.LocalDateTime;
import java.util.Map;

import static java.lang.Math.abs;

public class Smape extends ScoreMethod {
    public Smape() {
        super("Smape, %");
    }

    @Override
    public Number evaluate(Map<LocalDateTime, Double> tsValues, TimeSeries model) throws ModelingException {
        double sum = 0;
        for (TimeSeriesValue modelValue : model.getValues()) {
            double actualValue = getValueOnSameDate(tsValues, modelValue);
            sum += abs(modelValue.getValue() - actualValue)
                    / ((abs(actualValue) + abs(modelValue.getValue())) / 2);
        }
        sum = (double) Math.round(sum * 100) / 100;
        return sum * 100 / model.getLength();
    }
}