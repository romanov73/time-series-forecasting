package ru.ulstu.event.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.ulstu.event.model.ForecastResult;
import ru.ulstu.event.model.History;
import ru.ulstu.event.service.CommonService;


@RestController
public class EventForecastController {
    private final CommonService commonService;

    public EventForecastController(CommonService commonService) {
        this.commonService = commonService;
    }

    @PostMapping("get-forecast")
    public ForecastResult getForecast(@RequestBody History history) {
        return commonService.trainAndPredict(history);
    }
}
