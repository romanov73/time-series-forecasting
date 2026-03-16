package ru.ulstu.method.exponential.addtrendaddseason;

import org.springframework.stereotype.Component;
import ru.ulstu.datamodel.Model;
import ru.ulstu.datamodel.exception.ModelingException;
import ru.ulstu.datamodel.ts.TimeSeries;
import ru.ulstu.method.Method;
import ru.ulstu.method.MethodParamValue;
import ru.ulstu.method.MethodParameter;
import ru.ulstu.method.exponential.parameter.Season;

import java.util.List;

@Component
public class AddTrendAddSeason extends Method {

    @Override
    protected Model getModelOfValidTimeSeries(TimeSeries ts, List<MethodParamValue> parameters) {
        AddTrendAddSeasonModel model = new AddTrendAddSeasonModel(ts, parameters);
        List<Double> sComponent = model.getSmoothedComponent();
        List<Double> tComponent = model.getTrendComponent();
        List<Double> iComponent = model.getSeasonComponent();
        sComponent.add(ts.getFirstValue().getValue());
        TimeSeries tsModel = model.getTimeSeriesModel();

        iComponent.add(1.0);
        tComponent.add(0.0);
        tsModel.addValue(ts.getFirstValue());
        //выполняется проход модели по сглаживанию
        for (int t = 1; t < model.getSeason().getValue().intValue(); t++) {
            sComponent.add(model.getAlpha().getDoubleValue() * ts.getNumericValue(t)
                    + (1 - model.getAlpha().getDoubleValue())
                    * (sComponent.get(t - 1) + tComponent.get(t - 1)));
            tComponent.add(model.getBeta().getDoubleValue()
                    * (sComponent.get(t) - sComponent.get(t - 1))
                    + (1 - model.getBeta().getDoubleValue()) * tComponent.get(t - 1));
            iComponent.add(model.getGamma().getDoubleValue() * ts.getNumericValue(t) / sComponent.get(sComponent.size() - 1)
                    + (1 - model.getGamma().getDoubleValue()) * iComponent.get(0));
            tsModel.addValue(ts.getValues().get(t), sComponent.get(sComponent.size() - 1));
        }
        for (int t = model.getSeason().getIntValue(); t < ts.getValues().size(); t++) {
            sComponent.add(model.getAlpha().getDoubleValue() * ts.getNumericValue(t)
                    / iComponent.get(t - model.getSeason().getIntValue())
                    + (1 - model.getAlpha().getDoubleValue())
                    * (sComponent.get(t - 1) + tComponent.get(t - 1)));

            tComponent.add(model.getBeta().getDoubleValue()
                    * (sComponent.get(t) - sComponent.get(t - 1))
                    + (1 - model.getBeta().getDoubleValue()) * tComponent.get(t - 1));

            iComponent.add(model.getGamma().getDoubleValue() * ts.getNumericValue(t) / sComponent.get(sComponent.size() - 1)
                    + (1 - model.getGamma().getDoubleValue()) * iComponent.get(t - model.getSeason().getIntValue()));
            tsModel.addValue(ts.getValue(t), sComponent.get(sComponent.size() - 1));
        }
        return model;
    }

    @Override
    protected void validateAdditionalParams(TimeSeries ts, List<MethodParamValue> parameters) throws ModelingException {
        for (MethodParamValue parameter : parameters) {
            if (parameter.getParameter() instanceof Season) {
                if (ts.getLength() < parameter.getValue().intValue()) {
                    throw new ModelingException("Период больше чем длина ряда");
                }
            }
        }
    }

    @Override
    protected TimeSeries getForecastWithValidParams(Model model, TimeSeries forecast) {
        AddTrendAddSeasonModel currentModel = (AddTrendAddSeasonModel) model;
        List<Double> sComponent = currentModel.getSmoothedComponent();
        List<Double> tComponent = currentModel.getTrendComponent();
        List<Double> iComponent = currentModel.getSeasonComponent();
        for (int t = 1; t < forecast.getLength(); t++) {
            iComponent.add(currentModel.getGamma().getDoubleValue() * forecast.getNumericValue(t - 1) / sComponent.get(sComponent.size() - 1)
                    + (1 - currentModel.getGamma().getDoubleValue()) * iComponent.get(t + model.getTimeSeriesModel().getLength() - currentModel.getSeason().getIntValue() - 1));

            forecast.getValues().get(t).setValue((sComponent.get(sComponent.size() - 1) + tComponent.get(tComponent.size() - 1) * t)
                    * iComponent.get(t + model.getTimeSeriesModel().getLength() - currentModel.getSeason().getIntValue() - 1));
        }
        return forecast;
    }

    @Override
    public List<MethodParameter> getAvailableParameters() {
        return AddTrendAddSeasonModel.getAvailableParameters();
    }

    @Override
    public String getName() {
        return "Экспоненциальный метод с аддитивным трендом и аддитивной сезонностью (метод Хольта-Уинтерса)";
    }
}
