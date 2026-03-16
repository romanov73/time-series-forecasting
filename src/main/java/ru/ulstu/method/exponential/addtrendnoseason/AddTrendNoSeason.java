package ru.ulstu.method.exponential.addtrendnoseason;

import org.springframework.stereotype.Component;
import ru.ulstu.datamodel.Model;
import ru.ulstu.datamodel.ts.TimeSeries;
import ru.ulstu.method.Method;
import ru.ulstu.method.MethodParamValue;
import ru.ulstu.method.MethodParameter;

import java.util.List;

@Component
public class AddTrendNoSeason extends Method {

    @Override
    protected Model getModelOfValidTimeSeries(TimeSeries ts, List<MethodParamValue> parameters) {
        AddTrendNoSeasonModel model = new AddTrendNoSeasonModel(ts, parameters);
        List<Double> sComponent = model.getSmoothedComponent();
        List<Double> tComponent = model.getTrendComponent();
        sComponent.add(ts.getFirstValue().getValue());
        TimeSeries tsModel = model.getTimeSeriesModel();

        sComponent.add(ts.getFirstValue().getValue());
        tComponent.add(ts.getValue(1).getValue() - ts.getValue(0).getValue());
        tsModel.addValue(ts.getFirstValue());
        //выполняется проход модели по сглаживанию
        for (int t = 1; t < ts.getValues().size(); t++) {
            sComponent.add(model.getAlpha().getDoubleValue() * ts.getNumericValue(t)
                    + (1 - model.getAlpha().getDoubleValue())
                    * (sComponent.get(t - 1) - tComponent.get(t - 1)));

            tComponent.add(model.getBeta().getDoubleValue()
                    * (sComponent.get(t) - sComponent.get(t - 1))
                    + (1 - model.getBeta().getDoubleValue()) * tComponent.get(t - 1));
            tsModel.addValue(ts.getValues().get(t), sComponent.get(sComponent.size() - 1));
        }
        return model;
    }

    @Override
    protected TimeSeries getForecastWithValidParams(Model model, TimeSeries forecast) {
        AddTrendNoSeasonModel currentModel = (AddTrendNoSeasonModel) model;
        List<Double> sComponent = currentModel.getSmoothedComponent();
        List<Double> tComponent = currentModel.getTrendComponent();
        for (int t = 1; t < forecast.getLength(); t++) {
            forecast.getValues().get(t).setValue(sComponent.get(sComponent.size() - 1) + tComponent.get(tComponent.size() - 1) * t);
        }
        return forecast;
    }

    @Override
    public List<MethodParameter> getAvailableParameters() {
        return AddTrendNoSeasonModel.getAvailableParameters();
    }

    @Override
    public String getName() {
        return "Экспоненциальный метод с аддитивным трендом без сезонной компоненты (метод Хольта)";
    }
}
