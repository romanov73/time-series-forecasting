package ru.ulstu.method.exponential.notrendnoseason;

import org.springframework.stereotype.Component;
import ru.ulstu.datamodel.Model;
import ru.ulstu.datamodel.ts.TimeSeries;
import ru.ulstu.method.Method;
import ru.ulstu.method.MethodParamValue;
import ru.ulstu.method.MethodParameter;

import java.util.List;

@Component
public class NoTrendNoSeason extends Method {

    @Override
    protected NoTrendNoSeasonModel getModelOfValidTimeSeries(TimeSeries ts,
                                                             List<MethodParamValue> parameters) {
        NoTrendNoSeasonModel model = new NoTrendNoSeasonModel(ts, parameters);
        List<Double> sComponent = model.getSmoothedComponent();
        sComponent.add(ts.getFirstValue().getValue());
        TimeSeries tsModel = model.getTimeSeriesModel();
        tsModel.addValue(ts.getFirstValue());
        //выполняется проход модели по сглаживанию
        for (int t = 1; t < ts.getValues().size(); t++) {
            sComponent.add(sComponent.get(t - 1)
                    + model.getAlpha().getDoubleValue()
                    * (ts.getNumericValue(t) - sComponent.get(t - 1)));
            tsModel.addValue(ts.getValue(t), sComponent.get(sComponent.size() - 1));
        }
        return model;
    }

    @Override
    protected TimeSeries getForecastWithValidParams(Model model, TimeSeries forecast) {
        for (int t = 1; t < forecast.getLength(); t++) {
            forecast.getValues().get(t).setValue(forecast.getValues().get(t - 1).getValue());
        }
        return forecast;
    }

    @Override
    public List<MethodParameter> getAvailableParameters() {
        return NoTrendNoSeasonModel.getAvailableParameters();
    }

    @Override
    public String getName() {
        return "Экспоненциальный метод без трендовой и сезонной компоненты";
    }
}
