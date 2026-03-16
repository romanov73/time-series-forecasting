package ru.ulstu.score;

import ru.ulstu.datamodel.Score;
import ru.ulstu.datamodel.exception.ModelingException;
import ru.ulstu.datamodel.ts.TimeSeries;
import ru.ulstu.datamodel.ts.TimeSeriesValue;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

public abstract class ScoreMethod {
    private final String name;

    public ScoreMethod(String name) {
        this.name = name;
    }

    public Score getScore(Map<LocalDateTime, Double> tsValues, TimeSeries model) throws ModelingException {
        return new Score(this, evaluate(tsValues, model));
    }

    public abstract Number evaluate(Map<LocalDateTime, Double> tsValues, TimeSeries model) throws ModelingException;

    public String getName() {
        return name;
    }

    protected Double getValueOnSameDate(Map<LocalDateTime, Double> tsValues, TimeSeriesValue modelValue) throws ModelingException {
        return Optional.ofNullable(tsValues.get(modelValue.getDate()))
                .orElseThrow(() -> new ModelingException("Значение модельного ряда не найдено в оригинальном ряде: "
                        + modelValue.getDate()
                        + " " + tsValues));
    }
}
