package ru.ulstu;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.ulstu.datamodel.ts.TimeSeries;
import ru.ulstu.datamodel.ts.TimeSeriesValue;
import ru.ulstu.statistic.StatisticService;

import java.time.LocalDateTime;

public class StatisticServiceTest {

    private TimeSeries getTimeSeries() {
        TimeSeries ts = new TimeSeries();
        for (int i = 0; i < 11; i++) {
            ts.addValue(new TimeSeriesValue(LocalDateTime.now(), (double) i));
        }
        return TimeSeriesUtils.fillDates(ts);
    }

    @Test
    public void testAverage() {
        Assertions.assertEquals(new StatisticService().getAverage(getTimeSeries())
                .orElseThrow(() -> new RuntimeException("Average test failed")), 5.0);
    }

    @Test
    public void testMin() {
        Assertions.assertEquals(new StatisticService().getMin(getTimeSeries())
                .orElseThrow(() -> new RuntimeException("Min test failed")), 0.0);
    }

    @Test
    public void testMax() {
        Assertions.assertEquals(new StatisticService().getMax(getTimeSeries())
                .orElseThrow(() -> new RuntimeException("Max test failed")), 10.0);
    }

    @Test
    public void testLength() {
        Assertions.assertEquals(new StatisticService().getLength(getTimeSeries())
                .orElseThrow(() -> new RuntimeException("Length test failed")), 11.0);
    }
}
