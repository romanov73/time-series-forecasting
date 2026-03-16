package ru.ulstu.method.exponential.parameter;

public class Beta extends ExponentialMethodParameter {
    public Beta() {
        super("BETA", DEFAULT_MIN_VALUE, DEFAULT_MAX_VALUE, DEFAULT_OPTIMIZATION_STEP);
    }

    public static Beta getInstance() {
        return new Beta();
    }
}
