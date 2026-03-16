package ru.ulstu.method.exponential.parameter;

public class ExponentialMethodParamValue<T extends ExponentialMethodParameter> {
    private final T param;
    private Number value;

    public ExponentialMethodParamValue(T param, Number value) {
        this.param = param;
        this.value = value;
    }

    public T getParam() {
        return param;
    }

    public Number getValue() {
        return value;
    }

    public double getDoubleValue() {
        return value.doubleValue();
    }

    public int getIntValue() {
        return value.intValue();
    }

    public void setValue(Number value) {
        this.value = value;
    }
}
