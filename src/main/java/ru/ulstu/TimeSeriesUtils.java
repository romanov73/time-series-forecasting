package ru.ulstu;

import ru.ulstu.datamodel.ts.TimeSeries;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class TimeSeriesUtils {
    /**
     * Вычисляет среднее значение между датами временного ряда
     *
     * @param timeSeries объект, содержащий временной ряд
     * @return средняя разница между датами исходного временного ряда в миллисекундах
     */
    public static long getTimeDifferenceInMilliseconds(TimeSeries timeSeries) {
        long diffMilliseconds = 0;
        for (int i = 1; i < timeSeries.getLength(); i++) {
            diffMilliseconds += timeSeries.getValues().get(i - 1).getDate()
                    .until(timeSeries.getValues().get(i).getDate(), ChronoUnit.MILLIS);
        }
        return diffMilliseconds / (timeSeries.getLength() - 1);
    }

    public static TimeSeries fillDates(TimeSeries timeSeries, long milliseconds) {
        if (!timeSeries.isEmpty()) {
            timeSeries.getLastValue().setDate(LocalDateTime.now());
            for (int i = timeSeries.getLength() - 2; i >= 0; i--) {
                timeSeries.getValues().get(i).setDate(timeSeries.getValues().get(i + 1).getDate().minus(milliseconds, ChronoUnit.MILLIS));
            }
        }
        return timeSeries;
    }

    public static TimeSeries fillDates(TimeSeries timeSeries) {
        return fillDates(timeSeries, 1000 * 60 * 60 * 24);
    }
}
