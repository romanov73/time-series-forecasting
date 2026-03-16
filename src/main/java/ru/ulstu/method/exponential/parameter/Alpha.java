package ru.ulstu.method.exponential.parameter;

public class Alpha extends ExponentialMethodParameter {
    public Alpha() {
        super("ALPHA", DEFAULT_MIN_VALUE, DEFAULT_MAX_VALUE, DEFAULT_OPTIMIZATION_STEP);
    }

    public static Alpha getInstance() {
        return new Alpha();
    }
}
