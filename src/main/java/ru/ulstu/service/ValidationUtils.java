package ru.ulstu.service;

import ru.ulstu.datamodel.exception.ModelingException;
import ru.ulstu.datamodel.exception.TimeSeriesValidateException;
import ru.ulstu.datamodel.ts.TimeSeries;
import ru.ulstu.datamodel.ts.TimeSeriesValue;

import java.util.stream.Collectors;


public class ValidationUtils {
    public static void validateTimeSeries(TimeSeries timeSeries) throws ModelingException {
        if (timeSeries == null || timeSeries.isEmpty()) {
            throw new TimeSeriesValidateException("Временной ряд должен быть не пустым");
        }
        if (timeSeries.getLength() < 2) {
            throw new TimeSeriesValidateException("Временной ряд должен содержать хотя бы 2 точки");
        }
        if (timeSeries.getValues().stream().anyMatch(val -> val == null || val.getValue() == null)) {
            throw new TimeSeriesValidateException("Временной ряд содержит пустые значения");
        }
        if (timeSeries.getValues().stream().anyMatch(val -> val.getDate() == null)) {
            throw new TimeSeriesValidateException("Временной ряд должен иметь отметки времени");
        }

        if (timeSeries.getValues().stream().map(TimeSeriesValue::getDate).collect(Collectors.toSet()).size() < timeSeries.getLength()) {
            throw new TimeSeriesValidateException("Временной ряд должен иметь разные отметки времени");
        }
    }
}
