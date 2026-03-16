package ru.ulstu.datamodel.response;

public enum ErrorConstants {
    UNKNOWN(0, "Unknown error"),
    TIME_SERIES_VALIDATE_ERROR(10, "Некорректный временной ряд"),
    FORECAST_PARAMS_ERROR(11, "Некорректные параметры для прогнозирования"),
    MODELING_ERROR(13, "Ошибка моделирования"),
    HTTP_CLIENT_ERROR(66, "Http client error");

    private final int code;
    private final String message;

    ErrorConstants(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return String.format("%d: %s", code, message);
    }
}
