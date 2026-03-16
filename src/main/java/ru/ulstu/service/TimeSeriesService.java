package ru.ulstu.service;

import jakarta.validation.Valid;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import ru.ulstu.datamodel.DatesParams;
import ru.ulstu.datamodel.ModelingResult;
import ru.ulstu.datamodel.exception.ModelingException;
import ru.ulstu.datamodel.ts.TimeSeries;
import ru.ulstu.datamodel.ts.TimeSeriesValue;
import ru.ulstu.method.Method;

import java.lang.reflect.InvocationTargetException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;


@Service
public class TimeSeriesService {
    private final MethodParamBruteForce methodParamBruteForce;
    private final ApplicationContext applicationContext;
    private final UtilService utilService;

    public TimeSeriesService(MethodParamBruteForce methodParamBruteForce,
                             ApplicationContext applicationContext, UtilService utilService) {
        this.methodParamBruteForce = methodParamBruteForce;
        this.applicationContext = applicationContext;
        this.utilService = utilService;
    }

    public ModelingResult getForecast(TimeSeries timeSeries, int countPoints) throws ExecutionException, InterruptedException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, ModelingException {
        return methodParamBruteForce.getForecast(timeSeries, countPoints);
    }

    public ModelingResult getForecast(TimeSeries timeSeries, String methodClassName, int countPoints) throws ExecutionException, InterruptedException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, ModelingException {
        return methodParamBruteForce.getForecast(timeSeries, methodClassName, countPoints);
    }

    public ModelingResult smoothTimeSeries(TimeSeries timeSeries) throws ExecutionException, InterruptedException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        return methodParamBruteForce.getSmoothedTimeSeries(timeSeries);
    }

    public ModelingResult smoothTimeSeries(TimeSeries timeSeries, String methodClassName) throws ExecutionException, InterruptedException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, ModelingException {
        return methodParamBruteForce.getSmoothedTimeSeries(timeSeries, methodClassName);
    }

    public ModelingResult getMaxSmoothedTimeSeries(TimeSeries timeSeries) throws ExecutionException, InterruptedException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, ModelingException {
        return methodParamBruteForce.getMaxSmoothedTimeSeries(timeSeries);
    }

    public List<Method> getAvailableMethods() {
        return methodParamBruteForce.getAvailableMethods();
    }

    public List<TimeSeries> getGroupedTendencies(List<TimeSeries> timeSeriesList) {
        return timeSeriesList
                .stream()
                .filter(ts -> ts.getValues() != null && ts.getLength() > 5)
                .map(this::getGroupedTendencies)
                .collect(Collectors.toList());
    }

    public TimeSeries getGroupedTendencies(TimeSeries timeSeries) {
        try {
            timeSeries = getMaxSmoothedTimeSeries(timeSeries).getTimeSeries();
        } catch (Exception e) {
            e.printStackTrace();
        }
        int i = 2;
        double prevDiff = timeSeries.getNumericValue(1) -
                timeSeries.getNumericValue(0);
        while (i < timeSeries.getLength()) {
            double diff = timeSeries.getNumericValue(i) -
                    timeSeries.getNumericValue(i - 1);
            //если тенденция сохранилась
            if (tsTendencyNotChanged(diff, prevDiff)) {
                timeSeries.getValues().remove(i - 1);
            } else {
                i++;
            }
            prevDiff = diff;
        }
        return timeSeries;
    }

    private boolean tsTendencyNotChanged(double diff, double prevDiff) {
        return (diff > 0 && prevDiff > 0)
                || (diff < 0 && prevDiff < 0)
                || (diff == 0 && prevDiff == 0);
    }

    public ModelingResult getForecastByDates(@Valid DatesParams datesParams) throws ModelingException, ExecutionException, InterruptedException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        TimeSeries datesTimeSeries = new TimeSeries(diffDates(datesParams.getDates()));
        return getForecast(datesTimeSeries,
                datesParams.getFinalDatesCount() - datesParams.getDates().size());
    }

    public List<TimeSeriesValue> diffDates(List<LocalDateTime> dates) {
        if (dates == null || dates.size() < 2) {
            return new ArrayList<>();
        }

        List<TimeSeriesValue> result = new ArrayList<>(dates.size() - 1);

        for (int i = 0; i < dates.size() - 1; i++) {
            LocalDateTime date1 = dates.get(i);
            LocalDateTime date2 = dates.get(i + 1);

            if (date1 == null || date2 == null) {
                continue; // пропускаем пары с null значениями
            }

            LocalDateTime maxDate = date1.isAfter(date2) ? date1 : date2;
            long diffMillis = Math.abs(Duration.between(date1, date2).toMillis());
            TimeSeriesValue timeSeriesValue = new TimeSeriesValue(maxDate, (double) diffMillis);

            result.add(timeSeriesValue);
        }

        return result;
    }
}
