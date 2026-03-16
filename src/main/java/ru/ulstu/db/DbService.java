package ru.ulstu.db;

import ru.ulstu.datamodel.exception.ModelingException;
import ru.ulstu.datamodel.ts.TimeSeries;
import ru.ulstu.db.model.TimeSeriesMeta;
import ru.ulstu.db.model.TimeSeriesSet;

import java.io.IOException;
import java.util.List;

public interface DbService {
    List<TimeSeriesSet> getSets() throws IOException;

    List<TimeSeriesMeta> getTimeSeriesMeta(TimeSeriesSet timeSeriesSet) throws IOException;

    TimeSeries getTimeSeries(TimeSeriesSet timeSeriesSet, String timeSeriesKey) throws IOException;

    boolean addSet(String key);

    void addTimeSeries(TimeSeriesSet timeSeriesSet, TimeSeries timeSeries) throws IOException, ModelingException;

    boolean deleteTimeSeries(TimeSeriesSet set, String timeSeriesKey) throws IOException;

    boolean deleteTimeSeriesSet(TimeSeriesSet timeSeriesSet) throws IOException;
}
