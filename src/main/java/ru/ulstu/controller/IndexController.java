package ru.ulstu.controller;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import ru.ulstu.DateUtils;
import ru.ulstu.datamodel.ChartForm;
import ru.ulstu.datamodel.ModelingResult;
import ru.ulstu.datamodel.exception.ModelingException;
import ru.ulstu.datamodel.ts.TimeSeries;
import ru.ulstu.datamodel.ts.TimeSeriesValue;
import ru.ulstu.db.DbService;
import ru.ulstu.service.TimeSeriesService;
import ru.ulstu.statistic.StatisticService;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
@Hidden
public class IndexController {
    private final TimeSeriesService timeSeriesService;
    private final DbService dbService;
    private final StatisticService statisticService;

    public IndexController(TimeSeriesService timeSeriesService,
                           DbService dbService,
                           StatisticService statisticService) {
        this.timeSeriesService = timeSeriesService;
        this.dbService = dbService;
        this.statisticService = statisticService;
    }

    @GetMapping("/")
    public String index(Model model) throws IOException {
        model.addAttribute("sets", dbService.getSets());
        model.addAttribute("chartForm", new ChartForm());
        return "index";
    }

    @GetMapping("chart")
    public String chart(@ModelAttribute ChartForm chartForm, Model model) throws IOException, ModelingException, ExecutionException, InterruptedException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        model.addAttribute("sets", dbService.getSets());
        if (chartForm.getSet() != null) {
            model.addAttribute("listTimeSeries", dbService.getTimeSeriesMeta(chartForm.getSet()));
        }
        if (chartForm.getTimeSeriesMeta() != null
                && chartForm.getTimeSeriesMeta().getKey() != null
                && !chartForm.getTimeSeriesMeta().getKey().isEmpty()) {
            addChartToModel(dbService.getTimeSeries(chartForm.getSet(), chartForm.getTimeSeriesMeta().getKey()),
                    null,
                    chartForm.isNeedForecast(),
                    model);
        }
        return "index";
    }

    private void addChartToModel(TimeSeries timeSeries, String method, boolean needForecast, Model model) throws ExecutionException, InterruptedException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, ModelingException {
        if (needForecast) {
            int countForecastPoints = timeSeries.getLength() > 20 ? 10 : timeSeries.getLength() / 3;
            TimeSeries timeSeriesModel;
            ModelingResult modelingResult;
            if (method == null) {
                timeSeriesModel = timeSeriesService.smoothTimeSeries(timeSeries).getTimeSeries();
                modelingResult = timeSeriesService.getForecast(timeSeries, countForecastPoints);
            } else {
                timeSeriesModel = timeSeriesService.smoothTimeSeries(timeSeries, method).getTimeSeries();
                modelingResult = timeSeriesService.getForecast(timeSeries, method, countForecastPoints);
            }
            TimeSeries forecast = modelingResult.getTimeSeries();
            TimeSeries testForecast = modelingResult.getTestForecast();
            // если временной ряд был сжат моделью, то для графика нужно вставить пустые значения
            TimeSeries modelWithSkips = new TimeSeries(timeSeriesModel.getKey());
            int j = 0;
            for (int i = 0; i < timeSeries.getLength(); i++) {
                if (timeSeries.getValue(i).getDate().equals(timeSeriesModel.getValue(j).getDate())) {
                    modelWithSkips.addValue(timeSeriesModel.getValue(j));
                    j++;
                } else {
                    modelWithSkips.addValue(new TimeSeriesValue((Double) null));
                }
            }
            model.addAttribute("model", modelWithSkips.getValues().stream().map(TimeSeriesValue::getValue).toArray());
            timeSeries.getValues().remove(timeSeries.getValues().size() - 1);

            List<Double> forecastValues = timeSeries.getValues().stream().map(v -> (Double) null).collect(Collectors.toList());
            forecastValues.addAll(forecast.getValues().stream().map(TimeSeriesValue::getValue).collect(Collectors.toList()));
            model.addAttribute("forecast", forecastValues.toArray());

            List<Double> testForecastValues = timeSeries.getValues()
                    .stream()
                    .skip(countForecastPoints)
                    .map(v -> (Double) null)
                    .collect(Collectors.toList());
            testForecastValues.addAll(testForecast.getValues().stream().map(TimeSeriesValue::getValue).collect(Collectors.toList()));
            model.addAttribute("testForecast", testForecastValues.toArray());
            model.addAttribute("forecastDescription", modelingResult);
            model.addAttribute("statistic", statisticService.getStatistic(timeSeries));
            model.addAttribute("dates", getDatesForChart(timeSeries, forecast));
        } else {
            model.addAttribute("dates", getDatesForChart(timeSeries, new TimeSeries()));
        }
        model.addAttribute("timeSeries", timeSeries.getValues().stream().map(TimeSeriesValue::getValue).toArray());
    }

    private List<String> getDatesForChart(TimeSeries timeSeries, TimeSeries forecast) {
        return Stream.concat(timeSeries.getValues().stream(), forecast.getValues().stream().skip(1))
                .map(TimeSeriesValue::getDate)
                .sorted()
                .map(date -> {
                    Date date1 = DateUtils.addYears(DateUtils.localDateTimeToDate(date), 10);
                    LocalDateTime ldt = date1.toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime();
                    return ldt.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT));
                })
                .collect(Collectors.toList());

    }

    @GetMapping("/method")
    public String method(Model model) throws IOException {
        model.addAttribute("sets", dbService.getSets());
        model.addAttribute("methods", timeSeriesService.getAvailableMethods());
        model.addAttribute("chartForm", new ChartForm());
        return "method";
    }

    @GetMapping("chartMethod")
    public String chartMethod(@ModelAttribute ChartForm chartForm, Model model) throws IOException, ModelingException, ExecutionException, InterruptedException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        model.addAttribute("sets", dbService.getSets());
        model.addAttribute("methods", timeSeriesService.getAvailableMethods());
        if (chartForm.getSet() != null && !chartForm.getSet().getKey().equals("")) {
            model.addAttribute("listTimeSeries", dbService.getTimeSeriesMeta(chartForm.getSet()));
        }
        if (chartForm.getTimeSeriesMeta() != null
                && chartForm.getTimeSeriesMeta().getKey() != null
                && !chartForm.getTimeSeriesMeta().getKey().isEmpty()
                && chartForm.getMethodClassName() != null
                && !chartForm.getMethodClassName().equals("")) {
            addChartToModel(dbService.getTimeSeries(chartForm.getSet(), chartForm.getTimeSeriesMeta().getKey()),
                    chartForm.getMethodClassName(),
                    chartForm.isNeedForecast(),
                    model);
        }
        return "method";
    }
}
