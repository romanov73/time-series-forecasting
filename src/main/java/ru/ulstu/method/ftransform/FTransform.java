package ru.ulstu.method.ftransform;

import org.springframework.stereotype.Component;
import ru.ulstu.datamodel.Model;
import ru.ulstu.datamodel.ts.TimeSeries;
import ru.ulstu.method.Method;
import ru.ulstu.method.MethodParamValue;
import ru.ulstu.method.MethodParameter;

import java.util.List;

@Component
public class FTransform extends Method {

    @Override
    protected FTransformModel getModelOfValidTimeSeries(TimeSeries ts,
                                                        List<MethodParamValue> parameters) {
        FTransformModel model = new FTransformModel(ts, parameters);
        List<AComponent> aComponents = generateAComponents(ts, model.getNumberOfCoveredPoints().getIntValue(), model.getAComponents());

        TimeSeries piecewiseLinearTrend = model.getPiecewiseLinearTrend();
        TimeSeries tsModel = model.getTimeSeriesModel();

        for (AComponent aComponent : aComponents) {
            double sum1 = 0;
            double sum2 = 0;
            for (int j = 0; j < ts.getLength(); j++) {
                double membership = aComponent.getValueAtPoint(j);
                sum1 += membership * ts.getNumericValue(j);
                sum2 += membership;
            }
            piecewiseLinearTrend.addValue(ts.getValue(aComponent.getTop()), sum1 / sum2);
            tsModel.addValue(ts.getValue(aComponent.getTop()), sum1 / sum2);
        }
        return model;
    }

    private List<AComponent> generateAComponents(TimeSeries ts, int numberOfCoveredPoints, List<AComponent> piecewiseLinearTrend) {
        long deltaForTriangle = Math.round(numberOfCoveredPoints / 2.0);
        int currentPoint = 0;
        while (currentPoint < ts.getLength()) {
            int startPoint = (currentPoint == 0)
                    ? 0
                    : piecewiseLinearTrend.get(piecewiseLinearTrend.size() - 1).getTop();
            AComponent bf = new AComponent(startPoint, currentPoint, (int) (currentPoint + Math.round(numberOfCoveredPoints / 2.0)));
            if (bf.getStart() < 0) {
                bf.setStart(0);
            }
            if (bf.getEnd() > ts.getLength() - 1) {
                bf.setEnd(ts.getLength() - 1);
            }

            if (bf.getTop() > ts.getLength() - 1) {
                bf.setTop(ts.getLength() - 1);
            }

            piecewiseLinearTrend.add(bf);
            currentPoint += deltaForTriangle;
        }
        if (piecewiseLinearTrend.get(piecewiseLinearTrend.size() - 1).getEnd() != piecewiseLinearTrend.get(piecewiseLinearTrend.size() - 1).getTop()) {
            AComponent bf = new AComponent(piecewiseLinearTrend.get(piecewiseLinearTrend.size() - 1).getTop(),
                    piecewiseLinearTrend.get(piecewiseLinearTrend.size() - 1).getEnd(),
                    piecewiseLinearTrend.get(piecewiseLinearTrend.size() - 1).getEnd());
            piecewiseLinearTrend.add(bf);
        }
        return piecewiseLinearTrend;
    }

    @Override
    protected TimeSeries getForecastWithValidParams(Model model, TimeSeries forecast) {
        FTransformModel fTransformModel = (FTransformModel) model;
        for (int t = 1; t < forecast.getLength(); t++) {
            forecast.getValues().get(t).setValue(fTransformModel.getPiecewiseLinearTrend().getLastValue().getValue());
        }
        return forecast;
    }

    @Override
    public List<MethodParameter> getAvailableParameters() {
        return FTransformModel.getAvailableParameters();
    }

    @Override
    public String getName() {
        return "F - преобразование";
    }
}
