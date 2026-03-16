package ru.ulstu.method.exponential.parameter;

public class Gamma extends ExponentialMethodParameter {
    public Gamma() {
        super("Gamma", DEFAULT_MIN_VALUE, DEFAULT_MAX_VALUE, DEFAULT_OPTIMIZATION_STEP);
    }

    public static Gamma getInstance() {
        return new Gamma();
    }
}
