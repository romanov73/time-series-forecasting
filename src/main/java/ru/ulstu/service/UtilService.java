package ru.ulstu.service;

import org.springframework.stereotype.Service;
import ru.ulstu.TimeSeriesUtils;
import ru.ulstu.datamodel.ts.TimeSeries;
import ru.ulstu.datamodel.ts.TimeSeriesValue;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class UtilService {
    private static final String DELIMITER = ";";

    public TimeSeries getRandomTimeSeries(int length) {
        TimeSeries ts = new TimeSeries("Random time series");
        LocalDateTime dateStart = LocalDateTime.now().minusDays(length);
        for (int i = 0; i < length; i++) {
            ts.getValues().add(new TimeSeriesValue(dateStart, Math.random()));
            dateStart = dateStart.plusDays(1);
        }
        return ts;
    }

    public TimeSeries getTimeSeriesFromString(String tsString) {
        List<TimeSeriesValue> tsValues = Arrays.stream(tsString.split("\n"))
                .flatMap(v -> Arrays.stream(v.split(";")))
                .flatMap(v -> Arrays.stream(v.split(",")))
                .flatMap(v -> Arrays.stream(v.split("<br>")))
                .flatMap(v -> Arrays.stream(v.split(" ")))
                .filter(v -> {
                    try {
                        Double.parseDouble(v);
                        return true;
                    } catch (NumberFormatException e) {
                        return false;
                    }
                })
                .map(Double::parseDouble)
                .map(TimeSeriesValue::new)
                .collect(Collectors.toList());
        return TimeSeriesUtils.fillDates(new TimeSeries(tsValues));
    }

    public String getTimeSeriesToString(TimeSeries timeSeries) {
        return timeSeries
                .getValues()
                .stream()
                .map(v -> v.getValue().toString().replace("\\.", ","))
                .collect(Collectors.joining(DELIMITER));
    }

    public String getTimeSeriesToDateValueString(TimeSeries timeSeries) {
        return timeSeries
                .getValues()
                .stream()
                .map(v -> new StringBuilder(v.getDate().format(DateTimeFormatter.ISO_DATE_TIME))
                        .append(DELIMITER)
                        .append(v.getValue().toString().replace("\\.", ","))
                        .toString())
                .collect(Collectors.joining("\n"));
    }

    public TimeSeries getTimeSeriesFromDateValueString(String tsString) {
        List<TimeSeriesValue> tsValues = Arrays.stream(tsString.split("\n"))
                .map(row -> row.split(DELIMITER))
                .filter(v -> {
                    if (v.length > 1) {
                        try {
                            Double.parseDouble(v[1]);
                            return true;
                        } catch (NumberFormatException e) {
                            return false;
                        }
                    }
                    return false;
                })
                .map(v -> {
                    return new TimeSeriesValue(LocalDateTime.parse(v[0], DateTimeFormatter.ISO_DATE_TIME), Double.parseDouble(v[1]));
                })
                .collect(Collectors.toList());
        return new TimeSeries(tsValues);
    }
}
