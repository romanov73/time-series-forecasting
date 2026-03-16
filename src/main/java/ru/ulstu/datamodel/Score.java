package ru.ulstu.datamodel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ru.ulstu.score.ScoreMethod;

public class Score {
    private final ScoreMethod scoreMethod;
    private final Number value;

    public Score(ScoreMethod scoreMethod, Number value) {
        this.scoreMethod = scoreMethod;
        this.value = value;
    }

    public ScoreMethod getScoreMethod() {
        return scoreMethod;
    }

    public Number getValue() {
        return ((double) Math.round(value.doubleValue() * 100)) / 100;
    }

    @JsonIgnore
    public Double getDoubleValue() {
        return value.doubleValue();
    }
}
