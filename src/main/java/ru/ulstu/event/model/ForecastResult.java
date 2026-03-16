package ru.ulstu.event.model;

public class ForecastResult {
    private final String forecastEvent;
    private final double confidence;

    public ForecastResult(String forecastEvent, double confidence) {
        this.forecastEvent = forecastEvent;
        this.confidence = confidence;
    }

    public String getForecastEvent() {
        return forecastEvent;
    }

    public double getConfidence() {
        return confidence;
    }
}