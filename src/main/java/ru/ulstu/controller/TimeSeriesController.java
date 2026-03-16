package ru.ulstu.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.ulstu.HttpUtils;
import ru.ulstu.configuration.ApiConfiguration;
import ru.ulstu.datamodel.DatesParams;
import ru.ulstu.datamodel.ForecastParams;
import ru.ulstu.datamodel.ModelingResult;
import ru.ulstu.datamodel.SmoothingParams;
import ru.ulstu.datamodel.exception.ModelingException;
import ru.ulstu.datamodel.ts.TimeSeries;
import ru.ulstu.method.Method;
import ru.ulstu.service.TimeSeriesService;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping(ApiConfiguration.API_1_0)
public class TimeSeriesController {
    private final static Logger LOGGER = LoggerFactory.getLogger(TimeSeriesController.class);

    private final TimeSeriesService timeSeriesService;

    public TimeSeriesController(TimeSeriesService timeSeriesService) {
        this.timeSeriesService = timeSeriesService;
    }

    @PostMapping("getForecast")
    @Operation(description = "Получить прогноз временного ряда любым методом")
    public ResponseEntity<ModelingResult> getForecastTimeSeries(@RequestBody @Valid ForecastParams forecastParams, HttpServletRequest request) throws ExecutionException, InterruptedException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, ModelingException {
        LOGGER.info("User ip: " + HttpUtils.getUserIp(request));
        LOGGER.info("Forecast: " + forecastParams);
        ResponseEntity<ModelingResult> result = new ResponseEntity<>(timeSeriesService.getForecast(forecastParams.getOriginalTimeSeries(),
                forecastParams.getCountForecast()), HttpStatus.OK);
        LOGGER.info("Forecast result complete");
        return result;
    }

    @PostMapping("getSmoothed")
    @Operation(description = "Получить сглаженный временной ряд любым методом")
    public ResponseEntity<ModelingResult> getSmoothedTimeSeries(@RequestBody TimeSeries timeSeries, HttpServletRequest request) throws ExecutionException, InterruptedException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        LOGGER.info("User ip: " + HttpUtils.getUserIp(request));
        LOGGER.info("Time series for smoothing: " + timeSeries);
        ResponseEntity<ModelingResult> result = new ResponseEntity<>(timeSeriesService.smoothTimeSeries(timeSeries), HttpStatus.OK);
        LOGGER.info("Smoothing complete");
        return result;
    }

    @PostMapping("getSpecificMethodSmoothed")
    @Operation(description = "Получить сглаженный временной ряд выбранным методом")
    public ResponseEntity<ModelingResult> getSpecificMethodSmoothedTimeSeries(@RequestBody @Valid SmoothingParams smoothingParams, HttpServletRequest request) throws ExecutionException, InterruptedException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, ModelingException {
        LOGGER.info("User ip: " + HttpUtils.getUserIp(request));
        LOGGER.info("Time series for smoothing: " + smoothingParams.getOriginalTimeSeries());
        ResponseEntity<ModelingResult> result = new ResponseEntity<>(timeSeriesService.smoothTimeSeries(smoothingParams.getOriginalTimeSeries(), smoothingParams.getMethodClassName()), HttpStatus.OK);
        LOGGER.info("Smoothing complete");
        return result;
    }

    @PostMapping("getSpecificMethodForecast")
    @Operation(description = "Получить прогноз временного ряда указанным методом")
    public ResponseEntity<ModelingResult> getForecastTimeSeriesSpecificMethod(@RequestBody @Valid ForecastParams forecastParams, HttpServletRequest request) throws ExecutionException, InterruptedException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, ModelingException {
        LOGGER.info("User ip: " + HttpUtils.getUserIp(request));
        LOGGER.info("Forecast: " + forecastParams);
        ResponseEntity<ModelingResult> result = new ResponseEntity<>(timeSeriesService.getForecast(forecastParams.getOriginalTimeSeries(),
                forecastParams.getMethodClassName(),
                forecastParams.getCountForecast()), HttpStatus.OK);
        LOGGER.info("Forecast result complete");
        return result;
    }

    @GetMapping("availableMethods")
    @Operation(description = "Получить список доступных методов моделирования")
    public ResponseEntity<List<Method>> getAvailableMethods() {
        return new ResponseEntity<>(timeSeriesService.getAvailableMethods(), HttpStatus.OK);
    }

    @PostMapping("getGroupedTendencies")
    @Operation(description = "Получить список сгруппированных тенденций")
    public ResponseEntity<List<TimeSeries>> getGroupedTendencies(@RequestBody List<TimeSeries> timeSeriesList) {
        return new ResponseEntity<>(timeSeriesService.getGroupedTendencies(timeSeriesList), HttpStatus.OK);
    }

    @PostMapping("getMaxSmoothing")
    @Operation(description = "Получить максимальное сглаживание временного ряда")
    public ResponseEntity<ModelingResult> getMaxSmoothing(@RequestBody TimeSeries timeSeries) throws ModelingException, ExecutionException, InterruptedException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        return new ResponseEntity<>(timeSeriesService.getMaxSmoothedTimeSeries(timeSeries), HttpStatus.OK);
    }

    @PostMapping("getForecastByDates")
    @Operation(description = "Получить прогноз на основе списка дат")
    public ResponseEntity<ModelingResult> getForecastByDates(@RequestBody @Valid DatesParams datesParams, HttpServletRequest request) throws ExecutionException, InterruptedException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, ModelingException {
        LOGGER.info("User ip: " + HttpUtils.getUserIp(request));
        LOGGER.info("Forecast: " + datesParams);
        ResponseEntity<ModelingResult> result = new ResponseEntity<>(timeSeriesService.getForecastByDates(datesParams), HttpStatus.OK);
        LOGGER.info("Forecast result complete");
        return result;
    }
}
