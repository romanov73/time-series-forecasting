package ru.ulstu.method.fuzzy;

import org.springframework.stereotype.Component;
import ru.ulstu.datamodel.Model;
import ru.ulstu.datamodel.exception.ModelingException;
import ru.ulstu.datamodel.ts.TimeSeries;
import ru.ulstu.datamodel.ts.TimeSeriesValue;
import ru.ulstu.http.HttpService;
import ru.ulstu.method.Method;
import ru.ulstu.method.MethodParamValue;
import ru.ulstu.method.MethodParameter;

import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.List;

@Component
public class PlainFuzzy extends Method {
    private final HttpService httpService = new HttpService();

    @Override
    public List<MethodParameter> getAvailableParameters() {
        return PlainFuzzyModel.getAvailableParameters();
    }

    @Override
    protected Model getModelOfValidTimeSeries(TimeSeries timeSeries, List<MethodParamValue> parameters) {
        PlainFuzzyModel model = new PlainFuzzyModel(timeSeries, parameters);
        List<Triangle> fuzzySets = generateFuzzySets(timeSeries, model.getNumberOfFuzzyTerms().getIntValue());
        model.setFuzzySets(fuzzySets);
        for (TimeSeriesValue tsVal : timeSeries.getValues()) {
            model.getTimeSeriesModel().addValue(new TimeSeriesValue(
                    tsVal.getDate(),
                    getMaxMembership(fuzzySets, tsVal).getTop()));
            model.getFuzzyTimeSeries().add(getMaxMembership(fuzzySets, tsVal));
        }
        return model;
    }

    private List<Triangle> generateFuzzySets(TimeSeries timeSeries, Integer numberOfFuzzyTerms) {
        // Universum
        DoubleSummaryStatistics stat = timeSeries.getValues()
                .stream()
                .mapToDouble(TimeSeriesValue::getValue)
                .summaryStatistics();
        double min = stat.getMin();
        double max = stat.getMax();

        // Generate fuzzy sets
        List<Triangle> fuzzySets = new ArrayList<>();
        double delta = ((max - min) / (numberOfFuzzyTerms - 1));

        for (int i = 0; i < numberOfFuzzyTerms; i++) {
            fuzzySets.add(new Triangle(min + i * delta - delta,
                    min + i * delta,
                    min + i * delta + delta, i));
        }
        return fuzzySets;
    }

    private Triangle getMaxMembership(List<Triangle> fuzzySets, TimeSeriesValue tsVal) {
        Triangle maxTriangle = fuzzySets.get(0);
        double membersip = 0;
        for (Triangle triangle : fuzzySets) {
            if (membersip < triangle.getValueAtPoint(tsVal.getValue())) {
                maxTriangle = triangle;
                membersip = triangle.getValueAtPoint(tsVal.getValue());
            }
        }
        return maxTriangle;
    }

    @Override
    protected TimeSeries getForecastWithValidParams(Model model, TimeSeries forecast) throws ModelingException {
        PlainFuzzyModel pfm = ((PlainFuzzyModel) model);
        List<String> fuzzyTimeSeries = pfm.getFuzzyTimeSeries().stream().map(Triangle::getLabel).toList();
        List<OutputValue> result = httpService.post("http://plans.athene.tech/inferenceRest/get-inference-by-generated-rules",
                new FuzzyRuleDataDto(fuzzyTimeSeries.toArray(String[]::new), 2, forecast.getLength()));
        List<Double> forecastValues = result.stream().map(r -> pfm.getTopValueByLabel(r.getFuzzyTerm())).toList();
        List<TimeSeriesValue> values = forecast.getValues();
        for (int i = 0; i < values.size(); i++) {
            if (forecastValues.isEmpty()) {
                values.get(i).setValue(pfm.getTimeSeriesModel().getValues().getLast().getValue());
            } else {
                if (forecastValues.size() > i) {
                    values.get(i).setValue(forecastValues.get(i));
                } else {
                    values.get(i).setValue(forecastValues.getLast());
                }
            }
        }
        return forecast;
    }

    @Override
    public String getName() {
        return "Нечеткая модель";
    }
}
