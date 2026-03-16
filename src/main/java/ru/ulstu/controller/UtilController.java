package ru.ulstu.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.ulstu.configuration.ApiConfiguration;
import ru.ulstu.datamodel.ts.TimeSeries;
import ru.ulstu.service.UtilService;

import java.util.Map;

@RestController
@RequestMapping(ApiConfiguration.API_1_0)
public class UtilController {

    private final UtilService utilService;

    public UtilController(UtilService utilService) {
        this.utilService = utilService;
    }

    @GetMapping("alive")
    @Operation(description = "Проверка сервиса")
    public ResponseEntity<Map<String, Boolean>> isAlive() {
        return new ResponseEntity<>(Map.of("result", true), HttpStatus.OK);
    }

    @PostMapping("getRandom")
    @Operation(description = "Получить временной ряд рандомной длины")
    public ResponseEntity<TimeSeries> getRandomTimeSeries(@RequestParam("length") int length) {
        return new ResponseEntity<>(utilService.getRandomTimeSeries(length), HttpStatus.OK);
    }

    @GetMapping("getFromString")
    @Operation(description = "Преобразовать строку с разделителями во временной ряд")
    public ResponseEntity<TimeSeries> getTimeSeriesFromString(@RequestParam("tsString") String tsString) {
        return new ResponseEntity<>(utilService.getTimeSeriesFromString(tsString), HttpStatus.OK);
    }

    @PostMapping("timeSeriesToString")
    @Operation(description = "Преобразовать временной ряд в строку с разделителями")
    public ResponseEntity<String> getTimeSeriesToString(@RequestBody TimeSeries timeSeries) {
        return new ResponseEntity<>(utilService.getTimeSeriesToString(timeSeries), HttpStatus.OK);
    }
}
