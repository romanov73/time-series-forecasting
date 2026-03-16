package ru.ulstu.method;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ru.ulstu.TimeSeriesUtils;
import ru.ulstu.datamodel.Model;
import ru.ulstu.datamodel.exception.ForecastValidateException;
import ru.ulstu.datamodel.exception.ModelingException;
import ru.ulstu.datamodel.ts.TimeSeries;
import ru.ulstu.datamodel.ts.TimeSeriesValue;
import ru.ulstu.service.ValidationUtils;

import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Наиболее общая логика моделирования и прогнозирования временных рядов
 */
public abstract class Method {
    @JsonIgnore
    public abstract List<MethodParameter> getAvailableParameters();

    /**
     * Возвращает модельное представление валидного временного ряда: для тех же точек времени что и в параметре timeSeries
     * строится модель. Количество точек может быть изменено: сокращено при сжатии ряда, увеличено при интерполяции.
     *
     * @return модель временного ряда
     */
    protected abstract Model getModelOfValidTimeSeries(TimeSeries timeSeries, List<MethodParamValue> parameters);

    /**
     * Возвращает модельное представление временного ряда: для тех же точек времени что и в параметре timeSeries
     * строится модель. Количество точек может быть изменено: сокращено при сжатии ряда, увеличено при интерполяции.
     * Метод является шаблонным, выполняет операции валидации исходного ряда и потом его моделирование
     * <p>
     * return модельное представление временного ряда
     *
     * @throws ModelingException генерируется, если есть проблемы моделирования при задании параметров
     */
    public Model getModel(TimeSeries timeSeries, List<MethodParamValue> parameters) throws ModelingException {
        ValidationUtils.validateTimeSeries(timeSeries);
        validateAdditionalParams(timeSeries, parameters);
        return getModelOfValidTimeSeries(timeSeries, parameters);
    }

    /**
     * Выполняет построение прогноза временного ряда. Даты спрогнозированных точек будут сгенерированы по модельным точкам.
     *
     * @param model    модель временного ряда, включающая все нужные компоненты
     * @param forecast заготовка временного ряда для прогноза с датами и нужным количеством точек для прогнозирования
     * @return прогноз временного ряда
     */
    protected abstract TimeSeries getForecastWithValidParams(Model model, TimeSeries forecast) throws ModelingException;

    public boolean canMakeForecast(TimeSeries timeSeries, List<MethodParamValue> parameters, int countPoints) {
        try {
            ValidationUtils.validateTimeSeries(timeSeries);
            validateAdditionalParams(timeSeries, parameters);
            validateForecastParams(countPoints);
        } catch (ModelingException ex) {
            return false;
        }
        return true;
    }

    public void validateForForecast(TimeSeries timeSeries, int countPoints) throws ModelingException {
        ValidationUtils.validateTimeSeries(timeSeries);
        validateForecastParams(countPoints);
    }

    public boolean canMakeModel(TimeSeries timeSeries, List<MethodParamValue> parameters) {
        try {
            ValidationUtils.validateTimeSeries(timeSeries);
            validateAdditionalParams(timeSeries, parameters);
        } catch (ModelingException ex) {
            return false;
        }
        return true;
    }

    /**
     * Выполняет построение модели и прогноза временного ряда. Даты спрогнозированных точек будут сгенерированы
     * по модельным точкам.
     *
     * @param countPoints количество точек для прогнозирования
     * @return прогноз временного ряда
     */
    public TimeSeries getForecast(TimeSeries timeSeries,
                                  List<MethodParamValue> parameters,
                                  int countPoints) throws ModelingException {
        validateForecastParams(countPoints);
        Model model = getModel(timeSeries, parameters);
        TimeSeries forecast = generateEmptyForecastPoints(model.getTimeSeriesModel(), countPoints);
        forecast.getFirstValue().setValue(model.getTimeSeriesModel().getLastValue().getValue());
        forecast = getForecastWithValidParams(model, forecast);
        forecast.getFirstValue().setValue(timeSeries.getLastValue().getValue());
        return forecast;
    }

    protected TimeSeries generateEmptyForecastPoints(TimeSeries model, int countPointForecast) {
        long diffMilliseconds = TimeSeriesUtils.getTimeDifferenceInMilliseconds(model);
        TimeSeries forecast = new TimeSeries("Forecast of " + model.getKey());
        forecast.addValue(new TimeSeriesValue(model.getLastValue().getDate()));
        for (int i = 1; i < countPointForecast + 1; i++) {
            forecast.addValue(new TimeSeriesValue(forecast.getValues().get(i - 1).getDate().plus(diffMilliseconds, ChronoUnit.MILLIS)));
        }
        return forecast;
    }

    private void validateForecastParams(int countPoints) throws ForecastValidateException {
        if (countPoints < 1) {
            throw new ForecastValidateException("Количество прогнозных точек должно быть больше 0");
        }
    }

    protected void validateAdditionalParams(TimeSeries timeSeries, List<MethodParamValue> parameters) throws ModelingException {

    }

    @Override
    public String toString() {
        return getName();
    }

    public String getKey() {
        return getClass().getSimpleName();
    }

    public abstract String getName();
}
