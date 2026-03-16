package ru.ulstu.method.ftransform;

import ru.ulstu.datamodel.Model;
import ru.ulstu.datamodel.ts.TimeSeries;
import ru.ulstu.method.MethodParamValue;
import ru.ulstu.method.MethodParameter;
import ru.ulstu.method.ftransform.parameter.NumberOfCoveredPoints;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FTransformModel extends Model {
    private final MethodParamValue numberOfCoveredPoints = new MethodParamValue(NumberOfCoveredPoints.getInstance(), 3);
    private final List<AComponent> aComponents = new ArrayList<>();
    private final TimeSeries piecewiseLinearTrend;

    public FTransformModel(TimeSeries ts, List<MethodParamValue> parameters) {
        super(ts);
        piecewiseLinearTrend = new TimeSeries("Piecewise linear trend of ", ts.getKey());
        for (MethodParamValue parameter : parameters) {
            if (parameter.getParameter() instanceof NumberOfCoveredPoints) {
                numberOfCoveredPoints.setValue(parameter.getValue());
            }
        }
    }

    public TimeSeries getPiecewiseLinearTrend() {
        return piecewiseLinearTrend;
    }

    public List<AComponent> getAComponents() {
        return aComponents;
    }

    public MethodParamValue getNumberOfCoveredPoints() {
        return numberOfCoveredPoints;
    }

    public static List<MethodParameter> getAvailableParameters() {
        return Collections.singletonList(NumberOfCoveredPoints.getInstance());
    }
}
