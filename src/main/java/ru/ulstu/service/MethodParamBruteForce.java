package ru.ulstu.service;

import org.springframework.stereotype.Service;
import ru.ulstu.datamodel.Model;
import ru.ulstu.datamodel.ModelingResult;
import ru.ulstu.datamodel.exception.ModelingException;
import ru.ulstu.datamodel.ts.TimeSeries;
import ru.ulstu.datamodel.ts.TimeSeriesValue;
import ru.ulstu.method.Method;
import ru.ulstu.method.MethodParamValue;
import ru.ulstu.method.MethodParameter;
import ru.ulstu.method.ftransform.FTransform;
import ru.ulstu.score.ScoreMethod;
import ru.ulstu.score.Smape;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Service
class MethodParamBruteForce {
    private final int DEFAULT_THREAD_COUNT = 50;
    private final List<Method> methods;
    private final ScoreMethod scoreMethod = new Smape();
    private final ExecutorService executors = Executors.newCachedThreadPool();
    ;

    public MethodParamBruteForce(List<Method> methods) {
        this.methods = methods;
    }

    private ModelingResult getForecastByMethods(TimeSeries timeSeries, List<Method> methods, int countPointsForecast) throws ExecutionException, InterruptedException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, ModelingException {
        List<Future<ModelingResult>> futureModelingResults = new ArrayList<>();
        List<ModelingResult> modelingResults = new CopyOnWriteArrayList<>();
        final int countPoints = (countPointsForecast > timeSeries.getLength()) ? timeSeries.getLength() / 3 : countPointsForecast;
        TimeSeries reducedTimeSeries = new TimeSeries(timeSeries.getValues().stream().limit(timeSeries.getLength() - countPoints).collect(Collectors.toList()),
                "test part of " + timeSeries.getKey());

        try {
            ValidationUtils.validateTimeSeries(reducedTimeSeries);
        } catch (ModelingException ex) {
            throw new ModelingException("Тестовая часть временного ряда не прошла валидацию: " + ex.getMessage());
        }

        Map<LocalDateTime, Double> tsValues = timeSeries.getValues().stream()
                .collect(Collectors.toMap(TimeSeriesValue::getDate, TimeSeriesValue::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));

        for (Method method : methods) {
            List<List<MethodParamValue>> availableParametersValues = getAvailableParametersValues(timeSeries, method.getAvailableParameters());
            for (List<MethodParamValue> parametersValues : availableParametersValues) {
                Method methodInstance = method.getClass().getDeclaredConstructor().newInstance();
                if (methodInstance.canMakeForecast(reducedTimeSeries, parametersValues, countPoints)) {
                    futureModelingResults.add(executors.submit(() -> {
                        TimeSeries forecast = syncDates(methodInstance.getForecast(reducedTimeSeries, parametersValues, countPoints), timeSeries);
                        return new ModelingResult(forecast, null,
                                parametersValues,
                                scoreMethod.getScore(tsValues, forecast),
                                methodInstance);
                    }));
                }
            }
        }
        for (Future<ModelingResult> futureModelingResult : futureModelingResults) {
            modelingResults.add(futureModelingResult.get());
        }
        System.gc();
        return getBestResultForecast(modelingResults, timeSeries, countPoints);
    }

    public ModelingResult getForecast(TimeSeries timeSeries, String methodClassName, int countPointsForecast) throws ExecutionException, InterruptedException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, ModelingException {
        Method method = methods.stream()
                .filter(m -> m.getClass().getSimpleName().equals(methodClassName))
                .findAny()
                .orElseThrow(() -> new ModelingException("Неизвестный метод прогнозирования"));
        method.validateForForecast(timeSeries, countPointsForecast);
        return getForecastByMethods(timeSeries, List.of(method), countPointsForecast);
    }

    public ModelingResult getForecast(TimeSeries timeSeries, Method method, int countPointsForecast) throws ExecutionException, InterruptedException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, ModelingException {
        return getForecastByMethods(timeSeries, List.of(method), countPointsForecast);
    }

    public ModelingResult getForecast(TimeSeries timeSeries, int countPointsForecast) throws ExecutionException, InterruptedException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, ModelingException {
        return getForecastByMethods(timeSeries, methods, countPointsForecast);
    }

    private ModelingResult getBestResultForecast(List<ModelingResult> modelingResults,
                                                 TimeSeries timeSeries,
                                                 int countPoints) throws ModelingException {
        if (modelingResults.size() == 0) {
            throw new ModelingException("Нет результатов моделирования");
        }
        ModelingResult bestResult = modelingResults.stream()
                .min(Comparator.comparing(modelingResult -> modelingResult.getScore().getDoubleValue()))
                .orElseThrow(() -> new ModelingException("Лучший метод не найден"));

        TimeSeries forecast = bestResult.getTimeSeriesMethod().getForecast(timeSeries,
                bestResult.getParamValues(),
                countPoints);

        return new ModelingResult(forecast,
                bestResult.getTimeSeries(),
                bestResult.getParamValues(),
                bestResult.getScore(),
                bestResult.getTimeSeriesMethod());
    }

    private TimeSeries syncDates(TimeSeries forecast, TimeSeries timeSeries) {
        List<TimeSeriesValue> forecastValues = forecast.getValues();
        for (int i = 1; i <= forecastValues.size(); i++) {
            forecastValues.get(forecastValues.size() - i)
                    .setDate(timeSeries.getValues().get(timeSeries.getValues().size() - i).getDate());
        }
        return forecast;
    }

    /*
    TODO:
    public TimeSeries getForecastWithOptimalLength(TimeSeries timeSeries) {
        throw new RuntimeException("Not implemented");
    }
    */

    public ModelingResult getSmoothedTimeSeries(TimeSeries timeSeries, List<Method> methods) throws ExecutionException, InterruptedException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        List<Future<ModelingResult>> results = new ArrayList<>();
        List<ModelingResult> results2 = new CopyOnWriteArrayList<>();

        Map<LocalDateTime, Double> tsValues = timeSeries.getValues().stream()
                .collect(Collectors.toMap(TimeSeriesValue::getDate, TimeSeriesValue::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));

        for (Method method : methods) {
            List<List<MethodParamValue>> availableParametersValues = getAvailableParametersValues(timeSeries, method.getAvailableParameters());
            for (List<MethodParamValue> parametersValues : availableParametersValues) {
                Method methodInstance = method.getClass().getDeclaredConstructor().newInstance();
                if (methodInstance.canMakeModel(timeSeries, parametersValues)) {
                    results.add(executors.submit(() -> {
                        Model model = methodInstance.getModel(timeSeries, parametersValues);
                        return new ModelingResult(model.getTimeSeriesModel(),
                                null,
                                parametersValues,
                                scoreMethod.getScore(tsValues, model.getTimeSeriesModel()),
                                methodInstance);
                    }));
                }
            }
        }
        for (Future<ModelingResult> futureModelingResult : results) {
            results2.add(futureModelingResult.get());
        }
        System.gc();
        return results2.stream()
                .min(Comparator.comparing(modelingResult -> modelingResult.getScore().getDoubleValue()))
                .orElse(null);
    }

    public ModelingResult getMaxSmoothedTimeSeries(TimeSeries timeSeries) throws ExecutionException, InterruptedException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        List<Future<ModelingResult>> results = new ArrayList<>();
        List<ModelingResult> results2 = new CopyOnWriteArrayList<>();

        Map<LocalDateTime, Double> tsValues = timeSeries.getValues().stream()
                .collect(Collectors.toMap(TimeSeriesValue::getDate, TimeSeriesValue::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
        Method method = new FTransform();
        List<List<MethodParamValue>> availableParameterValues = getAvailableParametersValues(timeSeries, method.getAvailableParameters());
        List<MethodParamValue> parametersValue = availableParameterValues.get(availableParameterValues.size() - 1);
        Method methodInstance = method.getClass().getDeclaredConstructor().newInstance();
        if (methodInstance.canMakeModel(timeSeries, parametersValue)) {
            results.add(executors.submit(() -> {
                Model model = methodInstance.getModel(timeSeries, parametersValue);
                return new ModelingResult(model.getTimeSeriesModel(),
                        null,
                        parametersValue,
                        scoreMethod.getScore(tsValues, model.getTimeSeriesModel()),
                        methodInstance);
            }));

        }
        for (Future<ModelingResult> futureModelingResult : results) {
            results2.add(futureModelingResult.get());
        }
        System.gc();
        return results2.stream()
                .min(Comparator.comparing(modelingResult -> modelingResult.getScore().getDoubleValue()))
                .orElse(null);
    }

    public ModelingResult getSmoothedTimeSeries(TimeSeries timeSeries, String methodClassName) throws ExecutionException, InterruptedException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, ModelingException {
        Method method = methods.stream()
                .filter(m -> m.getClass().getSimpleName().equals(methodClassName))
                .findAny()
                .orElseThrow(() -> new ModelingException("Неизвестный метод прогнозирования"));
        return getSmoothedTimeSeries(timeSeries, List.of(method));
    }

    public ModelingResult getSmoothedTimeSeries(TimeSeries timeSeries) throws ExecutionException, InterruptedException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return getSmoothedTimeSeries(timeSeries, methods);
    }

    private List<List<MethodParamValue>> getAvailableParametersValues(TimeSeries timeSeries, List<MethodParameter> availableParameters) {
        List<List<MethodParamValue>> result = new ArrayList<>();
        Map<MethodParameter, Integer> parameterOffset = new TreeMap<>();
        Map<MethodParameter, List<Number>> parameterValues = new TreeMap<>();
        for (MethodParameter methodParameter : availableParameters) {
            parameterOffset.put(methodParameter, 0);
            parameterValues.put(methodParameter, methodParameter.getAvailableValues(timeSeries));
        }
        while (isNotAllParameterValuesUsed(parameterOffset, parameterValues)) {
            List<MethodParamValue> resultRow = new ArrayList<>();
            for (MethodParameter methodParameter : parameterOffset.keySet()) {
                resultRow.add(new MethodParamValue(methodParameter,
                        parameterValues.get(methodParameter).get(parameterOffset.get(methodParameter))));
            }
            incrementOffset(parameterOffset, parameterValues);
            result.add(resultRow);
        }
        return result;
    }

    private void incrementOffset(Map<MethodParameter, Integer> parameterOffset,
                                 Map<MethodParameter, List<Number>> parameterValues) {
        List<MethodParameter> parameters = new ArrayList<>(parameterOffset.keySet());
        int i = 0;
        while (i < parameters.size() && isNotAllParameterValuesUsed(parameterOffset, parameterValues)) {
            if (parameterOffset.get(parameters.get(i)) == parameterValues.get(parameters.get(i)).size() - 1) {
                parameterOffset.put(parameters.get(i), 0);
                i++;
                continue;
            }
            if (parameterOffset.get(parameters.get(i)) < parameterValues.get(parameters.get(i)).size()) {
                parameterOffset.put(parameters.get(i), parameterOffset.get(parameters.get(i)) + 1);
                return;
            }
            i++;
        }
    }

    private boolean isNotAllParameterValuesUsed(Map<MethodParameter, Integer> parameterOffset,
                                                Map<MethodParameter, List<Number>> parameterValues) {
        for (MethodParameter methodParameter : parameterOffset.keySet()) {
            if (parameterOffset.get(methodParameter) != parameterValues.get(methodParameter).size() - 1) {
                return true;
            }
        }
        return false;
    }

    public List<Method> getAvailableMethods() {
        return methods;
    }
}
