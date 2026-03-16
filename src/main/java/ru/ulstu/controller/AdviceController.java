package ru.ulstu.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpServerErrorException;
import ru.ulstu.datamodel.exception.ForecastValidateException;
import ru.ulstu.datamodel.exception.ModelingException;
import ru.ulstu.datamodel.exception.TimeSeriesValidateException;
import ru.ulstu.datamodel.response.ErrorConstants;
import ru.ulstu.datamodel.response.ResponseExtended;

@ControllerAdvice
public class AdviceController {
    private final Logger log = LoggerFactory.getLogger(AdviceController.class);

    private <E> ResponseExtended<E> handleException(ErrorConstants error, E errorData) {
        log.warn(error.toString());
        return new ResponseExtended<>(error, errorData);
    }

    @ExceptionHandler(Exception.class)
    public ResponseExtended<String> handleUnknownException(Throwable e) {
        e.printStackTrace();
        return handleException(ErrorConstants.UNKNOWN, e.getMessage());
    }

    @ExceptionHandler(HttpServerErrorException.class)
    public ResponseExtended<String> handleHttpClientException(Throwable e) {
        return handleException(ErrorConstants.HTTP_CLIENT_ERROR, e.getMessage());
    }

    @ExceptionHandler(TimeSeriesValidateException.class)
    public ResponseExtended<String> handleTimeSeriesValidateException(Throwable e) {
        e.printStackTrace();
        return handleException(ErrorConstants.TIME_SERIES_VALIDATE_ERROR, e.getMessage());
    }

    @ExceptionHandler(ForecastValidateException.class)
    public ResponseExtended<String> handleForecastValidateException(Throwable e) {
        e.printStackTrace();
        return handleException(ErrorConstants.FORECAST_PARAMS_ERROR, e.getMessage());
    }

    @ExceptionHandler(ModelingException.class)
    public ResponseExtended<String> handleModelingException(Throwable e) {
        e.printStackTrace();
        return handleException(ErrorConstants.MODELING_ERROR, e.getMessage());
    }
}
