package ru.ulstu.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.ulstu.configuration.ApiConfiguration;
import ru.ulstu.datamodel.exception.ModelingException;
import ru.ulstu.datamodel.ts.TimeSeries;
import ru.ulstu.db.DbService;
import ru.ulstu.db.model.TimeSeriesMeta;
import ru.ulstu.db.model.TimeSeriesSet;
import ru.ulstu.service.UtilService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(ApiConfiguration.API_1_0)
public class DbController {

    private final DbService dbService;
    private final UtilService utilService;

    public DbController(DbService dbService,
                        UtilService utilService) {
        this.dbService = dbService;
        this.utilService = utilService;
    }

    @GetMapping("get-time-series-sets")
    public List<TimeSeriesSet> getTimeSeriesSets() throws IOException {
        return dbService.getSets();
    }

    @GetMapping("get-time-series-meta")
    public List<TimeSeriesMeta> getTimeSeriesSets(@RequestParam("setKey") String setKey) throws IOException {
        return dbService.getTimeSeriesMeta(new TimeSeriesSet(setKey));
    }

    @GetMapping("get-time-series")
    public TimeSeries getTimeSeries(@RequestParam("setKey") String setKey, @RequestParam("timeSeriesKey") String timeSeriesKey) throws IOException {
        return dbService.getTimeSeries(new TimeSeriesSet(setKey), timeSeriesKey);
    }

    @GetMapping("add-time-series-set")
    public boolean addTimeSeriesSet(@RequestParam("setKey") String setKey) {
        return dbService.addSet(setKey);
    }

    @PostMapping("add-time-series")
    public void addTimeSeries(@RequestParam("setKey") String setKey, @RequestBody TimeSeries timeSeries) throws IOException, ModelingException {
        dbService.addTimeSeries(new TimeSeriesSet(setKey), timeSeries);
    }

    @PostMapping("add-time-series-string")
    public void addTimeSeries(@RequestParam("setKey") String setKey, @RequestParam("timeSeriesKey") String timeSeriesKey, @RequestBody String timeSeries) throws IOException, ModelingException {
        TimeSeries timeSeriesWithKey = utilService.getTimeSeriesFromString(timeSeries);
        timeSeriesWithKey.setKey(timeSeriesKey);
        dbService.addTimeSeries(new TimeSeriesSet(setKey), timeSeriesWithKey);
    }

    @DeleteMapping("delete-time-series")
    public boolean deleteTimeSeries(@RequestParam("setKey") String setKey, @RequestParam("timeSeriesKey") String timeSeriesKey) throws IOException {
        return dbService.deleteTimeSeries(new TimeSeriesSet(setKey), timeSeriesKey);
    }

    @DeleteMapping("delete-time-series-set")
    public boolean deleteTimeSeriesSet(@RequestParam("setKey") String setKey) throws IOException {
        return dbService.deleteTimeSeriesSet(new TimeSeriesSet(setKey));
    }
}
