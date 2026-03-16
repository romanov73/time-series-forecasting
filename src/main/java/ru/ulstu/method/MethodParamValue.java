package ru.ulstu.method;

public class MethodParamValue {
    protected MethodParameter parameter;
    protected Number value;

    public MethodParamValue(MethodParameter parameter, Number value) {
        this.parameter = parameter;
        this.value = value;
    }

    public MethodParameter getParameter() {
        return parameter;
    }

    public Number getValue() {
        return value;
    }

    public Integer getIntValue() {
        return value.intValue();
    }

    public void setValue(Number value) {
        this.value = value;
    }
}
