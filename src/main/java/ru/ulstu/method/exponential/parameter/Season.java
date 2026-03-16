package ru.ulstu.method.exponential.parameter;

public class Season extends ExponentialMethodParameter {
    private final static int DEFAULT_SEASON_OPTIMIZATION_STEP = 1;

    public Season() {
        super("Сезонность", 1, 12, DEFAULT_SEASON_OPTIMIZATION_STEP);
    }

    public static Season getInstance() {
        return new Season();
    }
}
